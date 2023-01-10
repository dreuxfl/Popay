package com.tdev.popay.controller

import com.tdev.popay.dto.CardDto
import com.tdev.popay.dto.ResponseMessage
import com.tdev.popay.model.Card
import com.tdev.popay.service.CardService
import com.tdev.popay.service.TokenService
import com.tdev.popay.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.*
import java.util.HashMap

@RestController
@RequestMapping("/api")
class CardController(
    private val cardService: CardService,
    private val tokenService: TokenService,
    private val userService: UserService
) {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, String>> {
        val errors: MutableMap<String, String> = HashMap()
        ex.bindingResult.fieldErrors.forEach { error -> errors[error.field] = error.defaultMessage!! }
        return ResponseEntity(errors, HttpStatus.BAD_REQUEST)
    }

    @PostMapping("/card")
    fun createCard(
        @RequestHeader("Authorization") token: String,
        @Valid @RequestBody card: Card
    ): ResponseEntity<Any> {
        val userId = tokenService.getUserIdFromToken(token)
        if (userId != null) {
            val checkUser = userService.findById(userId)
            if (checkUser != null) {
                if (!cardService.existsByNfcId(card.nfcId)) {
                    val newCard = Card(
                        nfcId = card.nfcId,
                        totalAmount = card.totalAmount,
                    )
                    cardService.save(newCard)
                    return ResponseEntity(ResponseMessage(true, "Card created successfully"), HttpStatus.CREATED)
                }
                return ResponseEntity(ResponseMessage(false, "Card already exists"), HttpStatus.BAD_REQUEST)
            }
            return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.BAD_REQUEST)
    }

    @GetMapping("/card/{nfcId}")
    fun getCardByNfcId(
        @RequestHeader("Authorization") token: String,
        @PathVariable nfcId: String
    ): ResponseEntity<Any> {
        val userId = tokenService.getUserIdFromToken(token)
        if (userId != null) {
            val checkUser = userService.findById(userId)
            if (checkUser != null) {
                val checkCard = cardService.findByNfcId(nfcId)
                if (checkCard != null) {
                    return ResponseEntity(checkCard.totalAmount, HttpStatus.OK)
                }
                return ResponseEntity(ResponseMessage(false, "Card not found"), HttpStatus.BAD_REQUEST)
            }
            return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.BAD_REQUEST)
    }

    @PutMapping("/card/{nfcId}")
    fun updateCardByNfcId(
        @RequestHeader("Authorization") token: String,
        @PathVariable nfcId: String,
        @Valid @RequestBody cardDto: CardDto
    ): ResponseEntity<Any> {
        val userId = tokenService.getUserIdFromToken(token)
        if (userId != null) {
            val checkUser = userService.findById(userId)
            if (checkUser != null) {
                val checkCard = cardService.findByNfcId(nfcId)
                if (checkCard != null) {
                    val newCard = checkCard.copy(
                        totalAmount = checkCard.totalAmount + cardDto.totalAmount,
                    )
                    cardService.save(newCard)
                    return ResponseEntity(ResponseMessage(true, "Card updated successfully"), HttpStatus.OK)
                }
                return ResponseEntity(ResponseMessage(false, "Card not found"), HttpStatus.BAD_REQUEST)
            }
            return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.BAD_REQUEST)
    }
}
