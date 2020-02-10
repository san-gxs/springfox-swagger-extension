package com.talor.core.annotation;

import com.talor.core.config.SpringFoxExtConfig;
import com.talor.core.config.WebMvcSupportConfig;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author luffy
 * @version 1.0
 * @className EnableDubboSwagger
 * @description dubbo and swagger2 annotation
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@EnableWebMvc
@EnableSwagger2
@Import({SpringFoxExtConfig.class, WebMvcSupportConfig.class})
public @interface EnableDubboSwagger {

}
