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
            "\neyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbklkIjoiYWJjZGVmZzAiLCJleHAiOjE3Mzc3MDMyMTZ9.c-UN8qz7EzBrIDk7Mj03ohW7yXcDxcD6xtjyNV4__9Y    "
            +
            "\n노동자 계정: 아래 4개    " +
            "\neyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbklkIjoiYWJjZGVmZzEiLCJleHAiOjE3Mzc3MDMyNDl9.QQcmJqutM61IqFTe-a4BPL8QCrnHdQGx9kOcoLw9yEQ    "
            +
            "\neyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbklkIjoiYWJjZGVmZzIiLCJleHAiOjE3Mzc3MDMyNjN9.PzT8hs8T9K6EpEZ1C4fD_rU_2Q91ezdJOUEJX8xqv9A    "
            +
            "\neyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbklkIjoiYWJjZGVmZzMiLCJleHAiOjE3Mzc3MDMyNzh9.nUuAmr330GYeyHO6UUffBQpIxn8civq6cD6C6kKIB1E    "
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