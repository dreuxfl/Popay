package com.tdev.popay.repository

import com.tdev.popay.model.CartProduct
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CartProductRepository : JpaRepository<CartProduct, Long>
