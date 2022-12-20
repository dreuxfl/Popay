package com.tdev.popay.dto

data class LoginDto (var email: String = "", var password: String = "")
data class CardDto (var nfcId: String = "", var totalAmount: Double = 0.0)
