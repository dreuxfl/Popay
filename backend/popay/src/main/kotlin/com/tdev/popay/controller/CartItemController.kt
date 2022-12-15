package com.tdev.popay.controller

import com.tdev.popay.dto.ResponseMessage
import com.tdev.popay.model.CartItem
import com.tdev.popay.service.CartItemService
import com.tdev.popay.service.CartService
import com.tdev.popay.service.ProductService
import com.tdev.popay.service.TokenService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.*
import java.util.*
import jakarta.validation.Valid

@RestController
@RequestMapping("/api")
class CartItemController(
    private val cartItemService: CartItemService,
    private val cartService: CartService,
    private val productService: ProductService,
    private val tokenService: TokenService,
) {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, String>> {
        val errors: MutableMap<String, String> = HashMap()
        ex.bindingResult.fieldErrors.forEach { error -> errors[error.field] = error.defaultMessage!! }
        return ResponseEntity(errors, HttpStatus.BAD_REQUEST)
    }

    @PostMapping("/cart_item/{product_id}")
    fun createCartItem(
        @RequestHeader("Authorization") token: String,
        @PathVariable(value = "product_id") productId: Long,
        @Valid @RequestBody cartItem: CartItem
    ): ResponseEntity<Any> {
        val userId = tokenService.getUserIdFromToken(token)
        if (userId != null) {
            val checkCart = cartService.findCurrentCartByUserId(userId)
            if (checkCart != null) {
                val checkProduct = productService.findById(productId)
                if (checkProduct != null) {
                    val checkCartItem = cartItemService.findByCartIdAndProductId(checkCart.id, productId)
                    if (checkCartItem != null) {
                        checkCartItem.count = checkCartItem.count + cartItem.count
                        cartItemService.save(checkCartItem)
                        return ResponseEntity(ResponseMessage(true, "Cart item updated successfully"), HttpStatus.CREATED)
                    } else {
                        val newCartItem = CartItem(
                            cart = checkCart,
                            product = checkProduct,
                            count = cartItem.count
                        )
                        cartItemService.save(newCartItem)
                        return ResponseEntity(ResponseMessage(true, "Cart item created successfully"), HttpStatus.CREATED)
                    }
                }
                return ResponseEntity(ResponseMessage(false, "Product not found"), HttpStatus.BAD_REQUEST)
            }
            return ResponseEntity(ResponseMessage(false, "Cart not found"), HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.BAD_REQUEST)
    }

    @GetMapping("/cart_items")
    fun getCartItems(@RequestHeader("Authorization") token: String): ResponseEntity<Any> {
        val userId = tokenService.getUserIdFromToken(token)
        if (userId != null) {
            val checkCart = cartService.findCurrentCartByUserId(userId)
            if (checkCart != null) {
                val checkCartItems = cartItemService.findAllByCartId(checkCart.id)
                return ResponseEntity(checkCartItems, HttpStatus.OK)
            }
            return ResponseEntity(ResponseMessage(false, "Cart not found"), HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.BAD_REQUEST)
    }
}
