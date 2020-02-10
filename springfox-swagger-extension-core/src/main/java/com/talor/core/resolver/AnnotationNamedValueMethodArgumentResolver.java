package com.talor.core.resolver;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
import com.talor.core.util.TypeUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static com.alibaba.fastjson.parser.Feature.*;
import static java.util.Objects.nonNull;

/**
 * @author luffy
 * @version 1.0
 * @className AnnotationNamedValueMethodArgumentResolver
 * @description resolve annotation argument
 */
public class AnnotationNamedValueMethodArgumentResolver extends AbstractNamedValueMethodArgumentResolver {

    private static final String BODY_VALUE_ATTRIBUTE = HandlerMapping.class.getName() + ".bodyValueParams";

    @Override
    protected NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
        return new NamedValueInfo(parameter.getParameterName(), false, null);
    }

    @Override
    protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
        Object paramValue = getParameterValues(name, request);
        if (paramValue == null) {
            return null;
        }
        boolean isStringArray = String[].class == paramValue.getClass();
        if (isStringArray && ((String[]) paramValue).length == 0) {
            return null;
        }
        if (TypeUtils.isBaseType(parameter.getParameterType())) {
            paramValue = isStringArray ? ((String[]) paramValue)[0] : paramValue;
            return com.alibaba.fastjson.util.TypeUtils.cast(paramValue, parameter.getGenericParameterType(), ParserConfig.getGlobalInstance());
        }
        if (TypeUtils.isContainerType(parameter.getParameterType())) {
            paramValue = isStringArray ? "[" + String.join(",", (String[]) paramValue) + "]" : paramValue;
        }
        return JSONObject.parseObject((String) paramValue, parameter.getGenericParameterType(), SupportAutoType, OrderedField);
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasMethodAnnotation(ApiOperation.class);
    }

    private Object getParameterValues(String name, NativeWebRequest request) throws IOException {
        Object values = request.getParameterValues(name);
        if (values == null) {
            JSONObject body = (JSONObject) request.getAttribute(BODY_VALUE_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
            if (body == null) {
                HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);
                body = JSONObject.parseObject(readBody(servletRequest), DisableSpecialKeyDetect, OrderedField);
                request.setAttribute(BODY_VALUE_ATTRIBUTE, body, RequestAttributes.SCOPE_REQUEST);
            }
            if (body == null) {
                return null;
            }
            Object param = body.get(name);
            if (param instanceof JSONArray) {
                return ((JSONArray) param).toJSONString();
            }
            return body.getString(name);
        }
        return values;
    }

    private String readBody(ServletRequest request) throws IOException {
        String str;
        StringBuilder wholeStr = new StringBuilder();
        while ((str = request.getReader().readLine()) != null) {
            wholeStr.append(str);
        }
        return wholeStr.toString();
    }
}
