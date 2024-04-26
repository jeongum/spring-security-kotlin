package com.jeongum.springsecurity.api.model.auth

data class SocialUser(
    val email: String,
    val name: String,
    val serviceUseAgreement: Boolean = false,
    val privacyPolicyAgreement: Boolean = false,
    var marketingAgreement: Boolean = false,
)
