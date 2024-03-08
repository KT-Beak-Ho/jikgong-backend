package jikgong.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@OpenAPIDefinition(
        info = @Info(title = "jikgong API 명세서",
                description = "기업 계정: 아래 1개   " +
                        "\neyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJwaG9uZSI6IjAxMDEyMzQ1Njc4IiwiZXhwIjoxNzE1MDYzMDAyfQ.iCyhNWDzyP4pjGUaV37bV4jNY13QyFiigyBIn1I0jTs    " +
                        "\n노동자 계정: 아래 4개    " +
                        "\neyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJwaG9uZSI6IjAxMDExMTExMTExIiwiZXhwIjoxNzE1MDYyODAxfQ.mEyrVB4gvQb5fUgtB6Z8iFhYgdPyYENSmg8Y8FM8IXA    " +
                        "\neyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJwaG9uZSI6IjAxMDIyMjIyMjIyIiwiZXhwIjoxNzE1MDYyODI3fQ.nwI6dub7Pv6e-k_jKiF62IjeeAVKavTnQRf8c2w9gAg    " +
                        "\neyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJwaG9uZSI6IjAxMDMzMzMzMzMzIiwiZXhwIjoxNzE1MDYyODQwfQ.XlO3rkTM_loSw8QMvGGmr07N5UdUoLOEQ36QBthg1Q8    " +
                        "\neyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJwaG9uZSI6IjAxMDQ0NDQ0NDQ0IiwiZXhwIjoxNzE1MDYyODcwfQ.-7Fe9BmZlx13GF_zgZOALJpwwXrpJpLhN76KcxLKmBA",
                version = "v1"))
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