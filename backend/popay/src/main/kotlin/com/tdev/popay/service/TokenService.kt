package com.tdev.popay.service

import com.tdev.popay.model.User
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Service
import java.util.*

@Service
class TokenService {
    fun createToken(user: User): String {
        val issuer = user.id.toString()
        val key = io.jsonwebtoken.security.Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256)
        return Jwts.builder()
            .setSubject(user.id.toString())
            .setIssuer(issuer)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + 3600000))
            .signWith(key).compact()
    }
}
