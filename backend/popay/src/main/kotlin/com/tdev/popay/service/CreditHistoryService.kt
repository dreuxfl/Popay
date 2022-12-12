package com.tdev.popay.service

import com.tdev.popay.model.CreditHistory
import com.tdev.popay.repository.CreditHistoryRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class CreditHistoryService(
    private val creditHistoryRepository: CreditHistoryRepository,
) {
    fun findAllByUserId(userId: Long) = creditHistoryRepository.findAllByUserId(userId)

    fun findById(id: Long): CreditHistory? {
        return creditHistoryRepository.findByIdOrNull(id)
    }

    fun save(creditHistory: CreditHistory): CreditHistory {
        return creditHistoryRepository.save(creditHistory)
    }
}
