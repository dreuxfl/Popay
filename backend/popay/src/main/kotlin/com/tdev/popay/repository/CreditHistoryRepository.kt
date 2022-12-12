package com.tdev.popay.repository

import com.tdev.popay.model.CreditHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CreditHistoryRepository : JpaRepository<CreditHistory, Long>
