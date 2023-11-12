package com.jeongum.springsecurity.api.service

import com.jeongum.springsecurity.core.entity.CustomUser
import com.jeongum.springsecurity.core.entity.member.Member
import com.jeongum.springsecurity.core.repository.MemberRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val memberRepository: MemberRepository
): UserDetailsService {
    override fun loadUserByUsername(username: String?): UserDetails =
        memberRepository.findByEmail(username)
            ?.let { createUserDetails(it) } ?: throw UsernameNotFoundException("No User Info")

    private fun createUserDetails(member: Member) : UserDetails =
        CustomUser(
            member.id!!,
            member.email,
            member.password,
            listOf(SimpleGrantedAuthority("ROLE_${member.memberRole!!.role}"))
        )
}
