/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.api.resources;

import com.smartcampus.api.CampusDataStore;
import com.smartcampus.api.model.Room;
import com.smartcampus.api.model.Sensor;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Sensor Resource - Part 3 
 * Manages all sensor operations for the Smart Campus
 * API Base path: /api/v1/sensors
 */
@Path("/sensors")
public class SensorResource {

    /**
     * GET /api/v1/sensors Return all sensors, or filter by type if type is
     * provided
     *
     * @param type - query parameter to filter sensors by type
     * @return list of matching sensors or all sensors
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllSensors(@QueryParam("type") String type) {
        List<Sensor> sensorList = new ArrayList<>(CampusDataStore.getSensors().values());

        // If type param is provided, filter the list
        if (type != null && !type.trim().isEmpty()) {
            List<Sensor> filtered = new ArrayList<>();
            for (Sensor sensor : sensorList) {
                if (sensor.getType() != null && sensor.getType().equalsIgnoreCase(type)) {
                    filtered.add(sensor);
                }
            }
            return Response.ok(filtered).build();
        }
        return Response.ok(sensorList).build();
    }

    /**
     * POST /api/v1/sensors Registers a new sensor and validates that roomId
     * exists
     *
     * @param sensor - the sensor object from the request body
     * @return 400 if required fields are missing, 409 if sensor ID already
     * exists, 422 if roomId not found, 201 on success
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSensor(Sensor sensor) {
        if (sensor == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Sensor payload is required\"}")
                    .build();
        }

        // Checking if sensor ID is provided
        if (sensor.getId() == null || sensor.getId().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Sensor ID is required\"}")
                    .build();
        }

        // Checking if roomId is provided
        if (sensor.getRoomId() == null || sensor.getRoomId().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"roomId is required\"}")
                    .build();
        }

        // Checking if sensor type is provided
        if (sensor.getType() == null || sensor.getType().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Sensor type is required\"}")
                    .build();
        }

        // Check if sensor with same ID already exists
        if (CampusDataStore.getSensors().containsKey(sensor.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\": \"Sensor " + sensor.getId() + " already exists\"}")
                    .build();
        }

        // Validate that the roomId exists
        Room room = CampusDataStore.getRooms().get(sensor.getRoomId());
        if (room == null) {
            return Response.status(422)
                    .entity("{\"error\": \"Room " + sensor.getRoomId() + " does not exist. Cannot register sensor.\"}")
                    .build();
        }

        // Save the sensor and link it to room's sensorIds list
        synchronized (CampusDataStore.class) {
            CampusDataStore.getSensors().put(sensor.getId(), sensor);

            List<String> updatedSensorIds = new ArrayList<>(room.getSensorIds());
            updatedSensorIds.add(sensor.getId());
            room.setSensorIds(updatedSensorIds);
        }

        return Response.status(Response.Status.CREATED)
                .entity("{\"message\": \"Sensor " + sensor.getId() + " created successfully ! \"}")
                .build();
    }

    /**
     * Sub-resource locator for sensor readings - Part 4 
     * Forwards requests for a sensor's readings to SensorReadingResource
     * Path: /api/v1/sensors/{sensorId}/readings
     *
     * @param sensorId - the sensor whose readings are being accessed
     * @return SensorReadingResource instance
     */
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingResource(@PathParam("sensorId") String sensorId) {
        if (!CampusDataStore.getSensors().containsKey(sensorId)) {
            throw new NotFoundException("Sensor " + sensorId + " not found");
        }
        return new SensorReadingResource(sensorId);
    }
}
