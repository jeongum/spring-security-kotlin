package com.jeongum.springsecurity.core.entity.member

import jakarta.persistence.*

@Entity
class MemberRole(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val role: ROLE,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = ForeignKey(name = "fk_member_role"))
    val member: Member
) {
    enum class ROLE {
        MEMBER
    }
}
