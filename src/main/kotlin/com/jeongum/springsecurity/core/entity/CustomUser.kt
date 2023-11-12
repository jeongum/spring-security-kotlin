package com.jeongum.springsecurity.core.entity

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User
import java.util.*

class CustomUser (
    val id: UUID,
    userName: String,
    password: String,
    authorities: Collection<GrantedAuthority>
): User(userName, password, authorities)
