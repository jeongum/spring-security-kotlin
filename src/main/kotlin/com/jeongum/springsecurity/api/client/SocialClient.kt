package com.jeongum.springsecurity.api.client

import com.jeongum.springsecurity.api.client.dto.KakaoSocialUser
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Component
class SocialClient(
    private val webClient: WebClient
) {
    // https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#req-user-info
    fun getKakaoUser(token: String): KakaoSocialUser = runBlocking {
        webClient.post().apply {
            uri("https://kapi.kakao.com/v2/user/me")
            headers {
                it.set("Authorization", "Bearer ${token}")
            }
            bodyValue("property_keys=[\"kakao_account.name\", \"kakao_account.email\"]")
        }.retrieve().awaitBody<KakaoSocialUser>()
    }
}
