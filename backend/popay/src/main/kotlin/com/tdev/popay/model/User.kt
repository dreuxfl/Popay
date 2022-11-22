package com.tdev.popay.model

import org.hibernate.Hibernate
import javax.persistence.*
import javax.validation.constraints.*

@Entity
@Table(name = "user")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id_user: Long = 0,
    @get: NotBlank
    val first_name: String = "",
    @get: NotBlank
    val last_name: String = "",
    @get: NotBlank
    val email: String = "",
    @get: NotBlank
    val password: String = "",
    val wallet: Double? = 0.0,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as User

        return id_user == other.id_user
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id_user = $id_user , first_name = $first_name , last_name = $last_name , email = $email , password = $password , wallet = $wallet )"
    }
}
