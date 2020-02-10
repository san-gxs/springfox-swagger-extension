package com.talor.demo.service;

import org.apache.dubbo.config.annotation.Service;
import org.springframework.validation.BindingResult;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * @author luffy
 * @version 1.0
 * @className SayHiImpl
 * @description TODO
 */
@Service(protocol = {"dubbo", "rest"}, interfaceClass = SayHi.class)
@org.springframework.stereotype.Service()
@Path("sayHi")
public class SayHiImpl implements SayHi {

    @Override
    @Path("echo")
    @GET
    public String echoByInput(String s, BindingResult bindingResult) {
        return s;
    }

}
