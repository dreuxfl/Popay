package com.tdev.popay.repository

import com.tdev.popay.model.Card
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CardRepository : JpaRepository<Card, Long> {
    fun existsByNfcId(nfcId: String): Boolean
    fun findByNfcId(nfcId: String): Card
}
