package com.talor.demo.service;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author luffy
 * @version 1.0
 * @className SayHi
 * @description TODO
 */
@Api
public interface SayHi {

    @ApiOperation(value = "echoByInput")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_ATOM_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_ATOM_XML})
    String echoByInput(String s, BindingResult bindingResult);
}
