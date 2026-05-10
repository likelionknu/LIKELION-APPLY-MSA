package com.likelionknu.applyserver.application.data.repository

import com.likelionknu.applyserver.application.data.entity.Application
import com.likelionknu.applyserver.application.data.entity.RecruitAnswer
import org.springframework.data.jpa.repository.JpaRepository

interface RecruitAnswerRepository : JpaRepository<RecruitAnswer, Long> {
    fun findByApplication(application: Application): List<RecruitAnswer>
    fun findAllByApplication(application: Application): List<RecruitAnswer>
    fun findByApplicationId(applicationId: Long): List<RecruitAnswer>
    fun deleteByApplication_Id(applicationId: Long)
}