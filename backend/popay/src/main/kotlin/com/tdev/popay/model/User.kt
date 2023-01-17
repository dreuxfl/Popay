package com.tdev.popay.model

import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.Hibernate
import jakarta.persistence.*
import jakarta.validation.constraints.*
import kotlin.math.max

@Entity
@Table(name = "user", uniqueConstraints = [UniqueConstraint(columnNames = ["email"])])
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @get: NotBlank(message = "First name is mandatory")
    @get: Size(max = 255, message = "First name must be 255 characters maximum")
    val firstName: String = "",
    @get: NotBlank(message = "Last name is mandatory")
    @get: Size(max = 255, message = "Last name must be 255 characters maximum")
    val lastName: String = "",
    @get: NotBlank(message = "Email is mandatory")
    @get: Email(message = "Email should be valid")
    @get: Size(max = 255, message = "Email must be 255 characters maximum")
    val email: String = "",
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @get: NotBlank(message = "Password is mandatory")
    @get: Size(min = 8, max = 255, message = "Password should be between 8 and 255 characters maximum")
    @get: Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!*.])(?=\\S+$).{8,}$", message = "Password should contain at least one digit, one lowercase, one uppercase, one special character (@#$%^&+=!*.) and no whitespace")
    val password: String = "",
    var wallet: Double = 0.0,
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
        return this::class.simpleName + "(id_user = $id, first_name = $firstName, last_name = $lastName, email = $email, wallet = $wallet)"
    }
}
