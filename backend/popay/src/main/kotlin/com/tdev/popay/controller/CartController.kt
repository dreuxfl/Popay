package com.tdev.popay.controller

import com.tdev.popay.dtos.ResponseMessage
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

    @PostMapping("/cart/{user_id}")
    fun createCart(@PathVariable(value = "user_id") userId: Long,
                   @Valid @RequestBody cart: Cart): ResponseEntity<Any> {
        val checkUser = userRepository.findById(userId)
        if (checkUser.isPresent) {
            val user = checkUser.get()
            val newCart = Cart(
                user = user,
                total_amount = cart.total_amount
            )
            cartRepository.save(newCart)
            return ResponseEntity(ResponseMessage(true, "Cart created successfully"), HttpStatus.CREATED)
        }
        return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.BAD_REQUEST)
    }

    @GetMapping("/carts")
    fun getAllCarts(): List<Cart> =
        cartRepository.findAll()

    @GetMapping("/cart/{id}")
    fun getCartById(@PathVariable(value = "id") cartId: Long): ResponseEntity<Any> {
        val checkCart = cartRepository.findById(cartId)
        if (checkCart.isPresent) {
            return ResponseEntity(checkCart.get(), HttpStatus.OK)
        }
        return ResponseEntity(ResponseMessage(false, "Cart not found"), HttpStatus.BAD_REQUEST)
    }

    @PutMapping("/cart/{id}")
    fun updateCartById(@PathVariable(value = "id") cartId: Long,
                       @Valid @RequestBody newCart: Cart): ResponseEntity<Any> {
        val checkCart = cartRepository.findById(cartId)
        if (checkCart.isPresent) {
            val cart = checkCart.get().copy(
                total_amount = newCart.total_amount
            )
            cartRepository.save(cart)
            return ResponseEntity(ResponseMessage(true, "Cart updated successfully"), HttpStatus.OK)
        }
        return ResponseEntity(ResponseMessage(false, "Cart not found"), HttpStatus.BAD_REQUEST)
    }

    @DeleteMapping("/cart/{id}")
    fun deleteCartById(@PathVariable(value = "id") cartId: Long): ResponseEntity<Any> {
        val checkCart = cartRepository.findById(cartId)
        if (checkCart.isPresent) {
            cartRepository.deleteById(cartId)
            return ResponseEntity(ResponseMessage(true, "Cart deleted successfully"), HttpStatus.OK)
        }
        return ResponseEntity(ResponseMessage(false, "Cart not found"), HttpStatus.BAD_REQUEST)
    }
}
