/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.api.exception.mapper;

import com.smartcampus.api.exception.LinkedResourceNotFoundException;
import com.smartcampus.api.model.ErrorMessage;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @author Ama Dombawela
 * UOW No: W2120682
 * IIT Student No: 20231642
 *
 * Linked Resource Not Found Exception Mapper - Maps LinkedResourceNotFoundException to HTTP 422
 */
@Provider
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Override
    public Response toResponse(LinkedResourceNotFoundException exception) {
        ErrorMessage errorMessage = new ErrorMessage(
                exception.getMessage(),
                422
        );
        return Response.status(422) //422
                .entity(errorMessage)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

}
