package com.likelionknu.applyserver.auth.service

import com.likelionknu.applyserver.auth.data.dto.request.ModifyProfileRequestDto
import com.likelionknu.applyserver.auth.data.dto.response.ProfileResponseDto
import com.likelionknu.applyserver.auth.data.repository.UserRepository
import com.likelionknu.applyserver.auth.exception.UserNotFoundException
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
) {
    private val log = LoggerFactory.getLogger(UserService::class.java)

    @Transactional
    fun modifyUsersProfile(
        email: String,
        modifyProfileRequestDto: ModifyProfileRequestDto
    ): ProfileResponseDto {
        val user = userRepository.findByEmail(email) ?: throw UserNotFoundException()
        val profile = checkNotNull(user.profile)

        modifyProfileRequestDto.name?.let { user.name = it }
        modifyProfileRequestDto.depart?.let { profile.depart = it }
        modifyProfileRequestDto.studentId?.let { profile.studentId = it }
        modifyProfileRequestDto.grade?.let { profile.grade = it }
        modifyProfileRequestDto.phone?.let { profile.phone = it }
        modifyProfileRequestDto.status?.let { profile.status = it }

        log.info("[modifyUsersProfile] 사용자 상세 정보 수정: {}", email)

        return ProfileResponseDto(
            email = user.email,
            name = user.name,
            profileUrl = user.profileUrl,
            depart = profile.depart,
            studentId = profile.studentId,
            grade = profile.grade,
            phone = profile.phone,
            status = profile.status?.displayName
        )
    }

    fun getUsersProfile(email: String): ProfileResponseDto {
        val user = userRepository.findByEmail(email) ?: throw UserNotFoundException()
        val profile = checkNotNull(user.profile)

        log.info("[getUsersProfile] 사용자 상세 정보 조회: {}", email)

        return ProfileResponseDto(
            email = user.email,
            name = user.name,
            profileUrl = user.profileUrl,
            depart = profile.depart,
            studentId = profile.studentId,
            grade = profile.grade,
            phone = profile.phone,
            status = profile.status?.displayName
        )
    }
}