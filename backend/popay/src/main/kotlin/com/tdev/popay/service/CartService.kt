package com.tdev.popay.service

import com.tdev.popay.model.Cart
import com.tdev.popay.repository.CartRepository
import org.springframework.stereotype.Service

@Service
class CartService(
    private val cartRepository: CartRepository,
) {
    fun findCurrentCartByUserId(userId: Long): Cart? {
        val carts = cartRepository.findAll()
        return carts.find { it.user?.id == userId && it.paymentDate == null }
    }

    fun findAllPayedCartsByUserId(userId: Long): List<Cart> {
        val carts = cartRepository.findAll()
        return carts.filter { it.user?.id == userId && it.paymentDate != null }
    }

    fun findOnePayedCartsByUserId(userId: Long, cartId: Long): Cart? {
        val carts = cartRepository.findAll()
        return carts.find { it.user?.id == userId && it.paymentDate != null && it.id == cartId }
    }

    fun save(cart: Cart) {
        cartRepository.save(cart)
    }
}
