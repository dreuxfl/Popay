package com.tdev.popay.controller

import com.tdev.popay.model.CartItem
import com.tdev.popay.repository.CartItemRepository
import com.tdev.popay.repository.CartRepository
import com.tdev.popay.repository.ProductRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.Valid

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

    @GetMapping("/cart_items")
    fun getAllCartItems(): List<CartItem> =
            cartItemRepository.findAll()

    @PostMapping("/cart_item/{cart_id}/{product_id}")
    fun createCartItem(@PathVariable(value = "cart_id") cartId: Long,
                       @PathVariable(value = "product_id") productId: Long,
                       @Valid @RequestBody cartItem: CartItem
    ): ResponseEntity<CartItem> {
        return cartRepository.findById(cartId).map { cart ->
            productRepository.findById(productId).map { product ->
                val newCartItem = cartItem
                    .copy(
                        count = cartItem.count,
                        cart = cart,
                        product = product
                    )
                ResponseEntity.ok().body(cartItemRepository.save(newCartItem))
            }.orElse(ResponseEntity.notFound().build())
        }.orElse(ResponseEntity.notFound().build())
    }

    @GetMapping("/cart_item/{id}")
    fun getCartItemById(@PathVariable(value = "id") cartItemId: Long): ResponseEntity<CartItem> {
        return cartItemRepository.findById(cartItemId).map { cartItem ->
            ResponseEntity.ok(cartItem)
        }.orElse(ResponseEntity.notFound().build())
    }

    @PutMapping("/cart_item/{id}")
    fun updateCartItemById(@PathVariable(value = "id") cartItemId: Long,
                           @Valid @RequestBody newCartItem: CartItem): ResponseEntity<CartItem> {
        return cartItemRepository.findById(cartItemId).map { existingCartItem ->
            val updatedCartItem: CartItem = existingCartItem
                    .copy(
                        count = newCartItem.count
                    )
            ResponseEntity.ok().body(cartItemRepository.save(updatedCartItem))
        }.orElse(ResponseEntity.notFound().build())
    }

    @DeleteMapping("/cart_item/{id}")
    fun deleteCartItemById(@PathVariable(value = "id") cartItemId: Long): ResponseEntity<Void> {
        return cartItemRepository.findById(cartItemId).map { cartItem  ->
            cartItemRepository.delete(cartItem)
            ResponseEntity<Void>(HttpStatus.OK)
        }.orElse(ResponseEntity.notFound().build())
    }
}
