package com.tdev.popay.controller

import com.tdev.popay.dto.ResponseMessage
import com.tdev.popay.model.CreditHistory
import com.tdev.popay.service.CardService
import com.tdev.popay.service.CreditHistoryService
import com.tdev.popay.service.TokenService
import com.tdev.popay.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.*
import java.util.*
import jakarta.validation.Valid

@RestController
@RequestMapping("/api")
class CreditHistoryController(
    private val creditHistoryService: CreditHistoryService,
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

    @PostMapping("/credit_history/{card_nfc_id}")
    fun createCreditHistory(
        @RequestHeader("Authorization") token: String,
        @PathVariable("card_nfc_id") cardNfcId: String,
        @Valid @RequestBody creditHistory: CreditHistory
    ): ResponseEntity<Any> {
        val userId = tokenService.getUserIdFromToken(token)
        if (userId != null) {
            val checkUser = userService.findById(userId)
            val checkCard = cardService.findByNfcId(cardNfcId)
            if (checkUser != null && checkCard != null) {
                if (checkCard.totalAmount <= creditHistory.amount) {
                    return ResponseEntity(ResponseMessage(false, "Not enough money in this card"), HttpStatus.BAD_REQUEST)
                }
                val debitedCard = checkCard.copy(totalAmount = checkCard.totalAmount - creditHistory.amount)
                val creditedUser = checkUser.copy(wallet = checkUser.wallet.plus(creditHistory.amount))
                val newCreditHistory = CreditHistory(
                    user = checkUser,
                    amount = creditHistory.amount,
                    card = checkCard,
                )
                cardService.save(debitedCard)
                creditHistoryService.save(newCreditHistory)
                userService.save(creditedUser)
                return ResponseEntity(ResponseMessage(true, "Credit history created successfully"), HttpStatus.CREATED)
            }
            return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.BAD_REQUEST)
    }

    @GetMapping("/credit_histories")
    fun getCreditHistories(
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<Any> {
        val userId = tokenService.getUserIdFromToken(token)
        if (userId != null) {
            val checkUser = userService.findById(userId)
            if (checkUser != null) {
                val creditHistories = creditHistoryService.findAllByUserId(userId)
                return ResponseEntity(creditHistories, HttpStatus.OK)
            }
            return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.BAD_REQUEST)
    }

    @GetMapping("/credit_history/{id}")
    fun getCreditHistoryById(
        @RequestHeader("Authorization") token: String,
        @PathVariable(value = "id") creditHistoryId: Long
    ): ResponseEntity<Any> {
        val userId = tokenService.getUserIdFromToken(token)
        if (userId != null) {
            val checkUser = userService.findById(userId)
            if (checkUser != null) {
                val creditHistory = creditHistoryService.findOneByUserId(creditHistoryId, userId)
                if (creditHistory != null) {
                    return ResponseEntity(creditHistory, HttpStatus.OK)
                }
                return ResponseEntity(ResponseMessage(false, "Credit history not found"), HttpStatus.BAD_REQUEST)
            }
            return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity(ResponseMessage(false, "Credit history not found"), HttpStatus.BAD_REQUEST)
    }
}
