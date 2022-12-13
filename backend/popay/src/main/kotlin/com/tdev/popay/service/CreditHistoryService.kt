package com.tdev.popay.service

import com.tdev.popay.model.CreditHistory
import com.tdev.popay.repository.CreditHistoryRepository
import org.springframework.stereotype.Service

@Service
class CreditHistoryService(
    private val creditHistoryRepository: CreditHistoryRepository
) {
    fun findAllByUserId(userId: Long): List<CreditHistory> {
        val creditHistories = creditHistoryRepository.findAll()
        return creditHistories.filter { it.user?.id == userId }
    }

    fun findOneByUserId(id: Long, userId: Long): CreditHistory? {
        val creditHistory = creditHistoryRepository.findById(id)
        if (creditHistory.isPresent) {
            if (creditHistory.get().user?.id == userId) {
                return creditHistory.get()
            }
        }
        return null
    }

    fun save(creditHistory: CreditHistory): CreditHistory {
        return creditHistoryRepository.save(creditHistory)
    }
}
