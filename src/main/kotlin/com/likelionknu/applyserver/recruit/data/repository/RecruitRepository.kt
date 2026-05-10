package com.likelionknu.applyserver.recruit.data.repository

import com.likelionknu.applyserver.recruit.data.entity.Recruit
import org.springframework.data.jpa.repository.JpaRepository

interface RecruitRepository : JpaRepository<Recruit, Long>