package com.jeongum.springsecurity.api.controller.dto

data class BaseResponse<T>(
    val resultCode: String = "SUCCESS",
    val data: T? = null,
    val message: String = "ok",
)
