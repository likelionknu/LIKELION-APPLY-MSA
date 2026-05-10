package com.likelionknu.applyserver.recruit.data.repository

import com.likelionknu.applyserver.recruit.data.entity.RecruitContent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface RecruitContentRepository : JpaRepository<RecruitContent, Long> {

    fun findByRecruitIdOrderByPriorityAsc(recruitId: Long): List<RecruitContent>

    fun findAllByRecruit_IdAndRequiredTrue(recruitId: Long?): List<RecruitContent>

    @Modifying
    @Query("delete from RecruitContent rc where rc.recruit.id = :recruitId")
    fun deleteAllByRecruitId(@Param("recruitId") recruitId: Long)
}