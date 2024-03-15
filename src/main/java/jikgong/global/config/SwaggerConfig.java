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
                        "\neyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbklkIjoiYWJjZGVmZzAiLCJleHAiOjE3MTU2OTY0NDh9.KdnJdTOzOsCD_AJi8SJOpmiRaO4mrAXVPswvCCzRMoI    " +
                        "\n노동자 계정: 아래 4개    " +
                        "\neyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbklkIjoiYWJjZGVmZzEiLCJleHAiOjE3MTU2OTY0NzJ9.EcdUqjdAYuT_n_dMBzecHIg-ZhXl1g-LEb7ZRZtK4VA    " +
                        "\neyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbklkIjoiYWJjZGVmZzIiLCJleHAiOjE3MTU2OTY0ODZ9.hwiv1cnkT_Juk502j_gkNK6BMsLXb3WuyO8Rqu0nrsk    " +
                        "\neyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbklkIjoiYWJjZGVmZzMiLCJleHAiOjE3MTU2OTY0OTl9.oAawfLzQJWiu4YcmXyuisHctAZjIymp0PXEW6ie2rNw    " +
                        "\neyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbklkIjoiYWJjZGVmZzQiLCJleHAiOjE3MTU2OTY1MTV9.rtKD3CCySX-ddS1xejyVlIn46cFTOVy4NbFfntPAefA",
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