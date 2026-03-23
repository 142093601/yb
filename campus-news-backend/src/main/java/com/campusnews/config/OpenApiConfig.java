package com.campusnews.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / Knife4j 文档配置
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI campusNewsOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("校园新闻系统 API")
                        .description("校园新闻系统后端接口文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Campus News")));
    }
}
