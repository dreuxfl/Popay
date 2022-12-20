package com.tdev.popay.service

import com.tdev.popay.model.Card
import com.tdev.popay.repository.CardRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class CardService(
    private val cardRepository: CardRepository,
) {
    fun findById(id: Long): Card? {
        return cardRepository.findByIdOrNull(id)
    }

    fun findByNfcId(nfcId: String): Card? {
        return cardRepository.findByNfcId(nfcId)
    }

    fun existsByNfcId(nfcId: String): Boolean {
        return cardRepository.existsByNfcId(nfcId)
    }

    fun save(card: Card): Card {
        return cardRepository.save(card)
    }
}
