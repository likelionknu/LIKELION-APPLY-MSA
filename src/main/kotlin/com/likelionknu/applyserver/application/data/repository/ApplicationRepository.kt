package com.likelionknu.applyserver.application.data.repository

import com.likelionknu.applyserver.application.data.entity.Application
import com.likelionknu.applyserver.auth.data.enums.ApplicationEvaluation
import com.likelionknu.applyserver.auth.data.enums.ApplicationStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ApplicationRepository : JpaRepository<Application, Long> {

    fun countByRecruitId(recruitId: Long): Long

    fun countByRecruitIdAndStatus(
        recruitId: Long,
        status: ApplicationStatus
    ): Long

    fun countByRecruitIdAndStatusAndEvaluation(
        recruitId: Long,
        status: ApplicationStatus,
        evaluation: ApplicationEvaluation
    ): Long

    fun countByRecruitIdAndStatusAndEvaluationIsNull(
        recruitId: Long,
        status: ApplicationStatus
    ): Long

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

    @Query(
        """
        select (count(a) > 0)
        from Application a
        where a.recruit.id = :recruitId
        """
    )
    fun existsByRecruitId(
        @Param("recruitId") recruitId: Long
    ): Boolean

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

    @Query(
        """
        select a
        from Application a
        join fetch a.recruit r
        where a.user.id = :userId
        order by a.submittedAt desc
        """
    )
    fun findAllWithRecruitByUserId(
        @Param("userId") userId: Long
    ): List<Application>

    @Query(
        """
        select distinct a
        from Application a
        join fetch a.user u
        left join fetch a.answers answer
        left join fetch answer.content content
        where a.recruit.id = :recruitId
        order by a.submittedAt desc
        """
    )
    fun findAllWithUserAndAnswersByRecruitId(
        @Param("recruitId") recruitId: Long
    ): List<Application>
}