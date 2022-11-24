package com.tdev.popay.model

import org.hibernate.Hibernate
import javax.persistence.*
import javax.validation.constraints.*
import java.time.LocalDateTime

@Entity
@Table(name = "cart")
data class Cart(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @get: DecimalMin(value = "1.0", inclusive = false, message = "Amount must be greater than 1")
    val total_amount: Double = 0.0,
    val payment_date: LocalDateTime = LocalDateTime.now(),
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
        return this::class.simpleName + "(id_cart = $id , amount = $total_amount , date = $payment_date , user = $user)"
    }
}
