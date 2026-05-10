package com.likelionknu.applyserver.admin.service

import com.likelionknu.applyserver.admin.data.dto.response.AdminUserDetailResponseDto
import com.likelionknu.applyserver.admin.data.dto.response.AdminUserResponseDto
import com.likelionknu.applyserver.auth.data.enums.Role
import com.likelionknu.applyserver.auth.data.repository.UserRepository
import com.likelionknu.applyserver.common.response.ErrorCode
import com.likelionknu.applyserver.common.response.GlobalException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AdminUserService(
    private val userRepository: UserRepository
) {

    /**
     * 모든 사용자 정보 조회
     *
     * 모든 사용자 정보를 조회함
     * 사용자 이름, 이메일, 권한, 학과, 가입일, 최근 접속일을 반환함
     *
     * @return 전체 사용자 목록
     */
    fun getAllUsers(): List<AdminUserResponseDto> {
        val users = userRepository.findAll()

        return users.map { user ->
            AdminUserResponseDto(
                userId = user.id!!,
                name = user.name,
                email = user.email,
                role = user.role.name,
                depart = user.profile?.depart,
                createdAt = user.registeredAt,
                lastAccessedAt = user.lastAccessAt
            )
        }
    }

    /**
     * 사용자 상세 정보 조회
     *
     * 특정 사용자의 상세 정보를 조회함
     * 이름, 이메일, 전화번호, 학번, 학과, 학년, 상태, 권한을 반환함
     *
     * @param userId: 조회할 사용자 ID
     * @return 사용자 상세 정보
     */
    fun getUserDetail(userId: Long): AdminUserDetailResponseDto {
        val user = userRepository.findById(userId)
            .orElseThrow { object : GlobalException(ErrorCode.NOT_FOUND) {} }

        val profile = user.profile

        return AdminUserDetailResponseDto(
            name = user.name,
            email = user.email,
            phone = profile?.phone,
            studentId = profile?.studentId,
            depart = profile?.depart,
            grade = profile?.grade,
            status = profile?.status?.name,
            role = user.role.name
        )
    }

    /**
     * 특정 사용자 강제 회원 탈퇴
     *
     * @param userId: 삭제할 사용자 고유 ID
     */
    @Transactional
    fun deleteUser(userId: Long) {
        val user = userRepository.findById(userId)
            .orElseThrow { object : GlobalException(ErrorCode.NOT_FOUND) {} }

        userRepository.delete(user)
    }

    /**
     * 특정 사용자 권한 변경
     *
     * @param userId: 권한을 변경할 사용자 ID
     * @param role: 변경할 권한 값
     */
    @Transactional
    fun updateUserRole(userId: Long, role: Role) {
        val user = userRepository.findById(userId)
            .orElseThrow { object : GlobalException(ErrorCode.NOT_FOUND) {} }

        user.role = role
    }
}