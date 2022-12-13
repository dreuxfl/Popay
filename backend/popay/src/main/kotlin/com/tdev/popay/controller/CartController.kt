package com.tdev.popay.controller

import com.tdev.popay.dto.ResponseMessage
import com.tdev.popay.model.Cart
import com.tdev.popay.service.CartService
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
class CartController(
    private val cartService: CartService,
    private val tokenService: TokenService,
    private val userService: UserService
    ) {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, String>> {
        val errors: MutableMap<String, String> = HashMap()
        ex.bindingResult.fieldErrors.forEach { error -> errors[error.field] = error.defaultMessage!! }
        return ResponseEntity(errors, HttpStatus.BAD_REQUEST)
    }

    @PostMapping("/cart")
    fun createCart(
        @RequestHeader("Authorization") token: String,
        @Valid @RequestBody cart: Cart
    ): ResponseEntity<Any> {
        val userId = tokenService.getUserIdFromToken(token)
        if (userId != null) {
            val checkUser = userService.findById(userId)
            if (checkUser != null) {
                val newCart = Cart(
                    user = checkUser
                )
                cartService.save(newCart)
                return ResponseEntity(ResponseMessage(true, "Cart created successfully"), HttpStatus.CREATED)
            }
            return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.BAD_REQUEST)
    }

    @GetMapping("/cart")
    fun getCurrentCart(@RequestHeader("Authorization") token: String): ResponseEntity<Any> {
        val userId = tokenService.getUserIdFromToken(token)
        if (userId != null) {
            val checkUser = userService.findById(userId)
            if (checkUser != null) {
                val creditHistories = cartService.findCurrentCartByUserId(userId)
                return ResponseEntity(creditHistories, HttpStatus.OK)
            }
            return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.BAD_REQUEST)
    }

    @GetMapping("/carts")
    fun getCarts(@RequestHeader("Authorization") token: String): ResponseEntity<Any> {
        val userId = tokenService.getUserIdFromToken(token)
        if (userId != null) {
            val checkUser = userService.findById(userId)
            if (checkUser != null) {
                val creditHistories = cartService.findAllPayedCartsByUserId(userId)
                return ResponseEntity(creditHistories, HttpStatus.OK)
            }
            return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.BAD_REQUEST)
    }

    @GetMapping("/cart/{id}")
    fun getCartById(
        @RequestHeader("Authorization") token: String,
        @PathVariable("id") id: Long
    ): ResponseEntity<Any> {
        val userId = tokenService.getUserIdFromToken(token)
        if (userId != null) {
            val checkUser = userService.findById(userId)
            if (checkUser != null) {
                val creditHistories = cartService.findOnePayedCartsByUserId(id, userId)
                return ResponseEntity(creditHistories, HttpStatus.OK)
            }
            return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.BAD_REQUEST)
    }
}
