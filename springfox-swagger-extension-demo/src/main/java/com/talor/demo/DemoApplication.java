package com.talor.demo;

import com.talor.core.annotation.EnableDubboSwagger;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.apache.dubbo.config.spring.context.annotation.EnableDubboConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author luffy
 * @version 1.0
 * @className DemoApplication
 * @description TODO
 */
@SpringBootApplication
@EnableDubbo
@EnableDubboConfig
@EnableDubboSwagger
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
