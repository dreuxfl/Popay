package com.tdev.popay.model

import org.hibernate.Hibernate
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "cart")
data class Cart(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    var totalAmount: Double = 0.0,
    val paymentDate: LocalDateTime? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user")
    val user: User?
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
