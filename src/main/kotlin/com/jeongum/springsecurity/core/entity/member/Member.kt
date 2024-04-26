package com.jeongum.springsecurity.core.entity.member

import com.jeongum.springsecurity.api.controller.dto.MemberInfoResponse
import com.jeongum.springsecurity.api.model.auth.MemberType
import com.jeongum.springsecurity.api.model.auth.SocialType
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "member")
class Member(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,    // 앱 내에서 사용되는 사용자의 고유키

    @Column(nullable = false, unique = true)    // 중복을 허용하지 않음
    val email: String,        // 로그인 시 사용되는 사용자의 email

    @Column(nullable = false)
    val password: String,

    val name: String,    // 앱 내에서 사용되는 사용자의 닉네임

    @Enumerated(value = EnumType.STRING)
    val type: MemberType = MemberType.MEMBER,    // 사용자의 타입 (Member, Admin..)

    @Enumerated(value = EnumType.STRING)
    val socialType: SocialType? = null,

    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    fun toInfoDto() = MemberInfoResponse(
        email,
        name
    )
}
