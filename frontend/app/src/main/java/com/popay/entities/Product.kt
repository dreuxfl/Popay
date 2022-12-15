package com.popay.entities

import java.io.Serializable

class Product : Serializable {
    var id: Int = 0
    var name: String = ""
    var price: Double = 0.0
    var description: String = ""
    var image: String = ""
    var quantity: Int = 1

    constructor(id: Int, name: String, price: Double, description: String) {
        this.id = id
        this.name = name
        this.price = price
        this.description = description
    }

    constructor()


}