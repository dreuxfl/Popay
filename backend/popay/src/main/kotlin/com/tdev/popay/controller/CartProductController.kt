package com.tdev.popay.controller

import com.tdev.popay.dto.ResponseMessage
import com.tdev.popay.model.CartProduct
import com.tdev.popay.service.CartProductService
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
class CartProductController(
    private val cartProductService: CartProductService,
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

    @PostMapping("/cart_product/{product_id}")
    fun createCartProduct(
        @RequestHeader("Authorization") token: String,
        @PathVariable(value = "product_id") productId: Long,
        @Valid @RequestBody cartProduct: CartProduct
    ): ResponseEntity<Any> {
        val userId = tokenService.getUserIdFromToken(token)
        if (userId != null) {
            val checkCart = cartService.findCurrentCartByUserId(userId)
            if (checkCart != null) {
                val checkProduct = productService.findById(productId)
                if (checkProduct != null) {
                    val checkCartProduct = cartProductService.findByCartIdAndProductId(checkCart.id, productId)
                    if (checkCartProduct != null) {
                        checkCartProduct.quantity = checkCartProduct.quantity + cartProduct.quantity
                        if (checkCartProduct.quantity <= 0) {
                            cartProductService.delete(checkCartProduct.id)
                        } else {
                            cartProductService.save(checkCartProduct)
                        }
                        return ResponseEntity(ResponseMessage(true, "Cart product updated successfully"), HttpStatus.CREATED)
                    } else {
                        if (cartProduct.quantity <= 0) {
                            return ResponseEntity(ResponseMessage(false, "Cart product count must be greater than 0"), HttpStatus.BAD_REQUEST)
                        }
                        val newCartProduct = CartProduct(
                            cart = checkCart,
                            product = checkProduct,
                            quantity = cartProduct.quantity
                        )
                        cartProductService.save(newCartProduct)
                        return ResponseEntity(ResponseMessage(true, "Cart product created successfully"), HttpStatus.CREATED)
                    }
                }
                return ResponseEntity(ResponseMessage(false, "Product not found"), HttpStatus.BAD_REQUEST)
            }
            return ResponseEntity(ResponseMessage(false, "Cart not found"), HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.BAD_REQUEST)
    }

    @GetMapping("/cart_products")
    fun getCurrentCartProducts(@RequestHeader("Authorization") token: String): ResponseEntity<Any> {
        val userId = tokenService.getUserIdFromToken(token)
        if (userId != null) {
            val checkCart = cartService.findCurrentCartByUserId(userId)
            if (checkCart != null) {
                val checkCartProducts = cartProductService.findAllByCartId(checkCart.id)
                for (cartProducts in checkCartProducts) {
                    cartProducts.cart?.totalAmount = cartProducts.cart?.totalAmount?.plus((cartProducts.product?.price ?: 0.0) * cartProducts.quantity)!!
                }
                return ResponseEntity(checkCartProducts, HttpStatus.OK)
            }
            return ResponseEntity(ResponseMessage(false, "Cart not found"), HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.BAD_REQUEST)
    }

    @GetMapping("/cart_products/{cart_id}")
    fun getCartProductsByCartId(
        @RequestHeader("Authorization") token: String,
        @PathVariable(value = "cart_id") cartId: Long
    ): ResponseEntity<Any> {
        val userId = tokenService.getUserIdFromToken(token)
        if (userId != null) {
            val checkCart = cartService.findOnePayedCartsByUserId(userId, cartId)
            if (checkCart != null) {
                val checkCartProducts = cartProductService.findAllByCartId(checkCart.id)
                return ResponseEntity(checkCartProducts, HttpStatus.OK)
            }
            return ResponseEntity(ResponseMessage(false, "Cart not found"), HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.BAD_REQUEST)
    }
}
