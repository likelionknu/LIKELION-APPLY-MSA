package com.likelionknu.applyserver.application.data.repository

import com.likelionknu.applyserver.application.data.entity.ApplyModifyLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ApplyModifyLogRepository :
    JpaRepository<ApplyModifyLog, Long> {

    @Query(
        """
        select log
        from ApplyModifyLog log
        join fetch log.user user
        left join fetch log.application application
        where log.recruit.id = :recruitId
        order by log.createdAt desc
        """
    )
    fun findAllByRecruitIdWithUserAndApplication(
        @Param("recruitId") recruitId: Long
    ): List<ApplyModifyLog>
}