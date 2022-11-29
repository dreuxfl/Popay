package com.popay.ui.main.ui.register

import com.popay.ui.main.ui.login.LoggedInUserView

/**
 * Authentication result : success (user details) or error message.
 */
data class RegisterResult(
    val success: LoggedInUserView? = null,
    val error: Int? = null
)