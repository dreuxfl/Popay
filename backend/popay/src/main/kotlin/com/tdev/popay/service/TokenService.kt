package com.tdev.popay.service

import com.tdev.popay.model.User
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.security.Key
import java.util.*

@Service
class TokenService {
    fun generateToken(user: User): String {
        val issuer = user.id.toString()
        val key: Key = Keys.secretKeyFor(SignatureAlgorithm.HS256)
        return Jwts.builder()
            .setSubject(user.id.toString())
            .setIssuer(issuer)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + 3600000))
            .signWith(key).compact()
    }

    fun validateToken(token: String): Boolean {
        val key: Key = Keys.secretKeyFor(SignatureAlgorithm.HS256)
        val claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
        return claims.body.expiration.after(Date())
    }
}
