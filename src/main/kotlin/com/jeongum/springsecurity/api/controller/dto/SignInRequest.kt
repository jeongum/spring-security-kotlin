package com.jeongum.springsecurity.api.controller.dto

data class SignInRequest(
    val email: String,
    val password: String
)
