package com.likelionknu.applyserver.application.data.repository

import com.likelionknu.applyserver.application.data.entity.Application
import com.likelionknu.applyserver.admin.data.dto.response.AdminRecruitSummaryResponseDto
import com.likelionknu.applyserver.auth.data.entity.User
import com.likelionknu.applyserver.auth.data.enums.ApplicationStatus
import com.likelionknu.applyserver.recruit.data.entity.Recruit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ApplicationRepository: JpaRepository<Application, Long> {
    fun findByUserIdAndRecruitIdAndStatus(
        userId: Long,
        recruitId: Long,
        status: ApplicationStatus
    ): Application?

    fun existsByUserIdAndRecruitIdAndStatus(
        userId: Long,
        recruitId: Long,
        status: ApplicationStatus
    ): Boolean

    @Query("""
        select (count(a) > 0)
        from Application a
        where a.recruit.id = :recruitId
    """)
    fun existsByRecruitId(@Param("recruitId") recruitId: Long): Boolean

    fun existsByUserIdAndRecruitIdAndStatusNot(
        userId: Long,
        recruitId: Long,
        status: ApplicationStatus
    ): Boolean

    fun existsByRecruitIdAndStatusNot(
        recruitId: Long,
        status: ApplicationStatus
    ): Boolean

    fun findByUserIdAndRecruitId(
        userId: Long,
        recruitId: Long
    ): Application?

    fun findAllByRecruit(recruit: Recruit): List<Application>

    @Query("""
        select a from Application a
        join fetch a.recruit r
        where a.user.id = :userId
        order by a.submittedAt desc
    """)
    fun findAllWithRecruitByUserId(@Param("userId") userId: Long): List<Application>

    @Query("""
        select new com.likelionknu.applyserver.admin.data.dto.response.AdminRecruitSummaryResponseDto(
            r.id,
            r.title,
            r.startAt,
            r.endAt,
            coalesce(sum(case 
                when a.status not in (
                    com.likelionknu.applyserver.auth.data.enums.ApplicationStatus.DRAFT, 
                    com.likelionknu.applyserver.auth.data.enums.ApplicationStatus.CANCELED
                ) then 1 else 0 
            end), 0),
            coalesce(sum(case 
                when a.status = com.likelionknu.applyserver.auth.data.enums.ApplicationStatus.DRAFT then 1 else 0 
            end), 0)
        )
        from Recruit r
        left join Application a on a.recruit = r
        group by r.id, r.title, r.startAt, r.endAt
        order by r.startAt desc
    """)
    fun findRecruitSummary(): List<AdminRecruitSummaryResponseDto>

    @Query("""
        select a from Application a
        join fetch a.user u
        left join fetch u.profile p
        join fetch a.recruit r
        where r.id = :recruitId
        order by
            case when a.submittedAt is null then 1 else 0 end,
            a.submittedAt desc,
            a.id desc
    """)
    fun findAllByRecruitIdWithUserProfile(@Param("recruitId") recruitId: Long): List<Application>

    fun findAllByUser(user: User): List<Application>
}