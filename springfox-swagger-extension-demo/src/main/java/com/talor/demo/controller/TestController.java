package com.talor.demo.controller;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author luffy
 * @version 1.0
 * @className TestController
 * @description TODO
 */
@RestController
@RequestMapping("test")
@Api
public class TestController {

    @GetMapping("display")
    String getDefaultDisplay(){
        return "Hello";
    }
}
