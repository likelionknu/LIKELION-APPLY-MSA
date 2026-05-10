package com.likelionknu.applyserver.common.response

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(GlobalException::class)
    fun handleGlobalException(e: GlobalException): ResponseEntity<GlobalResponse<*>> {
        val status = HttpStatus.valueOf(e.errorCode.status)
        return ResponseEntity
            .status(status)
            .body(GlobalResponse.error<Any>(e))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(e: MethodArgumentNotValidException): ResponseEntity<GlobalResponse<*>> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(GlobalResponse.error<Any>(ErrorCode.VALIDATION_ERROR))
    }
}