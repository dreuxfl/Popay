package com.tdev.popay.service

import com.tdev.popay.model.Product
import com.tdev.popay.repository.ProductRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ProductService(
    private val productRepository: ProductRepository,
) {
    fun findById(id: Long): Product? {
        return productRepository.findByIdOrNull(id)
    }

    fun findAll(): List<Product> {
        return productRepository.findAll()
    }

    fun save(product: Product) {
        productRepository.save(product)
    }
    
    fun deleteById(id: Long) {
        productRepository.deleteById(id)
    }
}
