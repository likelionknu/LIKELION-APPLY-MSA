package com.likelionknu.applyserver.common.response

data class GlobalErrorResponse(
    val code: String?,
    val message: String?
) {
    companion object {
        fun from(errorCode: ErrorCode): GlobalErrorResponse =
            GlobalErrorResponse(
                code = errorCode.code,
                message = errorCode.message
            )

        fun empty(): GlobalErrorResponse =
            GlobalErrorResponse(null, null)
    }
}