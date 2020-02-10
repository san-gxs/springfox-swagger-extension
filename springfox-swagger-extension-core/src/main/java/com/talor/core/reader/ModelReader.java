package com.talor.core.reader;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelContext;

/**
 * @author luffy
 * @version 1.0
 * @className ModelReader
 * @description TODO
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 1001)
public class ModelReader implements ModelBuilderPlugin {
    @Override
    public void apply(ModelContext modelContext) {
        modelContext.getBuilder().name(modelContext.getType().getTypeName()).build();
    }

    @Override
    public boolean supports(DocumentationType documentationType) {
        return true;
    }
}
