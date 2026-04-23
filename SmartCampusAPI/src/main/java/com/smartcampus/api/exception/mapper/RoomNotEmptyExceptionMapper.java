/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.api.exception.mapper;

import com.smartcampus.api.exception.RoomNotEmptyException;
import com.smartcampus.api.model.ErrorMessage;
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
 * Room Not Empty Exception Mapper - Maps RoomNotEmptyException to HTTP 409 Conflict
 */
@Provider
public class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException> {

    @Override
    public Response toResponse(RoomNotEmptyException exception) {

        ErrorMessage errorMessage = new ErrorMessage(
                exception.getMessage(),
                409
        );
        return Response.status(Status.CONFLICT) //409
                .entity(errorMessage)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

}
