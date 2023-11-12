package com.jeongum.springsecurity.api.controller

import com.jeongum.springsecurity.api.controller.dto.BaseResponse
import com.jeongum.springsecurity.api.controller.dto.MemberInfoResponse
import com.jeongum.springsecurity.api.service.MemberService
import com.jeongum.springsecurity.core.entity.CustomUser
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/member")
@RestController
class MemberController(
    private val memberService: MemberService
) {
    @GetMapping("/info")
    fun getMyInfo(): BaseResponse<MemberInfoResponse> {
        val userId = (SecurityContextHolder.getContext().authentication.principal as CustomUser).id
        val userInfo = memberService.getInfo(userId)
        return BaseResponse(data = userInfo)
    }
}
