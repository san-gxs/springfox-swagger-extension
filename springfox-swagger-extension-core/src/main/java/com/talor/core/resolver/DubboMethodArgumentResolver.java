package com.talor.core.resolver;

import com.talor.core.util.TypeUtils;
import io.swagger.annotations.Api;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.util.List;

import static java.util.Collections.emptyList;

/**
 * @author luffy
 * @version 1.0
 * @className DubboMethodArgumentResolver
 * @description resolver dubbo api argument
 */
public class DubboMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private final RequestResponseBodyMethodProcessor responseBodyMethodProcessor;
    private final AnnotationNamedValueMethodArgumentResolver annotationResolver;

    public DubboMethodArgumentResolver(List<HttpMessageConverter<?>> converters) {
        responseBodyMethodProcessor = new RequestResponseBodyMethodProcessor(converters, emptyList());
        annotationResolver = new AnnotationNamedValueMethodArgumentResolver();
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasMethodAnnotation(Api.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        if (parameter.getMethod().getParameterCount() == 1 && TypeUtils.isComplexObjectType(parameter.getParameterType())) {
            return responseBodyMethodProcessor.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        } else {
            return annotationResolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        }
    }
}
