package com.likelionknu.applyserver.common.swagger

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfiguration {
    companion object {
        private const val SECURITY_SCHEME_NAME = "bearerAuth"
    }

    @Bean
    fun openAPI(): OpenAPI {
        val info = Info()
            .title("KNU LIKELION APPLY SERVER")
            .version("v1.0.0")

        val localServer = Server()
            .url("http://localhost:8080")
            .description("Local Server")

        val prodServer = Server()
            .url("https://api.likelionknu.com")
            .description("Production Server")

        val securityScheme = SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .`in`(SecurityScheme.In.HEADER)
            .name("Authorization")

        val securityRequirement = SecurityRequirement()
            .addList(SECURITY_SCHEME_NAME)

        return OpenAPI()
            .info(info)
            .addServersItem(localServer)
            .addServersItem(prodServer)
            .components(
                Components()
                    .addSecuritySchemes(SECURITY_SCHEME_NAME, securityScheme)
            )
            .addSecurityItem(securityRequirement)
    }
}