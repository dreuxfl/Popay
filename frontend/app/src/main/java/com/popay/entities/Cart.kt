package com.popay.entities

import java.io.Serializable
import java.time.Instant
import java.time.LocalDateTime

class Cart: Serializable {
    var id: Int = 0
    var date: LocalDateTime = LocalDateTime.now()
    var price: Double = 0.0

    constructor(id: Int, date: LocalDateTime, price: Double) {
        this.id = id
        this.date = date
        this.price = price
    }

    constructor()
}
