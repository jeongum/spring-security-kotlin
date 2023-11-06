package com.jeongum.springsecurity.core.repository

import com.jeongum.springsecurity.core.entity.Member
import com.jeongum.springsecurity.core.entity.MemberRole
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface MemberRepository : JpaRepository<Member, UUID> {
    fun findByEmail(email: String?): Member?
}

interface MemberRoleRepository : JpaRepository<MemberRole, Long>

