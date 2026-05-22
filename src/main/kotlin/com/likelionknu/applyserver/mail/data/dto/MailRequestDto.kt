package com.likelionknu.applyserver.mail.data.dto

import com.likelionknu.applyserver.mail.data.entity.MailContent
import com.likelionknu.applyserver.user.data.entity.ApplyUser

data class MailRequestDto(
        val applyUser: ApplyUser? = null,
        val email: String,
        val title: String,
        val template: String,
        val dataList: List<MailContent>
)