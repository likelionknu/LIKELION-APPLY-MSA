package com.likelionknu.applyserver.auth.service

import com.likelionknu.applyserver.auth.data.dto.response.TokenResponseDto
import com.likelionknu.applyserver.auth.data.entity.GoogleProfile
import com.likelionknu.applyserver.auth.data.entity.Profile
import com.likelionknu.applyserver.auth.data.entity.User
import com.likelionknu.applyserver.auth.data.enums.PlatformDivider
import com.likelionknu.applyserver.auth.data.enums.Role
import com.likelionknu.applyserver.auth.exception.GoogleAuthenticaionFailedException
import com.likelionknu.applyserver.auth.data.repository.UserRepository
import com.likelionknu.applyserver.common.security.JwtTokenProvider
import com.likelionknu.applyserver.discord.service.DiscordNotificationService
import com.likelionknu.applyserver.mail.data.dto.MailRequestDto
import com.likelionknu.applyserver.mail.data.entity.MailContent
import com.likelionknu.applyserver.mail.data.service.MailService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestTemplate
import java.time.LocalDateTime

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val mailService: MailService,
    private val discordNotificationService: DiscordNotificationService,
    @Value("\${google.client.id}") private val clientId: String,
    @Value("\${google.client.secret}") private val clientSecret: String,
    @Value("\${google.redirect.uri}") private val redirectUri: String,
    @Value("\${google.redirect.admin}") private val redirectUriAdmin: String
) {
    private val log = LoggerFactory.getLogger(AuthService::class.java)

    private fun getGoogleProfile(code: String, platformDivider: PlatformDivider): GoogleProfile {
        return try {
            val restTemplate = RestTemplate()

            val redirect = when (platformDivider) {
                PlatformDivider.APPLY -> {
                    log.info("[getGoogleProfile] 어플라이 소셜 로그인 시도")
                    redirectUri
                }
                PlatformDivider.ADMIN -> {
                    log.info("[getGoogleProfile] 관리자 소셜 로그인 시도")
                    redirectUriAdmin
                }
                else -> redirectUri
            }

            val tokenUrl = "https://oauth2.googleapis.com/token"
            val params = hashMapOf(
                "code" to code,
                "client_id" to clientId,
                "client_secret" to clientSecret,
                "redirect_uri" to redirect,
                "grant_type" to "authorization_code"
            )

            val tokenResponse: ResponseEntity<Map<String, Any>> = restTemplate.exchange(
                tokenUrl,
                HttpMethod.POST,
                HttpEntity(params),
                object : ParameterizedTypeReference<Map<String, Any>>() {}
            )

            val tokenBody = tokenResponse.body
            if (tokenBody == null || !tokenBody.containsKey("access_token")) {
                log.error("[getGoogleProfile] Google Access Token 발급 실패")
                throw GoogleAuthenticaionFailedException()
            }

            val accessToken = tokenBody["access_token"].toString()

            val userInfoUrl = "https://www.googleapis.com/oauth2/v2/userinfo"
            val headers = HttpHeaders().apply {
                setBearerAuth(accessToken)
            }

            val userResponse: ResponseEntity<Map<String, Any>> = restTemplate.exchange(
                userInfoUrl,
                HttpMethod.GET,
                HttpEntity<String>(headers),
                object : ParameterizedTypeReference<Map<String, Any>>() {}
            )

            val attributes = userResponse.body
            if (attributes == null) {
                log.error("[getGoogleProfile] Google 소셜 프로필 조회 실패")
                throw GoogleAuthenticaionFailedException()
            }

            GoogleProfile(
                email = attributes["email"] as String,
                name = attributes["name"] as String,
                profileUrl = attributes["picture"] as String
            )
        } catch (e: HttpClientErrorException) {
            throw GoogleAuthenticaionFailedException()
        } catch (e: HttpServerErrorException) {
            throw GoogleAuthenticaionFailedException()
        }
    }

    private fun createAuthentication(user: User): Authentication {
        val authorities = listOf(
            SimpleGrantedAuthority("ROLE_${user.role}")
        )

        return UsernamePasswordAuthenticationToken(user.email, null, authorities)
    }

    private fun registerNewUser(googleProfile: GoogleProfile): User {
        val newUser = User().apply {
            email = googleProfile.email
            name = googleProfile.name
            profileUrl = googleProfile.profileUrl
            role = Role.USER
            modifiedAt = LocalDateTime.now()
            registeredAt = LocalDateTime.now()
            lastAccessAt = LocalDateTime.now()
        }

        val profile = Profile().apply {
            user = newUser
        }

        newUser.profile = profile

        userRepository.save(newUser)

        discordNotificationService.sendNewUserNotification(
            newUser.name,
            newUser.email,
            newUser.profileUrl
        )

        return newUser
    }

    fun userSocialSignIn(code: String, platformDivider: PlatformDivider): TokenResponseDto {
        val googleProfile = getGoogleProfile(code, platformDivider)
        var user = userRepository.findOptionalByEmail(googleProfile.email).orElse(null)
        var isNewUser = false

        if (user == null) {
            log.info("[userSocialSignIn] 새로운 사용자: {}", googleProfile.email)
            user = registerNewUser(googleProfile)

            val mailContentList = listOf(
                MailContent(
                    key = "userName",
                    value = user.name
                )
            )

            mailService.sendMail(
                MailRequestDto(
                    user = null,
                    email = user.email,
                    title = "회원가입 완료 안내",
                    template = "register-success",
                    dataList = mailContentList
                )
            )

            isNewUser = true
        } else {
            log.info("[userSocialSignIn] 기존 가입된 사용자: {}", googleProfile.email)
        }

        user.lastAccessAt = LocalDateTime.now()
        userRepository.save(user)

        val authentication = createAuthentication(user)
        val authenticationToken = jwtTokenProvider.generateToken(authentication)

        return TokenResponseDto(
            accessToken = authenticationToken.accessToken,
            refreshToken = authenticationToken.refreshToken,
            name = user.name,
            role = user.role.toString(),
            isNewUser = isNewUser
        )
    }

    fun userLogout(email: String) {
        log.info("[userLogout] 사용자 로그아웃: {}", email)
        jwtTokenProvider.expireUsersToken(email)
    }

    fun reissue(refreshToken: String): TokenResponseDto {
        jwtTokenProvider.validateRefreshToken(refreshToken)

        val email = jwtTokenProvider.getSubject(refreshToken)
        val user = userRepository.findOptionalByEmail(email)
            .orElseThrow { GoogleAuthenticaionFailedException() }

        val authentication = createAuthentication(user)
        val authenticationToken = jwtTokenProvider.generateToken(authentication)

        log.info("[reissue] 사용자 JWT 재발급 완료: {}", user.email)

        return TokenResponseDto(
            accessToken = authenticationToken.accessToken,
            refreshToken = authenticationToken.refreshToken,
            name = user.name,
            role = user.role.toString()
        )
    }
}