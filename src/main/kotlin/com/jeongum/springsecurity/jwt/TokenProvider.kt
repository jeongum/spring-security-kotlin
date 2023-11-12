package com.jeongum.springsecurity.jwt

import com.jeongum.springsecurity.core.entity.CustomUser
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.sql.Date
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

@Component
class TokenProvider(
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.expiration-minutes}") private val expirationMinutes: Long
) {

    private val key by lazy { Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)) }

    /**
     * 토큰 생성
     */
    fun createAccessToken(authentication: Authentication): String {
        val auth = authentication.authorities.joinToString(",", transform = GrantedAuthority::getAuthority)
        val userId = (authentication.principal as CustomUser).id

        return Jwts.builder()
            .setIssuedAt(Timestamp.valueOf(LocalDateTime.now()))
            .setExpiration(Date.from(Instant.now().plus(expirationMinutes, ChronoUnit.MINUTES)))
            .setSubject(authentication.name)
            .claim("auth", auth)
            .claim("userId", userId)
            .signWith(key, SignatureAlgorithm.HS256).compact()
    }

    /**
     * 토큰 정보 추출
     */
    fun getAuthentication(token: String): Authentication {
        val claims: Claims = getClaimsWithValidation(token)
        return getAuthentication(claims)
    }

    // Claim 추출
    private fun getClaimsWithValidation(token: String): Claims =
        Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).body

    // Claim 내, Token 정보 추출
    private fun getAuthentication(claims: Claims): Authentication {
        val auth = claims["auth"] ?: throw RuntimeException("권한 정보가 없습니다.")
        val userId = claims["userId"] ?: throw RuntimeException("권한 정보가 없습니다.")

        val authorities: Collection<GrantedAuthority> = (auth as String).split(",").map { SimpleGrantedAuthority(it) }

        val principal: UserDetails = CustomUser(UUID.fromString(userId.toString()), claims.subject, "", authorities)

        return UsernamePasswordAuthenticationToken(principal, "", authorities)
    }
}
