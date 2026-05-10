package com.likelionknu.applyserver.common.security

import com.likelionknu.applyserver.auth.data.enums.Role
import com.likelionknu.applyserver.auth.data.repository.UserRepository
import com.likelionknu.applyserver.common.redis.RedisService
import com.likelionknu.applyserver.common.response.ErrorCode
import com.likelionknu.applyserver.common.security.exception.JwtAuthenticationException
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.security.Key
import java.time.Duration
import java.util.Date

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}") secretKey: String,
    private val redisService: RedisService,
    private val userRepository: UserRepository,
    @Value("\${jwt.access-token.expire-time}") private val accessTokenExpireTime: Long,
    @Value("\${jwt.refresh-token.expire-time}") private val refreshTokenExpireTime: Long
) {
    private val log = LoggerFactory.getLogger(JwtTokenProvider::class.java)
    private val key: Key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey))

    fun getSubject(token: String): String {
        return parseClaims(token).subject
    }

    private fun createToken(email: String, authorities: String?, expireDate: Date): String {
        log.info("[createToken] 새 JWT 발급 됨: {}", email)

        val builder = Jwts.builder()
            .setSubject(email)
            .setExpiration(expireDate)
            .signWith(key, SignatureAlgorithm.HS256)

        if (!authorities.isNullOrEmpty()) {
            builder.claim("authorities", authorities)
        }

        return builder.compact()
    }

    fun generateToken(authentication: Authentication): AuthenticationToken {
        val username = authentication.name

        log.info("[generateToken] 새 JWT 발급 시도: {}", username)

        val authorities = authentication.authorities
            .map(GrantedAuthority::getAuthority)
            .joinToString(",")

        val now = Date().time

        val accessToken = createToken(
            username,
            authorities,
            Date(now + accessTokenExpireTime)
        )
        val refreshToken = createToken(
            username,
            null,
            Date(now + refreshTokenExpireTime)
        )

        log.info("[generateToken] 발급된 Refresh Token이 Redis에 저장 됨")
        redisService.setValues(
            username,
            refreshToken,
            Duration.ofMillis(refreshTokenExpireTime)
        )

        return AuthenticationToken(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    fun getAuthentication(accessToken: String): Authentication {
        val claims = parseClaims(accessToken)
        val authClaim = claims["authorities"]

        val authorities: Collection<GrantedAuthority> =
            if (authClaim == null || authClaim.toString().isEmpty()) {
                val user = userRepository.findOptionalByEmail(claims.subject).orElse(null)
                val role = (user?.role ?: Role.USER).toString()
                listOf(SimpleGrantedAuthority(role))
            } else {
                authClaim.toString()
                    .split(",")
                    .map { SimpleGrantedAuthority(it) }
            }

        val principal: UserDetails = org.springframework.security.core.userdetails.User(
            claims.subject,
            "",
            authorities
        )

        return UsernamePasswordAuthenticationToken(principal, "", authorities)
    }

    private fun parseClaims(accessToken: String): Claims {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(accessToken)
                .body
        } catch (e: ExpiredJwtException) {
            e.claims
        }
    }

    fun validateRefreshToken(refreshToken: String) {
        validateToken(refreshToken)

        val claims = parseClaims(refreshToken)
        val email = claims.subject
        val savedToken = redisService.getValues(email)?.toString()

        if (savedToken == null || savedToken != refreshToken) {
            throw JwtAuthenticationException(ErrorCode.TOKEN_INVALID)
        }
    }

    fun expireUsersToken(email: String) {
        val savedToken = redisService.getValues(email)?.toString()

        if (savedToken != null) {
            redisService.deleteValues(email)
        }
    }

    fun validateToken(token: String): Boolean {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)

            return true
        } catch (e: SecurityException) {
            log.error("[validateToken] 유효하지 않은 JWT 인증 요청: {}", e.message)
            throw JwtAuthenticationException(ErrorCode.TOKEN_INVALID)
        } catch (e: MalformedJwtException) {
            log.error("[validateToken] 유효하지 않은 JWT 인증 요청: {}", e.message)
            throw JwtAuthenticationException(ErrorCode.TOKEN_INVALID)
        } catch (e: ExpiredJwtException) {
            log.warn("[validateToken] 만료된 JWT 인증 요청: {}", e.message)
            throw JwtAuthenticationException(ErrorCode.TOKEN_INVALID)
        } catch (e: UnsupportedJwtException) {
            log.warn("[validateToken] 지원되지 않는 JWT 인증 요청: {}", e.message)
            throw JwtAuthenticationException(ErrorCode.TOKEN_INVALID)
        } catch (e: IllegalArgumentException) {
            log.warn("[validateToken] JWT가 제출되지 않음: {}", e.message)
            throw JwtAuthenticationException(ErrorCode.TOKEN_INVALID)
        }
    }
}