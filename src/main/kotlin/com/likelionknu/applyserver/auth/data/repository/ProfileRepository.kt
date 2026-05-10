package com.likelionknu.applyserver.auth.data.repository

import com.likelionknu.applyserver.auth.data.entity.Profile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProfileRepository : JpaRepository<Profile, Long>