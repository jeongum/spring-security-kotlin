package com.jeongum.springsecurity.api.client.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
@JsonIgnoreProperties(ignoreUnknown = true)
data class KakaoSocialUser (
    val kakaoAccount: KakaoAccount?
) {
    data class KakaoAccount (
        val name: String,
        val email: String
    )
}
