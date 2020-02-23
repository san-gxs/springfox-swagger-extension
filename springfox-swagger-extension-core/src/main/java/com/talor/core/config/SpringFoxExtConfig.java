package com.talor.core.config;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Predicates;
import com.talor.core.provider.ApiMethodModelsProvider;
import com.talor.core.provider.DubboApiRequestHandlerProvider;
import com.talor.core.reader.ApiListingTagReader;
import com.talor.core.reader.ModelReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.AnnotatedTypeMetadata;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver;
import springfox.documentation.swagger2.configuration.Swagger2DocumentationConfiguration;

/**
 * @author luffy
 * @version 1.0
 * @className SpringFoxExtConfig
 * @description spring-fox extension configuration
 */

@Configuration
public class SpringFoxExtConfig {

    @Bean
    public Docket controllerApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(buildApiInfo())
                .groupName("extension")
                .select()
                .paths(Predicates.not(PathSelectors.regex("/error.*")))
                .build();
    }

    private ApiInfo buildApiInfo() {
        return new ApiInfoBuilder()
                .title("api")
                .description("see api details when you need")
                .contact(new Contact("api-docs", null, null))
                .version("version:1.0")
                .build();
    }

//    @Bean
//    @Primary
//    public HandlerMethodResolver methodResolver(TypeResolver resolver) {
//        return new HandlerMethodResolver(resolver);
//    }
//
//    @Bean
//    public static ObjectMapperConfigurer objectMapperConfigurer() {
//        return new ObjectMapperConfigurer();
//    }
//
//    @Bean
//    public Defaults defaults() {
//        return new Defaults();
//    }


//    @Bean
//    public DocumentationCache resourceGroupCache() {
//        return new DocumentationCache();
//    }

//    @Bean
//    public JsonSerializer jsonSerializer(List<JacksonModuleRegistrar> moduleRegistrars) {
//        return new JsonSerializer(moduleRegistrars);
//    }

//    @Bean
//    public JacksonModuleRegistrar swagger2Module() {
//        return new Swagger2JacksonModule();
//    }
//
//    @Bean
//    public DescriptionResolver descriptionResolver(Environment environment) {
//        return new DescriptionResolver(environment);
//    }


    @Bean
    public ApiListingTagReader apiListingTagReader() {
        return new ApiListingTagReader();
    }

    @Bean
    public ModelReader modelReader() {
        return new ModelReader();
    }

    @Bean
    public DubboApiRequestHandlerProvider apiRequestHandlerProvider(HandlerMethodResolver methodResolver, TypeResolver typeResolver) {
        return new DubboApiRequestHandlerProvider(methodResolver, typeResolver);
    }

    @Bean
    public ApiMethodModelsProvider apiMethodModelsProvider(TypeResolver typeResolver) {
        return new ApiMethodModelsProvider(typeResolver);
    }


    static final class EnableSwagger2MissingConditional implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            // Non-spring Boot applications do not add @EnableSwagger2 to enable this configuration
            return !context.getRegistry().containsBeanDefinition(Swagger2DocumentationConfiguration.class.getName());
        }
    }

}
