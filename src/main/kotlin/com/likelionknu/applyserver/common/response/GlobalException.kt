package com.likelionknu.applyserver.common.response

open class GlobalException(
    val errorCode: ErrorCode
) : RuntimeException(errorCode.message)