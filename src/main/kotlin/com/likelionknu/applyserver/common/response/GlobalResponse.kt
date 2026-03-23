package com.likelionknu.applyserver.common.response

data class GlobalResponse<T>(
    val data: T?,
    val error: GlobalErrorResponse
) {
    companion object {
        fun <T> ok(data: T): GlobalResponse<T> =
            GlobalResponse(data, GlobalErrorResponse.empty())

        fun ok(): GlobalResponse<Void> =
            GlobalResponse(null, GlobalErrorResponse.empty())

        fun <T> error(errorCode: ErrorCode): GlobalResponse<T> =
            GlobalResponse(null, GlobalErrorResponse.from(errorCode))

        fun <T> error(e: GlobalException): GlobalResponse<T> =
            error(e.errorCode)
    }
}