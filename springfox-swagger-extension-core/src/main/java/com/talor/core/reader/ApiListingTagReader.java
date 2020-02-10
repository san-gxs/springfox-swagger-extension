package com.talor.core.reader;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import io.swagger.annotations.Api;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ApiListingBuilderPlugin;
import springfox.documentation.spi.service.contexts.ApiListingContext;

/**
 * @author luffy
 * @version 1.0
 * @className ApiListingTagReader
 * @description define apiList info
 */
public class ApiListingTagReader implements ApiListingBuilderPlugin {

    @Override
    public void apply(ApiListingContext apiListingContext) {
        Optional<? extends Class<?>> controllerClass = apiListingContext.getResourceGroup().getControllerClass();
        if (!controllerClass.isPresent()) {
            return;
        }
        java.util.Optional<Api> apiOptional = java.util.Optional.ofNullable(AnnotationUtils.getAnnotation(controllerClass.get(), Api.class));

        if (!apiOptional.isPresent()) {
            return;
        }
        Api api = apiOptional.get();
        String tags;
        if (api.tags().length == 0 || StringUtils.isEmpty(tags = api.tags()[0])) {
            tags = apiListingContext.getResourceGroup().getGroupName();
        }
        apiListingContext.apiListingBuilder()
                .tagNames(Sets.newHashSet(tags));
    }

    @Override
    public boolean supports(DocumentationType documentationType) {
        return true;
    }
}
