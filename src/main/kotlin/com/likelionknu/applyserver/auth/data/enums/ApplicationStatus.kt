package com.likelionknu.applyserver.auth.data.enums

enum class ApplicationStatus {
    DRAFT,
    SUBMITTED,
    UNDER_DOCUMENT_REVIEW,
    DOCUMENT_PASSED,
    DOCUMENT_FAILED,
    WAITING_INTERVIEW,
    DONE_INTERVIEW,
    UNDER_INTERVIEW_REVIEW,
    FAIL_INTERVIEW,
    FINAL_PASSED,
    CANCELED;

    fun isAfterFinalSubmit(): Boolean {
        return this != DRAFT && this != CANCELED
    }

    fun isUserLocked(): Boolean {
        return this != DRAFT
    }
}