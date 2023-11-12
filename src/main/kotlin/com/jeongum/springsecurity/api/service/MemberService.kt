package com.jeongum.springsecurity.api.service

import com.jeongum.springsecurity.api.controller.dto.MemberInfoResponse
import com.jeongum.springsecurity.core.repository.MemberRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class MemberService(
    private val memberRepository: MemberRepository
) {
    fun getInfo(id: UUID): MemberInfoResponse {
        val member = memberRepository.findByIdOrNull(id) ?: throw InvalidPropertiesFormatException("회원 정보가 없습니다.")
        return member.toInfoDto()
    }
}
