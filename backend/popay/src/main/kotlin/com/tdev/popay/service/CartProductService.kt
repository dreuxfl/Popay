package com.tdev.popay.service

import com.tdev.popay.model.CartProduct
import com.tdev.popay.repository.CartProductRepository
import org.springframework.stereotype.Service

@Service
class CartProductService(
    private val cartProductRepository: CartProductRepository
) {
    fun findAllByCartId(id: Long): List<CartProduct> {
        val cartProducts = cartProductRepository.findAll()
        return cartProducts.filter { it.cart?.id == id }
    }

    fun findByCartIdAndProductId(cartId: Long, productId: Long): CartProduct? {
        val cartProducts = cartProductRepository.findAll()
        return cartProducts.find { it.cart?.id == cartId && it.product?.id == productId }
    }

    fun save(cartProduct: CartProduct): CartProduct {
        return cartProductRepository.save(cartProduct)
    }

    fun delete(id: Long) {
        cartProductRepository.deleteById(id)
    }
}
