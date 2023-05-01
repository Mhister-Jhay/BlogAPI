package com.blog.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
@Configuration
public class SwaggerConfig {
    @Value("${application.version}")
    private String version;

    @Bean
    public OpenAPI api(){
        SecurityScheme jwtSecurityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("jwt", jwtSecurityScheme))
                .info(new Info()
                        .title("Blog App Documentation")
                        .description("Blog App API documentation")
                        .version(version))
                .security(Collections.singletonList(new SecurityRequirement().addList("jwt")));
    }
    @Bean
    public GroupedOpenApi usersEndpoint(){
        return  GroupedOpenApi
                .builder()
                .group("Users")
                .pathsToMatch("/api/users/**").build();
    }

    @Bean
    public GroupedOpenApi adminEndpoint(){
        return  GroupedOpenApi
                .builder()
                .group("Admin")
                .pathsToMatch("/api/admin/**").build();
    }
    @Bean
    public GroupedOpenApi superAdminEndpoint(){
        return  GroupedOpenApi
                .builder()
                .group("Super_Admin")
                .pathsToMatch("/api/superAdmin/**").build();
    }
    @Bean
    public GroupedOpenApi authEndpoint(){
        return  GroupedOpenApi
                .builder()
                .group("Authentication")
                .pathsToMatch("/api/auth/**").build();
    }

}
