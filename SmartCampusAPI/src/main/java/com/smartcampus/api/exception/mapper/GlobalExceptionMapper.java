/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.api.exception.mapper;

import com.smartcampus.api.model.ErrorMessage;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author User
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        if (exception instanceof WebApplicationException) {
            return ((WebApplicationException) exception).getResponse();
        }

        // Log the real error server-side but never expose it to the client
        System.err.println("[500] Internal error: " + exception.getMessage());

        ErrorMessage errorMessage = new ErrorMessage(
                "Internal Server Error - Something went wrong on our end. Please try again later.",
                500
        );
        return Response.status(Status.INTERNAL_SERVER_ERROR) //500
                .entity(errorMessage)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

}
