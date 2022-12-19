package com.popay.entities

import java.io.Serializable

class User : Serializable {
    var id: Int = 0
    var name: String   = ""
    var email: String = ""
    var password: String = ""

    constructor(id: Int, name: String, email: String) {
        this.id = id
        this.name = name
        this.email = email
    }
    constructor()
}