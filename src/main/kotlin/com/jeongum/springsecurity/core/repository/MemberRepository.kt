package com.jeongum.springsecurity.core.repository

import com.jeongum.springsecurity.core.entity.member.Member
import com.jeongum.springsecurity.core.entity.member.MemberRefreshToken
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface MemberRepository : JpaRepository<Member, UUID> {
    fun findByEmail(email: String?): Member?
}

interface MemberRefreshTokenRepository: JpaRepository<MemberRefreshToken, UUID>
