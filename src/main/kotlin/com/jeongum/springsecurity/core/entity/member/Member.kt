package com.jeongum.springsecurity.core.entity.member

import com.jeongum.springsecurity.api.controller.dto.MemberInfoResponse
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "member")
class Member(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    val password: String,

    val name: String,

    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "member")
    val memberRole: MemberRole? = null

    fun toInfoDto() = MemberInfoResponse(
        email,
        name
    )
}
