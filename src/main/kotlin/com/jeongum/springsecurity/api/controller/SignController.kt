package com.jeongum.springsecurity.api.controller

import com.jeongum.springsecurity.api.controller.dto.BaseResponse
import com.jeongum.springsecurity.api.controller.dto.SignInRequest
import com.jeongum.springsecurity.api.controller.dto.SingUpRequest
import com.jeongum.springsecurity.api.service.SignService
import com.jeongum.springsecurity.jwt.TokenInfo
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class SignController(
    private val signService: SignService
) {
    @PostMapping("/sign-up")
    fun singUp(@RequestBody singUpRequest: SingUpRequest): BaseResponse<String> =
        signService.signUp(singUpRequest).run {
            BaseResponse()
        }

    @PostMapping("/sign-in")
    fun login(@RequestBody signInRequest: SignInRequest): BaseResponse<TokenInfo> =
        signService.signIn(signInRequest).let {
            BaseResponse(data = it)
        }
}
