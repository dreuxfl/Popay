package com.tdev.popay.model

import org.hibernate.Hibernate
import jakarta.persistence.*
import jakarta.validation.constraints.*

@Entity
@Table(name = "product")
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @get: DecimalMin(value = "0.01", inclusive = false, message = "Price must be greater than 0.01")
    val price: Double = 0.0,
    @get: NotBlank(message = "Caption is mandatory")
    @get: Size(max = 50, message = "Caption must be 50 characters maximum")
    val caption: String = "",
    @get: NotBlank(message = "Description is mandatory")
    @get: Size(max = 255, message = "Description must be 255 characters maximum")
    val description: String = "",
    @get: Min(value = 1, message = "Quantity must be greater than 0")
    val stock: Int = 0,
) {
   override fun equals(other: Any?): Boolean {
       if (this === other) {
           return true
       }
       if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) {
           return false
       }
       other as Product

       return id == other.id
   }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id_product = $id , price = $price , caption = $caption , description = $description , stock = $stock)"
    }
}
