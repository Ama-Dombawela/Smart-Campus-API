package com.smartcampus.api.resources;

import com.smartcampus.api.CampusDataStore;
import com.smartcampus.api.model.SensorReading;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * 
 */
public class SensorReadingResource {

    private final String sensorId;

    
    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

   
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReadings() {
        List<SensorReading> readingList = CampusDataStore.getReadings().get(sensorId);
        if (readingList == null) {
            readingList = new ArrayList<>();
        }
        return Response.ok(readingList).build();
    }
}
