package com.tdev.popay.controller

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

    @GetMapping("/credit_histories")
    fun getAllCreditHistories(): List<CreditHistory> =
            creditHistoryRepository.findAll()

    @PostMapping("/credit_history/{user_id}")
    fun createCreditHistory(@PathVariable(value = "user_id") userId: Long,
                            @Valid @RequestBody creditHistory: CreditHistory): ResponseEntity<CreditHistory> {
        return userRepository.findById(userId).map { user ->
            val newCreditHistory = creditHistory
                .copy(
                    user = user
                )
            ResponseEntity.ok().body(creditHistoryRepository.save(newCreditHistory))
        }.orElse(ResponseEntity.notFound().build())
    }

    @GetMapping("/credit_history/{id}")
    fun getCreditHistoryById(@PathVariable(value = "id") creditHistoryId: Long): ResponseEntity<CreditHistory> {
        return creditHistoryRepository.findById(creditHistoryId).map { creditHistory ->
            ResponseEntity.ok(creditHistory)
        }.orElse(ResponseEntity.notFound().build())
    }

    @PutMapping("/credit_history/{id}")
    fun updateCreditHistoryById(@PathVariable(value = "id") creditHistoryId: Long,
                        @Valid @RequestBody newCreditHistory: CreditHistory): ResponseEntity<CreditHistory> {

        return creditHistoryRepository.findById(creditHistoryId).map { existingCreditHistory ->
            val updatedCreditHistory: CreditHistory = existingCreditHistory
                    .copy(
                        amount = newCreditHistory.amount,
                        transaction_date = newCreditHistory.transaction_date
                    )
            ResponseEntity.ok().body(creditHistoryRepository.save(updatedCreditHistory))
        }.orElse(ResponseEntity.notFound().build())

    }

    @DeleteMapping("/credit_history/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun removeCreditHistoryById(@PathVariable(value = "id") creditHistoryId: Long): ResponseEntity<Void> {

        return creditHistoryRepository.findById(creditHistoryId).map { creditHistory  ->
            creditHistoryRepository.delete(creditHistory)
            ResponseEntity<Void>(HttpStatus.OK)
        }.orElse(ResponseEntity.notFound().build())

    }
}
