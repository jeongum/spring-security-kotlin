package com.jeongum.springsecurity.api.model.auth

enum class SocialType(val value: String){
    KAKAO("kakao");

    companion object {
        fun from(value: String): SocialType = SocialType.values().first { it.value == value }
    }
}
