package com.jeongum.springsecurity.api.service

import com.jeongum.springsecurity.api.client.SocialClient
import com.jeongum.springsecurity.api.model.auth.SocialType
import com.jeongum.springsecurity.api.model.auth.SocialUser
import com.jeongum.springsecurity.core.entity.CustomUser
import com.jeongum.springsecurity.core.entity.member.Member
import com.jeongum.springsecurity.core.repository.MemberRepository
import com.jeongum.springsecurity.jwt.TokenInfo
import com.jeongum.springsecurity.jwt.TokenProvider
import org.springframework.dao.DuplicateKeyException
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import org.springframework.stereotype.Service
import java.util.*

@Service
class SocialAuthService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val tokenProvider: TokenProvider,
    private val socialClient: SocialClient
) {
    fun signInWithSocial(token: String, type: String): TokenInfo {
        val socialType = SocialType.from(type)
        val socialUser = getSocialUserFromType(token, socialType)

        val member = memberRepository.findByEmailAndSocialType(socialUser.email, socialType) ?: signUpWithSocial(
            socialUser, socialType
        )

        return createTokenWithMember(member)
    }

    private fun getSocialUserFromType(token: String, type: SocialType): SocialUser {
        return when (type) {
            SocialType.KAKAO -> {
                socialClient.getKakaoUser(token).run {
                    if (kakaoAccount == null) throw AuthenticationCredentialsNotFoundException("kakao 계정이 존재하지 않습니다.")
                    else SocialUser(
                        email = kakaoAccount.email, name = kakaoAccount.name
                    )
                }
            }
        }
    }

    private fun signUpWithSocial(socialUser: SocialUser, socialType: SocialType): Member {
        memberRepository.findByEmail(socialUser.email)?.let {
            throw DuplicateKeyException("Already Exists")
        }

        val member = Member(
            email = socialUser.email, password = passwordEncoder.encode(
                listOf(
                    socialType, socialUser.email, socialUser.name
                ).joinToString("")
            ), name = socialUser.name, socialType = socialType
        )
        memberRepository.save(member)

        return member
    }

    private fun createTokenWithMember(member: Member): TokenInfo {
        val authorities = listOf(SimpleGrantedAuthority("ROLE_${member.type}"))
        val principal = CustomUser(UUID.fromString(member.id.toString()), member.email, member.password, authorities)

        // 여기에선 이미 인증된 사용자를 바탕으로 Token을 만들기 때문에, authenticate를 진행할 필요가 없음
        val authenticationToken = PreAuthenticatedAuthenticationToken(principal, "", authorities)

        val accessToken = tokenProvider.createAccessToken(authenticationToken)
        val refreshToken = tokenProvider.createRefreshToken(authenticationToken)

        return TokenInfo(accessToken = accessToken, refreshToken = refreshToken)
    }
}
