package com.likelionknu.applyserver.auth.data.enums

enum class StudentStatus(val displayName: String) {
    ATTENDING("재학"),
    LEAVE_OF_ABSENCE("휴학"),
    GRADUATION_DEFERRAL("졸업 유예")
}