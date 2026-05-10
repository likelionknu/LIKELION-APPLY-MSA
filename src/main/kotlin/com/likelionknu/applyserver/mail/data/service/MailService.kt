package com.likelionknu.applyserver.mail.data.service

import com.likelionknu.applyserver.application.data.entity.MailHistory
import com.likelionknu.applyserver.application.data.repository.MailHistoryRepository
import com.likelionknu.applyserver.mail.data.dto.MailRequestDto
import com.likelionknu.applyserver.mail.data.entity.MailContent
import jakarta.mail.MessagingException
import org.slf4j.LoggerFactory
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine
import java.time.LocalDateTime

@Service
class MailService(
        private val mailSender: JavaMailSender,
        private val templateEngine: SpringTemplateEngine,
        private val mailHistoryRepository: MailHistoryRepository
){
    private val log = LoggerFactory.getLogger(javaClass)
    @Async
    fun sendMail(mailRequestDto: MailRequestDto){
        try {
            val subject = "[멋쟁이사자처럼 강남대학교] ${mailRequestDto.title}"
            val body = setContext(mailRequestDto.template, mailRequestDto.dataList)

            val mimeMessage = mailSender.createMimeMessage()
            MimeMessageHelper(mimeMessage, false, "UTF-8").apply {
                setTo(mailRequestDto.email)
                setSubject(subject)
                setText(body, true)
            }
            mailSender.send(mimeMessage)

            mailHistoryRepository.save(
                    MailHistory(null,
                            subject = subject,
                            body = body,
                            user = mailRequestDto.user,
                            recipient = mailRequestDto.email,
                            sentAt = LocalDateTime.now()
                    )
            )
            log.info("[sendMail] 메일 전송 성공: {}, {}", mailRequestDto.email, subject)
        } catch (e: MessagingException) {
            log.error("[sendMail] 메일 전송 실패: {}", e.message)
            throw RuntimeException(e)
        }
    }

    private fun setContext(template: String, dataList: List<MailContent>): String {
        val context = Context().apply {
            dataList.forEach { mailContent ->
                setVariable(mailContent.key, mailContent.value)
            }
        }
        return templateEngine.process(template, context)
    }
}
