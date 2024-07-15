package jikgong.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
    info = @Info(title = "jikgong API 명세서",
        description = "기업 계정: 아래 1개   " +
            "\neyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbklkIjoiYWJjZGVmZzAiLCJleHAiOjE3MjYyMjY3NDB9.fztvihYHiIqMviCdHRxu5CBbCv9yN3gOIQy_8U4olMI    "
            +
            "\n노동자 계정: 아래 4개    " +
            "\neyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbklkIjoiYWJjZGVmZzEiLCJleHAiOjE3MjYyMjY3NjZ9.wKJLM50gYXZ-xDxalx13xsTBJRuVILG72Y4uyflTq3Y    "
            +
            "\neyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbklkIjoiYWJjZGVmZzIiLCJleHAiOjE3MjYyMjY3OTB9.KD1B-mmOeOa-k2wvgaFLMZ7RLwCfhzhB8EBFSFtbJao    "
            +
            "\neyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbklkIjoiYWJjZGVmZzMiLCJleHAiOjE3MjYyMjY4MDd9.bnMXxKmBBSXVufJ8wvI7RDcK62WhrWJdH9abRTxuC-E    "
            +
            "\neyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbklkIjoiYWJjZGVmZzQiLCJleHAiOjE3MjYyMjY4MjB9.pCs2S7lzylPgIxD0t0IQ7bO89GaRZi1QoFcvjIc2W2c",
        version = "v1"),
    servers = {
        @Server(url = "https://asdfdsas.p-e.kr", description = "Default Server"),
        @Server(url = "http://localhost:8080", description = "Local Server"),
        @Server(url = "http://14.42.195.195:8080", description = "Home Server")
    })
@Configuration
public class SwaggerConfig {

    /**
     * http://localhost:8080/swagger-ui/index.html
     */

    // JWT + swagger
    @Bean
    public OpenAPI openAPI() {
        SecurityScheme securityScheme = new SecurityScheme()
            .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
            .in(SecurityScheme.In.HEADER).name("Authorization");
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

        return new OpenAPI()
            .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
            .security(Arrays.asList(securityRequirement));
    }
}