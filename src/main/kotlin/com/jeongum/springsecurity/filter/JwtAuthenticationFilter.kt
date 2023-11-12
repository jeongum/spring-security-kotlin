package com.jeongum.springsecurity.filter

import com.jeongum.springsecurity.jwt.TokenInfo
import com.jeongum.springsecurity.jwt.TokenProvider
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.annotation.Order
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter

@Order(0)
@Component
class JwtAuthenticationFilter(
    private val tokenProvider: TokenProvider
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain
    ) {
        resolveToken(request, TokenInfo.TokenType.ACCESS_TOKEN.value)?.let { token ->
            handleAuthServlet(request, response) {
                (tokenProvider.getAuthentication(token) to token)
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun resolveToken(request: HttpServletRequest, headerName: String): String? {
        val bearerToken = request.getHeader(headerName)
        return if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TokenInfo.TokenGrantType.BEARER.value)) {
            bearerToken.substring(7)
        } else null
    }

    private fun handleAuthServlet(
        request: HttpServletRequest, response: HttpServletResponse, resolveAuthInfo: () -> Pair<Authentication, String>
    ) = try {
        val (authentication, token) = resolveAuthInfo()
        SecurityContextHolder.getContext().authentication = authentication
        response.setHeader(TokenInfo.TokenType.ACCESS_TOKEN.value, token)
    } catch (e: Exception) {
        request.setAttribute("exception", e)
    }
}
