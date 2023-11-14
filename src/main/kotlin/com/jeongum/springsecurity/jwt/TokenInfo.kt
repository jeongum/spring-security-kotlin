package com.jeongum.springsecurity.jwt

data class TokenInfo(
    val grantType: TokenGrantType = TokenGrantType.BEARER,
    val accessToken: String,
    val refreshToken: String
) {
    enum class TokenGrantType(val value: String) {
        BEARER("Bearer")
    }

    enum class TokenType(val value: String) {
        ACCESS_TOKEN("Access-Token"),
        REFRESH_TOKEN("Refresh-Token")
    }
}
