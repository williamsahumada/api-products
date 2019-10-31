package com.test.api.product.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.test.api.product"))
                .paths(PathSelectors.regex("/teste/api/products/.*"))
                .build()
                .apiInfo(getApiInformation())
                .useDefaultResponseMessages(false)
                ;
    }

    private ApiInfo getApiInformation() {
        return new ApiInfo("Products API",
                "Products API Rest",
                "1.0.0",
                null,
                null,
                null,
                null,
                Collections.emptyList()
        );
    }
}