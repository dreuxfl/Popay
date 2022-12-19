package com.tdev.popay.model

import org.hibernate.Hibernate
import jakarta.persistence.*

@Entity
@Table(name = "cart_product")
data class CartProduct(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    var quantity: Int = 0,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart")
    val cart: Cart?,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product")
    val product: Product?,
) {
   override fun equals(other: Any?): Boolean {
       if (this === other) {
           return true
       }
       if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) {
           return false
       }
       other as CartProduct

       return id == other.id
   }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id_cart_product = $id , quantity = $quantity , cart = $cart , product = $product)"
    }
}
