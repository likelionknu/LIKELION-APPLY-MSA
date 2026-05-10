package com.likelionknu.applyserver.common.response

data class GlobalResponse<T>(
    val data: T?,
    val error: GlobalErrorResponse
) {
    companion object {
        @JvmStatic
        fun <T> ok(data: T): GlobalResponse<T> =
            GlobalResponse(data, GlobalErrorResponse.empty())

        @JvmStatic
        fun ok(): GlobalResponse<Void> =
            GlobalResponse(null, GlobalErrorResponse.empty())

        @JvmStatic
        fun <T> error(errorCode: ErrorCode): GlobalResponse<T> =
            GlobalResponse(null, GlobalErrorResponse.from(errorCode))

        @JvmStatic
        fun <T> error(e: GlobalException): GlobalResponse<T> =
            error(e.errorCode)
    }
}