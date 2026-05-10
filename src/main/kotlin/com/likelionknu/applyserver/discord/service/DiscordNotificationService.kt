package com.likelionknu.applyserver.discord.service

import com.likelionknu.applyserver.discord.data.dto.DiscordMessageDto
import com.likelionknu.applyserver.discord.data.enums.ChannelDivider
import com.likelionknu.applyserver.discord.data.exception.DiscordNotificationException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.net.URI
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Service
class DiscordNotificationService(
        @Value("\${discord.webhook.user}") private val userWebhookUrl: String,
        @Value("\${discord.webhook.application}") private val applicationWebhookUrl: String,
        private val discordFeignClient: DiscordFeignClient
){
    private val log = LoggerFactory.getLogger(javaClass)
    private val formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 HH시 mm분 ss초")

    private fun sendDiscordNotification (channelDivider: ChannelDivider, content: String){
        val discordMessageDto = DiscordMessageDto(content)
        val webhookUrl = when(channelDivider){
            ChannelDivider.USER -> userWebhookUrl
            ChannelDivider.APPLICATION -> applicationWebhookUrl
        }
        try {
            log.info("[sendDiscordNotification] Discord 채널에 알림이 발송 됨")
            discordFeignClient.sendNotification(URI.create(webhookUrl), discordMessageDto)
        } catch (e: Exception){
            throw DiscordNotificationException()
        }
    }

    @Async
    fun sendNewUserNotification(name: String, email: String, profileUrl: String){
        log.info("[sendNewUserNotification] Discord 채널에 신규 가입자 알림 발송 시도")
        val now = LocalDateTime.now().format(formatter)

        val message ="""
            ## 새로운 사용자가 등록 됨
            > 신규 사용자가 서비스에 등록되었습니다.
            
            ```yml
            이름: $name
            이메일 주소: $email
            가입일: $now
            ```
            [.]($profileUrl)
        """.trimIndent()
        sendDiscordNotification(ChannelDivider.USER, message)
    }

    @Async
    fun sendUserSubmittedApplication(name: String, email: String, recruitTitle: String) {
        log.info("[sendUserSubmittedApplication] Discord 채널에 지원서 최종 제출 알림 발송 시도")
        val now = LocalDateTime.now().format(formatter)

        val message = """
            ## 지원서 최종제출 됨
            > 사용자가 지원서를 최종 제출했습니다.
            
            ```yml
            이름: $name
            이메일 주소: $email
            공고 명: $recruitTitle
            제출일: $now
            ```
        """.trimIndent()

        sendDiscordNotification(ChannelDivider.APPLICATION, message)
    }

    @Async
    fun sendUserDraftApplication(name: String, email: String, recruitTitle: String) {
        log.info("[sendUserDraftApplication] Discord 채널에 지원서 임시저장 제출 알림 발송 시도")
        val now = LocalDateTime.now().format(formatter)

        val message = """
            ## 지원서 임시저장 됨
            > 사용자가 지원서를 임시저장 했습니다.
            
            ```yml
            이름: $name
            이메일 주소: $email
            공고 명: $recruitTitle
            임시저장일: $now
            ```
        """.trimIndent()

        sendDiscordNotification(ChannelDivider.APPLICATION, message)
    }
}

