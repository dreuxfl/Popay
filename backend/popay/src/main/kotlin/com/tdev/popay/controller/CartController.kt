package com.tdev.popay.controller

import com.tdev.popay.dto.ResponseMessage
import com.tdev.popay.model.Cart
import com.tdev.popay.service.CartProductService
import com.tdev.popay.service.CartService
import com.tdev.popay.service.TokenService
import com.tdev.popay.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.*
import java.util.*
import jakarta.validation.Valid
import java.time.LocalDateTime

@RestController
@RequestMapping("/api")
class CartController(
    private val cartService: CartService,
    private val cartProductService: CartProductService,
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
                val cart = cartService.findCurrentCartByUserId(userId)
                if (cart != null) {
                    val cartProducts = cartProductService.findAllByCartId(cart.id)
                    for (cartProduct in cartProducts) {
                        cart.totalAmount = cart.totalAmount.plus((cartProduct.product?.price ?: 0.0) * cartProduct.quantity)
                    }
                }
                return ResponseEntity(cart, HttpStatus.OK)
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
                val carts = cartService.findAllPayedCartsByUserId(userId)
                for (cart in carts) {
                    val cartProducts = cartProductService.findAllByCartId(cart.id)
                    for (cartProduct in cartProducts) {
                        cart.totalAmount = cart.totalAmount.plus((cartProduct.product?.price ?: 0.0) * cartProduct.quantity)
                    }
                }
                return ResponseEntity(carts, HttpStatus.OK)
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
                val cart = cartService.findOnePayedCartsByUserId(userId, id)
                if (cart != null) {
                    val cartProducts = cartProductService.findAllByCartId(cart.id)
                    for (cartProduct in cartProducts) {
                        cart.totalAmount = cart.totalAmount.plus((cartProduct.product?.price ?: 0.0) * cartProduct.quantity)
                    }
                }
                return ResponseEntity(cart, HttpStatus.OK)
            }
            return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.BAD_REQUEST)
    }

    @PostMapping("/cart/validate")
    fun validateCart(@RequestHeader("Authorization") token: String): ResponseEntity<Any> {
        val userId = tokenService.getUserIdFromToken(token)
        if (userId != null) {
            val checkUser = userService.findById(userId)
            if (checkUser != null) {
                val cart = cartService.findCurrentCartByUserId(userId)
                if (cart != null) {
                    val cartProducts = cartProductService.findAllByCartId(cart.id)
                    if (cartProducts.isNotEmpty()) {
                        for (cartProduct in cartProducts) {
                            cart.totalAmount = cart.totalAmount.plus((cartProduct.product?.price ?: 0.0) * cartProduct.quantity)
                            if ((cartProduct.product?.stock ?: 0) < cartProduct.quantity) {
                                return ResponseEntity(ResponseMessage(false, "There is not enough ${cartProduct.product?.caption} in stock"), HttpStatus.BAD_REQUEST)
                            }
                        }
                        if (cart.totalAmount <= checkUser.wallet) {
                            checkUser.wallet = checkUser.wallet.minus(cart.totalAmount)
                            userService.save(checkUser)
                            cart.paymentDate = LocalDateTime.now()
                            cartService.save(cart)
                            return ResponseEntity(ResponseMessage(true, "Cart validated successfully"), HttpStatus.OK)
                        }
                        return ResponseEntity(ResponseMessage(false, "Not enough money in wallet"), HttpStatus.BAD_REQUEST)
                    }
                    return ResponseEntity(ResponseMessage(false, "Cart is empty"), HttpStatus.BAD_REQUEST)
                }
                return ResponseEntity(ResponseMessage(false, "Cart not found"), HttpStatus.BAD_REQUEST)
            }
            return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.BAD_REQUEST)
    }
}
