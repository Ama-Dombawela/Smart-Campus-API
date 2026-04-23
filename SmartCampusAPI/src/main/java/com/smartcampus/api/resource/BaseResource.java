/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.api.resource;

import com.smartcampus.api.datastore.CampusDataStore;
import com.smartcampus.api.model.Room;
import com.smartcampus.api.model.Sensor;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Ama Dombawela 
 * UOW No: W2120682 
 * IIT Student No: 20231642
 * 
 * Base Resource - Provides common lookup methods and HTTP response builders
 * shared across all resource classes to avoid code duplication.
 */
public abstract class BaseResource {

    //Look up a Room from the data store by its id
    protected Room findRoom(String roomId) {
        return CampusDataStore.getRooms().get(roomId);
    }

    //Looks up a Sensor from the data store by its id
    protected Sensor findSensor(String sensorId) {
        return CampusDataStore.getSensors().get(sensorId);
    }

    /**
     * Builds a standardised 404 Not Found JSON response
     *
     * @param message - descriptive error message that return to the client
     * @return 404 Not found Response with JSON error body
     */
    protected Response notFoundResponse(String message) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"error\": \"" + message + "\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    /**
     * Builds a standardised 409 Conflict JSON response
     *
     * @param message - descriptive error message that return to the client
     * @return 409 Conflict Response with JSON error body
     */
    protected Response conflictResponse(String message) {
        return Response.status(Response.Status.CONFLICT)
                .entity("{\"error\": \"" + message + "\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    /**
     * Builds a standardised 400 Bad Request JSON response
     *
     * @param message - descriptive error message that return to the client
     * @return 400 Bad Request Response with JSON error body
     */
    protected Response badRequestResponse(String message) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"" + message + "\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    /**
     * Builds a standardised 200 OK JSON response for successful operations
     *
     * @param message - success message that returns to the client
     * @return 200 OK Response with JSON message body
     */
    protected Response successResponse(String message) {
        return Response.ok("{\"message\": \"" + message + "\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    /**
     * Builds a standardised 201 Created JSON response for successful POST
     * operations
     *
     * @param message - success message that returns to the client
     * @return 201 Created Response with JSON message body
     */
    protected Response createdResponse(String message) {
        return Response.status(Response.Status.CREATED)
                .entity("{\"message\": \"" + message + "\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

}
