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
 * @author Ama Dombawela
 * UOW No: W2120682
 * IIT Student No: 20231642
 *
 * Global Exception Mapper - Catches all unhandled exceptions and returns HTTP 500
 * Prevents stack traces from being exposed to clients
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    /**
     * Maps any unhandled Throwable to a proper JSON error response
     * If exception is a WebApplicationException, preserves the original status code
     * otherwise returns HTTP 500 Internal Server Error
     * @param exception - the caught exception
     * @return JSON error response with appropriate status code
     */
    @Override
    public Response toResponse(Throwable exception) {
        if (exception instanceof WebApplicationException) {
            Response originalResponse = ((WebApplicationException) exception).getResponse();
            // Get status code from original response or default to 500
            int statusCode = originalResponse != null
                    ? originalResponse.getStatus()
                    : Status.INTERNAL_SERVER_ERROR.getStatusCode();

            // Get message from exception or fall back to status reason phrase
            String message = exception.getMessage();
            if (message == null || message.trim().isEmpty()) {
                message = originalResponse != null
                        ? originalResponse.getStatusInfo().getReasonPhrase()
                        : Status.INTERNAL_SERVER_ERROR.getReasonPhrase();
            }

            //Return Structures JSON error response
            ErrorMessage errorMessage = new ErrorMessage(message, statusCode);
            return Response.status(statusCode)
                    .entity(errorMessage)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        // Log the real error server-side but never expose it to the client
        System.err.println("[500] Internal error: " + exception.getMessage());

        //Return generic 500 error
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
