package com.likelionknu.applyserver.application.service

import com.likelionknu.applyserver.application.data.entity.Application
import com.likelionknu.applyserver.application.data.repository.ApplicationRepository
import com.likelionknu.applyserver.auth.data.entity.User
import com.likelionknu.applyserver.auth.data.enums.ApplicationStatus
import com.likelionknu.applyserver.auth.data.repository.UserRepository
import com.likelionknu.applyserver.auth.exception.UserNotFoundException
import com.likelionknu.applyserver.mail.data.dto.MailRequestDto
import com.likelionknu.applyserver.mail.data.entity.MailContent
import com.likelionknu.applyserver.mail.data.service.MailService
import com.likelionknu.applyserver.recruit.data.repository.RecruitRepository
import org.springframework.stereotype.Service

@Service
class ApplicationMailService (
        private val mailService: MailService,
        private val applicationRepository: ApplicationRepository,
        private val recruitRepository: RecruitRepository,
        private val userRepository: UserRepository
){

    fun sendDocumentResult(email: String, recruitId: Long) {
        val user = userRepository.findByEmail(email) ?: throw UserNotFoundException()
        val recruit = recruitRepository.findById(recruitId).orElse(null)
        val applicationList = applicationRepository.findAllByRecruit(recruit)

        applicationList.forEach { applicationList ->
            when (applicationList.status) {
                ApplicationStatus.DOCUMENT_PASSED -> {
                    val mailContentList = mutableListOf(
                            MailContent("userName", applicationList.user.name),
                            MailContent("part", applicationList.recruit.title),
                            MailContent("interviewLocation", "추후 공지 예정 (SMS 개별 안내)"),
                            MailContent("interviewTime", "15")
                    )
                    sendMail(user, applicationList, "1차 서류 전형 결과 안내", "document-passed", mailContentList)
                }

                ApplicationStatus.DOCUMENT_FAILED -> {
                    val mailContentList = mutableListOf(
                            MailContent("userName", applicationList.user.name),
                            MailContent("part", applicationList.recruit.title)
                    )
                    sendMail(user, applicationList, "1차 서류 전형 결과 안내", "document-failed", mailContentList)
                }

                else -> {}
            }
        }
    }
        fun sendFinalResult(email: String, recruitId: Long) {
            val user = userRepository.findByEmail(email) ?: throw UserNotFoundException()
            val recruit = recruitRepository.findById(recruitId).orElse(null)
            val applicationList = applicationRepository.findAllByRecruit(recruit)

            applicationList.forEach { applicationList ->
                when (applicationList.status) {
                    ApplicationStatus.FINAL_PASSED -> {
                        val mailContentList = mutableListOf(
                                MailContent("userName", applicationList.user.name),
                                MailContent("part", applicationList.recruit.title),
                        )
                        sendMail(user, applicationList, "최종 선발 전형 결과 안내", "final-passed", mailContentList)
                    }

                    ApplicationStatus.FAIL_INTERVIEW -> {
                        val mailContentList = mutableListOf(
                                MailContent("userName", applicationList.user.name),
                                MailContent("part", applicationList.recruit.title)
                        )
                        sendMail(user, applicationList, "최종 선발 전형 결과 안내", "final-failed", mailContentList)
                    }

                    else -> {}
                }
            }
        }
            private fun sendMail(user: User, application: Application, title: String, template: String, contents: List<MailContent>) {
                mailService.sendMail(
                        MailRequestDto(
                                user = user,
                                email = application.user.email,
                                title = title,
                                template = template,
                                dataList = contents
                        )
                )
            }

    }