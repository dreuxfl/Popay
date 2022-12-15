package com.tdev.popay.service

import com.tdev.popay.model.CartItem
import com.tdev.popay.repository.CartItemRepository
import org.springframework.stereotype.Service

@Service
class CartItemService(
    private val cartItemRepository: CartItemRepository
) {
    fun findAllByCartId(id: Long): List<CartItem> {
        val cartItems = cartItemRepository.findAll()
        return cartItems.filter { it.cart?.id == id }
    }

    fun findByCartIdAndProductId(cartId: Long, productId: Long): CartItem? {
        val cartItems = cartItemRepository.findAll()
        return cartItems.find { it.cart?.id == cartId && it.product?.id == productId }
    }

    fun save(cartItem: CartItem): CartItem {
        return cartItemRepository.save(cartItem)
    }
}
