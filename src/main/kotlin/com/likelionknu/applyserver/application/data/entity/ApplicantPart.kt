package com.likelionknu.applyserver.application.data.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class ApplicantPart(
    @get:JsonValue
    val value: String
) {
    FRONTEND("프론트엔드"),
    BACKEND("백엔드"),
    DESIGN("디자인"),
    PLANNING("기획");

    companion object {
        @JvmStatic
        @JsonCreator
        fun from(value: String): ApplicantPart {
            return entries.firstOrNull {
                it.value == value || it.name.equals(value, ignoreCase = true)
            } ?: throw IllegalArgumentException("올바르지 않은 지원 파트입니다.")
        }
    }
}