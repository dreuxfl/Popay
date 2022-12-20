package com.tdev.popay.model

import org.hibernate.Hibernate
import jakarta.persistence.*
import jakarta.validation.constraints.*

@Entity
@Table(name = "card",  uniqueConstraints = [UniqueConstraint(columnNames = ["nfcId"])])
data class Card(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @get: NotBlank(message = "Card nfc id is mandatory")
    @get: Size(min = 14, max = 14, message = "Card nfc id should be 14 characters")
    val nfcId: String = "",
    @get: DecimalMin(value = "0.01", inclusive = false, message = "Total amount must be greater than 0.01")
    val totalAmount: Double = 0.0,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) {
            return false
        }
        other as Card

        return id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id_card = $id, nfc_id = $nfcId, total_amount = $totalAmount)"
    }
}
