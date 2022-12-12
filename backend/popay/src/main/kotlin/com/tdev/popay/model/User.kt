package com.tdev.popay.model

import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.Hibernate
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import jakarta.persistence.*
import jakarta.validation.constraints.*

@Entity
@Table(name = "user", uniqueConstraints = [UniqueConstraint(columnNames = ["email"])])
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @get: NotBlank(message = "First name is mandatory")
    val firstName: String = "",
    @get: NotBlank(message = "Last name is mandatory")
    val lastName: String = "",
    @get: NotBlank(message = "Email is mandatory")
    @get: Email(message = "Email should be valid")
    val email: String = "",
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @get: NotBlank(message = "Password is mandatory")
    @get: Size(min = 8, message = "Password should be at least 8 characters")
    val password: String = "",
    val wallet: Double? = 0.0,
) {
    fun comparePassword(password: String): Boolean {
        return BCryptPasswordEncoder().matches(password, this.password)
    }

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
        return this::class.simpleName + "(id_user = $id, first_name = $firstName, last_name = $lastName, email = $email, wallet = $wallet)"
    }
}
