package com.tdev.popay.model

import org.hibernate.Hibernate
import jakarta.persistence.*
import jakarta.validation.constraints.*
import java.time.LocalDateTime

@Entity
@Table(name = "cart")
data class Cart(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @get: DecimalMin(value = "0.1", inclusive = false, message = "Amount must be greater than 1")
    val totalAmount: Double? = null,
    val paymentDate: LocalDateTime? = null,
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
        other as Cart

        return id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id_cart = $id , total_amount = $totalAmount , payment_date = $paymentDate , user = $user)"
    }
}
