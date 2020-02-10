package com.talor.core.mapping;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import springfox.documentation.spi.service.contexts.Defaults;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author luffy
 * @version 1.0
 * @className DubboHandlerMethodMapping
 * @description to custom api mapping
 */
@Slf4j
public class DubboHandlerMethodMapping<T> extends AbstractHandlerMethodMapping<RequestMappingInfo> {

    private ApplicationContext applicationContext;

    @Override
    protected void initApplicationContext(ApplicationContext context) {
        super.initApplicationContext(context);
        this.applicationContext = context;
    }


    @Override
    protected boolean isHandler(Class<?> beanType) {
        return org.apache.dubbo.config.spring.ServiceBean.class.isAssignableFrom(beanType) ||
                com.alibaba.dubbo.config.spring.ServiceBean.class.isAssignableFrom(beanType) ||
                AnnotatedElementUtils.hasAnnotation(beanType, com.alibaba.dubbo.config.annotation.Service.class) ||
                AnnotatedElementUtils.hasAnnotation(beanType, org.apache.dubbo.config.annotation.Service.class)
                ;
    }

    @Override
    protected void detectHandlerMethods(Object handler) {

        if (handler instanceof String) {
            String beanName = (String) handler;
            handler = getApplicationContext().getBean(beanName);
        }
        Class<?> interfaceType = ClassUtils.getUserClass(handler.getClass());
        Map<Method, RequestMappingInfo> methods = MethodIntrospector.selectMethods(interfaceType,
                (MethodIntrospector.MetadataLookup<RequestMappingInfo>) method -> getMappingForMethod(method, interfaceType));

        for (Map.Entry<Method, RequestMappingInfo> entry : methods.entrySet()) {
            Method invocableMethod = AopUtils.selectInvocableMethod(entry.getKey(), interfaceType);
            RequestMappingInfo mapping = entry.getValue();
            registerHandlerMethod(handler, invocableMethod, mapping);
        }

    }

    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> interfaceType) {
        if (!Modifier.isStatic(method.getModifiers())
                && Modifier.isPublic(method.getModifiers())
                && support(method, interfaceType)) {
            return createRequestMappingInfo(method, interfaceType);
        }
        return null;
    }



    @Override
    protected Set<String> getMappingPathPatterns(RequestMappingInfo info) {
        return info.getPatternsCondition().getPatterns();
    }

    @Override
    protected RequestMappingInfo getMatchingMapping(RequestMappingInfo info, HttpServletRequest request) {
        return info.getMatchingCondition(request);
    }

    @Override
    protected Comparator<RequestMappingInfo> getMappingComparator(HttpServletRequest request) {
        return (info1, info2) -> info1.compareTo(info2, request);
    }


    private RequestMappingInfo createRequestMappingInfo(Method method, Class<?> interfaceType) {
        Class<?> aClass = applicationContext.getBean(interfaceType).getClass();
        Annotation[] annotations = aClass.getAnnotations();

        Optional<Annotation> classPathAnnotation = Arrays.stream(annotations).filter(f->f instanceof Path).findFirst();
        if (!classPathAnnotation.isPresent()){
            log.warn("class {} doesn't define path annotation", aClass.getName());
            return null;
        }
        Path classPath = (Path)classPathAnnotation.get();
        Method[] methods = aClass.getDeclaredMethods();
        Optional<Method> methodOptional = Arrays.stream(methods).filter(f -> f.getName().equals(method.getName())
                && Arrays.stream(f.getDeclaredAnnotations()).anyMatch(a -> a instanceof Path)).findFirst();
        if(!methodOptional.isPresent()){
            log.warn("method {} doesn't define path annotation", method.getName());
            return null;
        }
        Annotation[] declaredAnnotations = methodOptional.get().getDeclaredAnnotations();
        Optional<Annotation> methodPathAnnotation = Arrays.stream(declaredAnnotations).filter(f -> f instanceof Path).findFirst();
        Path methodPath = (Path)methodPathAnnotation.get();
        Annotation[] interfaceAnnotation = findInterfaceClass(method, interfaceType);
        Annotation[] distinctAnnotations = getDistinctAnnotations(declaredAnnotations, interfaceAnnotation);
        RequestMappingInfo.Builder builder = RequestMappingInfo
                .paths(classPath.value() + "/" + methodPath.value())
                .methods(findRequestMethod(distinctAnnotations))
                .consumes(findMethodConsumes(distinctAnnotations))
                .produces(findMethodProduces(distinctAnnotations));
        return builder.build();
    }


    private Annotation[] findInterfaceClass(Method method, Class<?> cls){
        Class<?>[] clsInterfaces = cls.getInterfaces();
//        final Set<Class> ignorableParameterTypes = new Defaults().defaultIgnorableParameterTypes();
//        final Set<Class<?>> collect = Arrays.stream(method.getParameterTypes()).filter(p -> !ignorableParameterTypes.contains(p)).collect(Collectors.toSet());
        Optional<Class<?>> first = Arrays.stream(clsInterfaces).parallel().filter(f -> {
            try {
                f.getDeclaredMethod(method.getName(), method.getParameterTypes());
                return true;
            } catch (NoSuchMethodException e) {
                log.warn("NoSuchMethodException match interface : {},method: {} fail", f.getTypeName(), method.getName());
                return false;
            }

        }).findFirst();

        if (first.isPresent()){
            try {
                return first.get().getDeclaredMethod(method.getName(), method.getParameterTypes()).getDeclaredAnnotations();
            } catch (Exception e) {

            }
        }
        return new Annotation[]{};
    }

    private Annotation[] getDistinctAnnotations(Annotation[] a, Annotation[] b){
        HashSet<Annotation> annotationHashSet = new HashSet<>();
        annotationHashSet.addAll(Arrays.asList(a));
        annotationHashSet.addAll(Arrays.asList(b));
        return annotationHashSet.toArray(new Annotation[annotationHashSet.size()]);
    }

    private RequestMethod findRequestMethod(Annotation[] declaredAnnotations){
        boolean anyMatch = Arrays.stream(declaredAnnotations).anyMatch(f -> Arrays.stream(f.annotationType().getAnnotations()).anyMatch(a -> a instanceof HttpMethod));
        if (!anyMatch){
            return RequestMethod.POST;
        }
        Optional<Annotation> first = Arrays.stream(declaredAnnotations).filter(f -> Arrays.stream(f.annotationType().getAnnotations()).anyMatch(i -> i instanceof HttpMethod)).findFirst();
        if (!first.isPresent()) {
            return RequestMethod.POST;
        }
        String className = first.get().annotationType().getName();
        String name = className.substring(className.lastIndexOf('.') + 1);
        Optional<RequestMethod> requestMethod = Arrays.stream(RequestMethod.values()).filter(f -> f.name().equals(name)).findFirst();

        return requestMethod.orElse(RequestMethod.POST);
    }

    private String[] findMethodConsumes(Annotation[] declaredAnnotations){
        Optional<Annotation> first = Arrays.stream(declaredAnnotations).filter(f -> f instanceof Consumes).findFirst();
        if(first.isPresent()){
            return ((Consumes)first.get()).value();
        }
        return new String[]{MediaType.APPLICATION_JSON};
    }

    private String[] findMethodProduces(Annotation[] declaredAnnotations){
        Optional<Annotation> first = Arrays.stream(declaredAnnotations).filter(f -> f instanceof Produces).findFirst();
        if(first.isPresent()){
            return ((Produces)first.get()).value();
        }
        return new String[]{MediaType.APPLICATION_JSON};
    }

    private boolean support(Method method, Class interfaceType) {
        return AnnotatedElementUtils.hasAnnotation(interfaceType, Api.class) ;
    }
}
