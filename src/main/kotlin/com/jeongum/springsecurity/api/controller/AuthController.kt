package com.jeongum.springsecurity.api.controller

import com.jeongum.springsecurity.api.controller.dto.BaseResponse
import com.jeongum.springsecurity.api.controller.dto.SignInRequest
import com.jeongum.springsecurity.api.controller.dto.SingUpRequest
import com.jeongum.springsecurity.api.service.OnAppAuthService
import com.jeongum.springsecurity.api.service.SocialAuthService
import com.jeongum.springsecurity.jwt.TokenInfo
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val onAppAuthService: OnAppAuthService,
    private val socialAuthService: SocialAuthService
) {
    @PostMapping("/sign-up")
    fun singUp(@RequestBody singUpRequest: SingUpRequest): BaseResponse<String> =
        onAppAuthService.signUp(singUpRequest).run {
            BaseResponse()
        }

    @PostMapping("/sign-in")
    fun login(@RequestBody signInRequest: SignInRequest): BaseResponse<TokenInfo> =
        onAppAuthService.signIn(signInRequest).let {
            BaseResponse(data = it)
        }

    @GetMapping("/social/{type}")
    fun loginSocialUser(
        @RequestHeader("Authorization") token: String, @PathVariable("type") type: String
    ): BaseResponse<TokenInfo> = socialAuthService.signInWithSocial(token, type).let {
        BaseResponse(data = it)
    }
}
