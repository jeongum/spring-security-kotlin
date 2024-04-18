package com.jeongum.springsecurity.jwt

import com.jeongum.springsecurity.core.entity.CustomUser
import com.jeongum.springsecurity.core.entity.member.MemberRefreshToken
import com.jeongum.springsecurity.core.repository.MemberRefreshTokenRepository
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
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
    @Value("\${jwt.expiration-minutes}") private val expirationMinutes: Long,
    @Value("\${jwt.refresh-expiration-days}") private val refreshExpirationDays: Long,
    private val memberRefreshTokenRepository: MemberRefreshTokenRepository
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
            .setExpiration(Date.from(Instant.now().plus(expirationMinutes, ChronoUnit.SECONDS)))
            .setSubject(authentication.name)
            .claim("auth", auth)
            .claim("userId", userId)
            .signWith(key, SignatureAlgorithm.HS256).compact()
    }

    /**
     * RefreshToken 생성
     */
    fun createRefreshToken(authentication: Authentication): String {
        val memberId = (authentication.principal as CustomUser).id
        val refreshToken = Jwts.builder()
            .setIssuedAt(Timestamp.valueOf(LocalDateTime.now()))
            .setExpiration(Date.from(Instant.now().plus(refreshExpirationDays, ChronoUnit.DAYS)))
            .signWith(key, SignatureAlgorithm.HS256).compact()

        memberRefreshTokenRepository.findByIdOrNull(memberId)?.updateRefreshToken(refreshToken)
            ?: memberRefreshTokenRepository.save(MemberRefreshToken(memberId, refreshToken))

        return refreshToken
    }

    /**
     * Refresh Token Validation 및 Authentication 반환
     */
    fun validateRefreshToken(prevAccessToken: String, refreshToken: String): Authentication = try{
        // Token Validation -> check expired
        getClaimsWithValidation(refreshToken)

        // User - Token Match Validation
        val oldTokenClaims = try {
            getClaimsWithValidation(prevAccessToken)
        } catch (e: ExpiredJwtException) {
            e.claims
        }

        val userId = oldTokenClaims["userId"] as String? ?: throw RuntimeException("권한 정보가 없습니다.")
        memberRefreshTokenRepository.findByIdOrNull(UUID.fromString(userId))?.takeIf {
            it.validateRefreshToken(refreshToken)
        } ?: throw ExpiredJwtException(null, null, "Refresh token is expired.")

        getAuthentication(oldTokenClaims)
    } catch (e: ExpiredJwtException) {
        throw ExpiredJwtException(null, null, "refresh token is expired")
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
        val auth = claims["auth"] ?: throw AuthenticationCredentialsNotFoundException("권한 정보가 없습니다.")
        val userId = claims["userId"] ?: throw AuthenticationCredentialsNotFoundException("권한 정보가 없습니다.")

        val authorities: Collection<GrantedAuthority> = (auth as String).split(",").map { SimpleGrantedAuthority(it) }

        val principal: UserDetails = CustomUser(UUID.fromString(userId.toString()), claims.subject, "", authorities)

        return UsernamePasswordAuthenticationToken(principal, "", authorities)
    }
}
