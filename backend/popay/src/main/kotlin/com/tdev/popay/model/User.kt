package com.tdev.popay.model

import javax.persistence.*
import javax.validation.constraints.*

@Entity
@Table(name = "users")
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
    @get: NotBlank
    val wallet: Double = 0.0,
)
