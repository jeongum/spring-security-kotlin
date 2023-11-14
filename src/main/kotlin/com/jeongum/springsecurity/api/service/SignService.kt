package com.jeongum.springsecurity.api.service

import com.jeongum.springsecurity.api.controller.dto.SignInRequest
import com.jeongum.springsecurity.api.controller.dto.SingUpRequest
import com.jeongum.springsecurity.core.entity.member.Member
import com.jeongum.springsecurity.core.entity.member.MemberRole
import com.jeongum.springsecurity.core.repository.MemberRepository
import com.jeongum.springsecurity.core.repository.MemberRoleRepository
import com.jeongum.springsecurity.jwt.TokenInfo
import com.jeongum.springsecurity.jwt.TokenProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SignService(
    private val memberRepository: MemberRepository,
    private val memberRoleRepository: MemberRoleRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManagerBuilder: AuthenticationManagerBuilder,
    private val tokenProvider: TokenProvider
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

    @Transactional
    fun signIn(signInRequest: SignInRequest): TokenInfo {
        val authenticationToken = UsernamePasswordAuthenticationToken(signInRequest.email, signInRequest.password)
        val authentication = authenticationManagerBuilder.`object`.authenticate(authenticationToken)

        val accessToken = tokenProvider.createAccessToken(authentication)
        val refreshToken = tokenProvider.createRefreshToken(authentication) // 추가

        return TokenInfo(accessToken = accessToken, refreshToken = refreshToken)
    }
}
