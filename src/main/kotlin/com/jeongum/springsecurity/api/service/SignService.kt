package com.jeongum.springsecurity.api.service

import com.jeongum.springsecurity.api.controller.dto.SingUpRequest
import com.jeongum.springsecurity.core.entity.Member
import com.jeongum.springsecurity.core.entity.MemberRole
import com.jeongum.springsecurity.core.repository.MemberRepository
import com.jeongum.springsecurity.core.repository.MemberRoleRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SignService(
    private val memberRepository: MemberRepository,
    private val memberRoleRepository: MemberRoleRepository,
    private val passwordEncoder: PasswordEncoder
) {
    @Transactional
    fun signUp(singUpRequest: SingUpRequest): String {
        memberRepository.findByEmail(singUpRequest.email)?.let {
            throw RuntimeException("Already Exists")
        }

        val member = Member(
            email = singUpRequest.email,
            password = passwordEncoder.encode(singUpRequest.password),
            name = singUpRequest.name
        )
        memberRepository.save(member)

        val memberRole = MemberRole(null, MemberRole.ROLE.MEMBER, member)
        memberRoleRepository.save(memberRole)

        return "Success"
    }
}
