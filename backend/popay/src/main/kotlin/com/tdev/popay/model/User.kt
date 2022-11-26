package com.tdev.popay.model

import org.hibernate.Hibernate
import javax.persistence.*
import javax.validation.constraints.*

@Entity
@Table(name = "user")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    @get: NotBlank(message = "First name is mandatory")
    val first_name: String = "",
    @get: NotBlank(message = "Last name is mandatory")
    val last_name: String = "",
    @get: NotBlank(message = "Email is mandatory")
    @get: Email(message = "Email should be valid")
    val email: String = "",
    @get: NotBlank(message = "Password is mandatory")
    @get: Size(min = 8, message = "Password should be at least 8 characters")
    val password: String = "",
    val wallet: Double? = 0.0,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) {
            return false
        }
        other as User

        return id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id_user = $id , first_name = $first_name , last_name = $last_name , email = $email , password = $password , wallet = $wallet)"
    }
}
