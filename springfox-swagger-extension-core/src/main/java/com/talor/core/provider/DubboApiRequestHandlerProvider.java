package com.talor.core.provider;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.talor.core.hanlder.DubboApiRequestHandler;
import com.talor.core.mapping.DubboHandlerMethodMapping;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.BuilderDefaults;
import springfox.documentation.spi.service.RequestHandlerProvider;
import springfox.documentation.spi.service.contexts.Orderings;
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver;

import java.util.List;
import java.util.Map;

/**
 * @author luffy
 * @version 1.0
 * @className DubboApiRequestHandlerProvider
 * @description when application start, that register custom handler
 */
public class DubboApiRequestHandlerProvider implements RequestHandlerProvider, InitializingBean, ApplicationContextAware {

    private HandlerMethodResolver handlerMethodResolver;

    private TypeResolver typeResolver;

    private List<DubboHandlerMethodMapping> methodMappings = Lists.newArrayList();

    private ApplicationContext applicationContext;

    public DubboApiRequestHandlerProvider(HandlerMethodResolver handlerMethodResolver, TypeResolver typeResolver) {
        this.handlerMethodResolver = handlerMethodResolver;
        this.typeResolver = typeResolver;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        methodMappings.addAll(applicationContext.getBeansOfType(DubboHandlerMethodMapping.class).values());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public List<RequestHandler> requestHandlers() {
        return Orderings.byPatternsCondition()
                .sortedCopy(FluentIterable.from(BuilderDefaults.nullToEmptyList(methodMappings))
                    .transformAndConcat(toMappingEntries())
                    .transform(toRequestHandler())
                );
    }

    private Function<? super DubboHandlerMethodMapping,
                Iterable<Map.Entry<RequestMappingInfo, HandlerMethod>>> toMappingEntries() {
        return (Function<DubboHandlerMethodMapping, Iterable<Map.Entry<RequestMappingInfo, HandlerMethod>>>) input -> input.getHandlerMethods().entrySet();
    }

    private Function<Map.Entry<RequestMappingInfo, HandlerMethod>, RequestHandler> toRequestHandler() {
        return input -> new DubboApiRequestHandler(handlerMethodResolver, typeResolver, input.getKey(), input.getValue());
    }
}
