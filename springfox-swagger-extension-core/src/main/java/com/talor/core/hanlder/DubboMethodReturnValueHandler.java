package com.talor.core.hanlder;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.util.Collections;
import java.util.Objects;

import static com.talor.core.config.WebMvcSupportConfig.fastJsonHttpMessageConverter;
import static java.util.Collections.emptyList;

/**
 * @author luffy
 * @version 1.0
 * @className DubboMethodReturnValueHandler
 * @description custom method return value
 */
public class DubboMethodReturnValueHandler implements HandlerMethodReturnValueHandler {

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.hasMethodAnnotation(Api.class);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        if (Objects.isNull(returnValue)) {
            returnValue = returnType.getParameterType() == void.class ? "success" : new JSONObject();
        }
        new RequestResponseBodyMethodProcessor(Collections.singletonList(fastJsonHttpMessageConverter()), emptyList()).handleReturnValue(returnValue, returnType, mavContainer, webRequest);
    }
}
