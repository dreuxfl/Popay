package com.tdev.popay.controller

import com.tdev.popay.dto.ResponseMessage
import com.tdev.popay.model.CreditHistory
import com.tdev.popay.repository.CreditHistoryRepository
import com.tdev.popay.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.Valid

@RestController
@RequestMapping("/api")
class CreditHistoryController(
    private val creditHistoryRepository: CreditHistoryRepository,
    private val userRepository: UserRepository
    ) {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, String>> {
        val errors: MutableMap<String, String> = HashMap()
        ex.bindingResult.fieldErrors.forEach { error -> errors[error.field] = error.defaultMessage!! }
        return ResponseEntity(errors, HttpStatus.BAD_REQUEST)
    }

    @PostMapping("/credit_history/{user_id}")
    fun createCreditHistory(@PathVariable(value = "user_id") userId: Long,
                            @Valid @RequestBody creditHistory: CreditHistory): ResponseEntity<Any> {
        val checkUser = userRepository.findById(userId)
        if (checkUser.isPresent) {
            val user = checkUser.get()
            val newCreditHistory = CreditHistory(
                user = user,
                amount = creditHistory.amount
            )
            creditHistoryRepository.save(newCreditHistory)
            return ResponseEntity(ResponseMessage(true, "Credit history created successfully"), HttpStatus.CREATED)
        }
        return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.BAD_REQUEST)
    }

    @GetMapping("/credit_histories")
    fun getAllCreditHistories(): List<CreditHistory> =
        creditHistoryRepository.findAll()

    @GetMapping("/credit_history/{id}")
    fun getCreditHistoryById(@PathVariable(value = "id") creditHistoryId: Long): ResponseEntity<Any> {
        val checkCreditHistory = creditHistoryRepository.findById(creditHistoryId)
        if (checkCreditHistory.isPresent) {
            return ResponseEntity(checkCreditHistory.get(), HttpStatus.OK)
        }
        return ResponseEntity(ResponseMessage(false, "Credit history not found"), HttpStatus.BAD_REQUEST)
    }

    @PutMapping("/credit_history/{id}")
    fun updateCreditHistoryById(@PathVariable(value = "id") creditHistoryId: Long,
                        @Valid @RequestBody newCreditHistory: CreditHistory): ResponseEntity<Any> {
        val checkCreditHistory = creditHistoryRepository.findById(creditHistoryId)
        if (checkCreditHistory.isPresent) {
            val creditHistory = checkCreditHistory.get().copy(
                amount = newCreditHistory.amount
            )
            creditHistoryRepository.save(creditHistory)
            return ResponseEntity(ResponseMessage(true, "Credit history updated successfully"), HttpStatus.OK)
        }
        return ResponseEntity(ResponseMessage(false, "Credit history not found"), HttpStatus.BAD_REQUEST)
    }

    @DeleteMapping("/credit_history/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun removeCreditHistoryById(@PathVariable(value = "id") creditHistoryId: Long): ResponseEntity<Any> {
        val checkCreditHistory = creditHistoryRepository.findById(creditHistoryId)
        if (checkCreditHistory.isPresent) {
            creditHistoryRepository.deleteById(creditHistoryId)
            return ResponseEntity(ResponseMessage(true, "Credit history deleted successfully"), HttpStatus.OK)
        }
        return ResponseEntity(ResponseMessage(false, "Credit history not found"), HttpStatus.BAD_REQUEST)
    }
}
