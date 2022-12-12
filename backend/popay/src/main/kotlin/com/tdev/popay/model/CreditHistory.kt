package com.tdev.popay.model

import org.hibernate.Hibernate
import jakarta.persistence.*
import jakarta.validation.constraints.*
import java.time.LocalDateTime

@Entity
@Table(name = "credit_history")
data class CreditHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @get: DecimalMin(value = "0.01", inclusive = false, message = "Amount must be greater than 1")
    val amount: Double = 0.0,
    val transactionDate: LocalDateTime = LocalDateTime.now(),
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user")
    val user: User? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) {
            return false
        }
        other as CreditHistory

        return id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id_credit_history = $id , credit = $amount , transaction_date = $transactionDate , user = $user)"
    }
}
