package com.talor.core.provider;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Optional;
import com.talor.core.util.TypeUtils;
import io.swagger.annotations.ApiModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationModelsProviderPlugin;
import springfox.documentation.spi.service.contexts.OperationModelContextsBuilder;
import springfox.documentation.spi.service.contexts.RequestMappingContext;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author luffy
 * @version 1.0
 * @className ApiMethodModelsProvider
 * @description custom method return model
 */
public class ApiMethodModelsProvider implements OperationModelsProviderPlugin {

    private final TypeResolver typeResolver;

    @Autowired
    public ApiMethodModelsProvider(TypeResolver typeResolver) {
        this.typeResolver = typeResolver;
    }

    @Override
    public void apply(RequestMappingContext context) {
        collectFromReturnType(context);
    }

    private void collectFromReturnType(RequestMappingContext context) {
        ResolvedType modelType = context.alternateFor(context.getReturnType());
        context.operationModelsBuilder().addReturn(modelType);
    }

    private List<ResolvedType> collectAllTypes(RequestMappingContext context, ResolvedMethodParameter parameter) {
        List<ResolvedType> allTypes = newArrayList();
        for (ResolvedType type : collectBindingTypes(context.alternateFor(parameter.getParameterType()), newArrayList())) {
            ApiModel apiModel = AnnotationUtils.getAnnotation(type.getErasedType(), ApiModel.class);
            allTypes.add(type);
            if (apiModel != null) {
                allTypes.addAll(Arrays.stream(apiModel.subTypes())
                        .filter(subType -> subType.getAnnotation(ApiModel.class) != type.getErasedType().getAnnotation(ApiModel.class))
                        .map(typeResolver::resolve).collect(Collectors.toList()));
            }
        }
        return allTypes;
    }

    private List<ResolvedType> collectBindingTypes(ResolvedType type, List<ResolvedType> types) {
        if (TypeUtils.isComplexObjectType(type.getErasedType())) {
            types.add(type);
        }
        if (TypeUtils.isBaseType(type.getErasedType())
                || type.getTypeBindings().isEmpty()) {
            return types;
        }
        for (ResolvedType resolvedType : type.getTypeBindings().getTypeParameters()) {
            collectBindingTypes(resolvedType, types);
        }
        return types;
    }

    @Override
    public boolean supports(DocumentationType delimiter) {
        return true;
    }
}
