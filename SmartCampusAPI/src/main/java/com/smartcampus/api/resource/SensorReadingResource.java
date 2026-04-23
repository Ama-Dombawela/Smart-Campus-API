package com.smartcampus.api.resource;

import com.smartcampus.api.CampusDataStore;
import com.smartcampus.api.exception.SensorUnavailableException;
import com.smartcampus.api.model.Sensor;
import com.smartcampus.api.model.SensorReading;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Sensor Reading Resource - Part 4 Manages reading operations for a specific
 * sensor
 */
public class SensorReadingResource extends BaseResource {

    private final String sensorId;

    /**
     * Constructor for sub-resource locator.
     *
     * @param sensorId - parent sensor ID
     */
    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    /**
     * GET /api/v1/sensors/{sensorId}/readings
     *
     * @return all readings for the sensor, or an empty list if none exist
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReadings() {
        List<SensorReading> readingList = CampusDataStore.getReadings().get(sensorId);
        if (readingList == null) {
            readingList = new ArrayList<>();
        }

        //Sort the sensor readings in ascending order based on timestamp (oldest to newest)
        readingList.sort((a, b) -> Long.compare(a.getTimestamp(), b.getTimestamp()));
        return Response.ok(readingList).build();
    }

    /**
     * POST /api/v1/sensors/{sensorId}/readings Adds a new reading for the
     * sensor and updates sensor currentValue.
     *
     * @param reading - sensor reading payload
     * @return 400 if reading payload/value is invalid, 404 if sensor not found,
     * 201 on success
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)

    public Response addReading(SensorReading reading) {
        // Checking if reading payload is provided
        if (reading == null) {
            return badRequestResponse("Reading payload is required.");
        }

        // Checking if reading value is valid
        if (Double.isNaN(reading.getValue()) || Double.isInfinite(reading.getValue())) {
            return badRequestResponse("Reading value is required.");
        }

        Sensor sensor = findSensor(sensorId);
        if (sensor == null) {
            return notFoundResponse("Sensor " + sensorId + " not found.");
        }

        //Block new readings if sensor is under MAINTENANCE(403 Forbidden)
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException("Sensor " + sensorId + " is under MAINTENANCE and cannot accept new readings.");
        }
        reading.setId(UUID.randomUUID().toString());
        reading.setTimestamp(System.currentTimeMillis());

        synchronized (CampusDataStore.class) {
            List<SensorReading> existing = CampusDataStore.getReadings().get(sensorId);
            List<SensorReading> updated = existing == null ? new ArrayList<>() : new ArrayList<>(existing);
            updated.add(reading);
            CampusDataStore.getReadings().put(sensorId, updated);

            // Update parent sensor currentValue with latest reading
            sensor.setCurrentValue(reading.getValue());
        }

        return createdResponse("Reading added successfully ! ");
    }
}
