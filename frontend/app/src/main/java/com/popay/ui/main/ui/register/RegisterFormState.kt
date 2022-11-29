package com.popay.ui.main.ui.register

/**
 * Data validation state of the register form.
 */
data class RegisterFormState(
    val firstNameError: Int? = null,
    val lastNameError: Int? = null,
    val emailError: Int? = null,
    val password0Error: Int? = null,
    val password1Error: Int? = null,
    val passwordNotTheSameError : Int? = null,
    val isDataValid: Boolean = false
)