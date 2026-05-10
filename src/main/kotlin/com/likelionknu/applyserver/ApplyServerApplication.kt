package com.likelionknu.applyserver

import jakarta.annotation.PostConstruct
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import java.util.TimeZone

@EnableFeignClients
@SpringBootApplication
class ApplyServerApplication {

    @PostConstruct
    fun started() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"))
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(ApplyServerApplication::class.java, *args)
}