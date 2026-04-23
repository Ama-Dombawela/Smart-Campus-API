/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.api.exception.mapper;

import com.smartcampus.api.exception.SensorUnavailableException;
import com.smartcampus.api.model.ErrorMessage;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author User
 */
@Provider
public class SensorUnavailableExceptionMapper implements ExceptionMapper<SensorUnavailableException> {

    @Override
    public Response toResponse(SensorUnavailableException exception) {

        ErrorMessage errorMessage = new ErrorMessage(
                exception.getMessage(),
                403
        );
        return Response.status(Response.Status.FORBIDDEN) //403
                .entity(errorMessage)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

}
