package com.likelionknu.applyserver.mail.data.dto

import com.likelionknu.applyserver.auth.data.entity.User
import com.likelionknu.applyserver.mail.data.entity.MailContent

data class MailRequestDto (
        val user: User? = null,
        val email: String,
        val title: String,
        val template: String,
        val dataList: List<MailContent>
)