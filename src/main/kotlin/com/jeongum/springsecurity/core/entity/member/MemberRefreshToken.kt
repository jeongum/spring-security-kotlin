package com.jeongum.springsecurity.core.entity.member

import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.*

@Entity
class MemberRefreshToken(
    @Id
    val memberId: UUID? = null,

    private var refreshToken: String
) {
    fun updateRefreshToken(refreshToken: String) {
        this.refreshToken = refreshToken
    }

    fun validateRefreshToken(refreshToken: String) =
        this.refreshToken == refreshToken
}
