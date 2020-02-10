package com.talor.core.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.google.common.collect.Lists;
import com.talor.core.hanlder.DubboMethodReturnValueHandler;
import com.talor.core.mapping.DubboHandlerMethodMapping;
import com.talor.core.resolver.AnnotationNamedValueMethodArgumentResolver;
import com.talor.core.resolver.DubboMethodArgumentResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ViewNameMethodReturnValueHandler;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.Collections;
import java.util.List;

import static com.alibaba.fastjson.parser.Feature.*;

/**
 * @author luffy
 * @version 1.0
 * @className WebMvcSupportConfig
 * @description
 */
@Configuration
public class WebMvcSupportConfig extends WebMvcConfigurerAdapter {

    public static FastJsonHttpMessageConverter fastJsonHttpMessageConverter() {
        FastJsonConfig config = new FastJsonConfig();
        config.setSerializerFeatures(
                SerializerFeature.PrettyFormat,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.DisableCircularReferenceDetect,
                SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteDateUseDateFormat);
        config.setFeatures(DisableSpecialKeyDetect, SupportAutoType, OrderedField);
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        converter.setFastJsonConfig(config);
        converter.setSupportedMediaTypes(Lists.newArrayList(MediaType.APPLICATION_JSON));
        return converter;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("/api/**")
                .addResourceLocations("classpath:/META-INF/resources/static/");
        super.addResourceHandlers(registry);
    }


    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.viewResolver(new InternalResourceViewResolver());
        registry.order(1);
        super.configureViewResolvers(registry);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new DubboMethodArgumentResolver(Collections.singletonList(fastJsonHttpMessageConverter())));
        argumentResolvers.add(new AnnotationNamedValueMethodArgumentResolver());
        super.addArgumentResolvers(argumentResolvers);
    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
        returnValueHandlers.add(new DubboMethodReturnValueHandler());
        super.addReturnValueHandlers(returnValueHandlers);
    }

    @Bean
    public DubboHandlerMethodMapping dubboHandlerMethodMapping(RequestMappingHandlerAdapter requestMappingHandlerAdapter) {
        requestMappingHandlerAdapter.setReturnValueHandlers(adjustHandlerMethodReturnValueHandlerOrder(requestMappingHandlerAdapter.getReturnValueHandlers()));
        return new DubboHandlerMethodMapping();
    }

    private List<HandlerMethodReturnValueHandler> adjustHandlerMethodReturnValueHandlerOrder(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
        List<HandlerMethodReturnValueHandler> handlers = Lists.newArrayList();
        for (HandlerMethodReturnValueHandler returnValueHandler : returnValueHandlers) {
            if (returnValueHandler instanceof ViewNameMethodReturnValueHandler) {
                handlers.add(new DubboMethodReturnValueHandler());
            }
            handlers.add(returnValueHandler);
        }
        return handlers;
    }

}
