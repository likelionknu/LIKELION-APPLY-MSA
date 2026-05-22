package com.likelionknu.applyserver.application.data.repository

import com.likelionknu.applyserver.application.data.entity.MailHistory
import org.springframework.data.jpa.repository.JpaRepository

interface MailHistoryRepository : JpaRepository<MailHistory, Long>