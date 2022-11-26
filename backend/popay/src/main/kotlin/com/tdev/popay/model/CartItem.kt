package com.tdev.popay.model

import org.hibernate.Hibernate
import javax.persistence.*
import javax.validation.constraints.*

@Entity
@Table(name = "cart_item")
data class CartItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @get: Min(value = 1, message = "Count must be greater than 0")
    val count: Int = 0,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart")
    val cart: Cart? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product")
    val product: Product? = null,
) {
   override fun equals(other: Any?): Boolean {
       if (this === other) {
           return true
       }
       if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) {
           return false
       }
       other as CartItem

       return id == other.id
   }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id_cart_item = $id , count = $count , cart = $cart , product = $product)"
    }
}
