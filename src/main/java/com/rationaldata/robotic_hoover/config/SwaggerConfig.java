package com.rationaldata.robotic_hoover.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Robotic Hoover API")
                        .version("1.0")
                        .description("API for controlling a robotic hoover and tracking cleaning progress"));
    }
}
