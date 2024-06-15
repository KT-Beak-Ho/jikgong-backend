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
            "\neyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbklkIjoiYWJjZGVmZzAiLCJleHAiOjE3MjA5NzA5MTB9.yg1G0zizw-BoGxcWGUPNgudBhi7CLsM1359eHYMOujQ    "
            +
            "\n노동자 계정: 아래 4개    " +
            "\neyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbklkIjoiYWJjZGVmZzEiLCJleHAiOjE3MjA5NzA3OTJ9.mhV9FqhLONb5uohaA8FrTEY45DFFEc5qYsDjpQD5PH8    "
            +
            "\neyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbklkIjoiYWJjZGVmZzIiLCJleHAiOjE3MjA5NzA4NjJ9.xy7PZ5iJ95zBkbjS8qUQH9jP4TWj6t4sAXYZMqJp5js    "
            +
            "\neyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbklkIjoiYWJjZGVmZzMiLCJleHAiOjE3MjA5NzA4Nzl9.aDbyOOZ5_n69hcLNIAsjKa68xalsx8D8_cucljce3fw    "
            +
            "\neyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbklkIjoiYWJjZGVmZzQiLCJleHAiOjE3MjA5NzA4OTZ9.zXCpelBa3gmy35bpDCmn_y1KzQdzUNm6K9o7fzk8VQU",
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