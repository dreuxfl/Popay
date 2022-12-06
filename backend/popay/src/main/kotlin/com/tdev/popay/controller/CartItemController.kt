package com.tdev.popay.controller

import com.tdev.popay.dto.ResponseMessage
import com.tdev.popay.model.CartItem
import com.tdev.popay.repository.CartItemRepository
import com.tdev.popay.repository.CartRepository
import com.tdev.popay.repository.ProductRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.*
import java.util.*
import jakarta.validation.Valid

@RestController
@RequestMapping("/api")
class CartItemController(
    private val cartItemRepository: CartItemRepository,
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository
) {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, String>> {
        val errors: MutableMap<String, String> = HashMap()
        ex.bindingResult.fieldErrors.forEach { error -> errors[error.field] = error.defaultMessage!! }
        return ResponseEntity(errors, HttpStatus.BAD_REQUEST)
    }

    @PostMapping("/cart_item/{cart_id}/{product_id}")
    fun createCartItem(
        @PathVariable(value = "cart_id") cartId: Long,
        @PathVariable(value = "product_id") productId: Long,
        @Valid @RequestBody cartItem: CartItem
    ): ResponseEntity<Any> {
        val checkCart = cartRepository.findById(cartId)
        if (checkCart.isPresent) {
            val cart = checkCart.get()
            val checkProduct = productRepository.findById(productId)
            if (checkProduct.isPresent) {
                val product = checkProduct.get()
                val newCartItem = CartItem(
                    cart = cart,
                    product = product,
                    count = cartItem.count
                )
                cartItemRepository.save(newCartItem)
                return ResponseEntity(ResponseMessage(true, "Cart item created successfully"), HttpStatus.CREATED)
            }
            return ResponseEntity(ResponseMessage(false, "Product not found"), HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity(ResponseMessage(false, "Cart not found"), HttpStatus.BAD_REQUEST)
    }

    @GetMapping("/cart_items")
    fun getAllCartItems(): List<CartItem> = cartItemRepository.findAll()

    @GetMapping("/cart_item/{id}")
    fun getCartItemById(@PathVariable(value = "id") cartItemId: Long): ResponseEntity<Any> {
        val checkCartItem = cartItemRepository.findById(cartItemId)
        if (checkCartItem.isPresent) {
            return ResponseEntity(checkCartItem.get(), HttpStatus.OK)
        }
        return ResponseEntity(ResponseMessage(false, "Cart item not found"), HttpStatus.BAD_REQUEST)
    }

    @PutMapping("/cart_item/{id}")
    fun updateCartItemById(
        @PathVariable(value = "id") cartItemId: Long,
        @Valid @RequestBody newCartItem: CartItem
    ): ResponseEntity<Any> {
        val checkCartItem = cartItemRepository.findById(cartItemId)
        if (checkCartItem.isPresent) {
            val cartItem = checkCartItem.get().copy(
                count = newCartItem.count
            )
            cartItemRepository.save(cartItem)
            return ResponseEntity(ResponseMessage(true, "Cart item updated successfully"), HttpStatus.OK)
        }
        return ResponseEntity(ResponseMessage(false, "Cart item not found"), HttpStatus.BAD_REQUEST)
    }

    @DeleteMapping("/cart_item/{id}")
    fun deleteCartItemById(@PathVariable(value = "id") cartItemId: Long): ResponseEntity<Any> {
        val checkCartItem = cartItemRepository.findById(cartItemId)
        if (checkCartItem.isPresent) {
            cartItemRepository.deleteById(cartItemId)
            return ResponseEntity(ResponseMessage(true, "Cart item deleted successfully"), HttpStatus.OK)
        }
        return ResponseEntity(ResponseMessage(false, "Cart item not found"), HttpStatus.BAD_REQUEST)
    }
}
