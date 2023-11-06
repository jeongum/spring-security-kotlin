package com.jeongum.springsecurity.api.controller.dto

data class SingUpRequest(
    val email: String,
    val password: String,
    val name: String
)
