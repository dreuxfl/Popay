package com.tdev.popay.controller

import com.tdev.popay.model.Cart
import com.tdev.popay.repository.CartRepository
import com.tdev.popay.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.Valid

@RestController
@RequestMapping("/api")
class CartController(
    private val cartRepository: CartRepository,
    private val userRepository: UserRepository
    ) {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, String>> {
        val errors: MutableMap<String, String> = HashMap()
        ex.bindingResult.fieldErrors.forEach { error -> errors[error.field] = error.defaultMessage!! }
        return ResponseEntity(errors, HttpStatus.BAD_REQUEST)
    }

    @GetMapping("/carts")
    fun getAllCarts(): List<Cart> =
            cartRepository.findAll()

    @PostMapping("/cart/{user_id}")
    fun createCart(@PathVariable(value = "user_id") userId: Long,
                   @Valid @RequestBody cart: Cart): ResponseEntity<Cart> {
        return userRepository.findById(userId).map { user ->
            val newCart = cart
                .copy(
                    user = user
                )
            ResponseEntity.ok().body(cartRepository.save(newCart))
        }.orElse(ResponseEntity.notFound().build())
    }

    @GetMapping("/cart/{id}")
    fun getCartById(@PathVariable(value = "id") cartId: Long): ResponseEntity<Cart> {
        return cartRepository.findById(cartId).map { cart ->
            ResponseEntity.ok(cart)
        }.orElse(ResponseEntity.notFound().build())
    }

    @PutMapping("/cart/{id}")
    fun updateCartById(@PathVariable(value = "id") cartId: Long,
                       @Valid @RequestBody newCart: Cart): ResponseEntity<Cart> {
        return cartRepository.findById(cartId).map { existingCart ->
            val updatedCart: Cart = existingCart
                    .copy(
                        total_amount = newCart.total_amount,
                        payment_date = newCart.payment_date
                    )
            ResponseEntity.ok().body(cartRepository.save(updatedCart))
        }.orElse(ResponseEntity.notFound().build())
    }

    @DeleteMapping("/cart/{id}")
    fun deleteCartById(@PathVariable(value = "id") cartId: Long): ResponseEntity<Void> {
        return cartRepository.findById(cartId).map { cart  ->
            cartRepository.delete(cart)
            ResponseEntity<Void>(HttpStatus.OK)
        }.orElse(ResponseEntity.notFound().build())
    }
}
