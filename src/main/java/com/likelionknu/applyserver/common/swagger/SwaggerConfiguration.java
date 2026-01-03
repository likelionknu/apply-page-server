package com.likelionknu.applyserver.common.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {
    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("KNU LIKELION APPLY SERVER")
                .version("v1.0.0");

        Server localServer = new Server()
                .url("http://localhost:8080")
                .description("Local Server");

        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList(SECURITY_SCHEME_NAME);

        return new OpenAPI()
                .info(info)
                .addServersItem(localServer)
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, securityScheme))
                .addSecurityItem(securityRequirement);
    }
}