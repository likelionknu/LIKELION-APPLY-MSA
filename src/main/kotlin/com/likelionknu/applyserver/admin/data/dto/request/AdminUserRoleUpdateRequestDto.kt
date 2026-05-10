package com.likelionknu.applyserver.admin.data.dto.request

import com.likelionknu.applyserver.auth.data.enums.Role

data class AdminUserRoleUpdateRequestDto (
    val role: Role
    )