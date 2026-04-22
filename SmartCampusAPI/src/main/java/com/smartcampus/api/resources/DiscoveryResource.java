/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.api.resources;

import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *Provides API metadata at the root endpoint GET /api/v1/
 * @author User
 */

@Path("/")
public class DiscoveryResource {
    
    /**
     * GET /api/v1/
     * Returns API version,contact info and links to all resource collections
     * @return JSON response with API metadata 
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response discover(){
        
        //Main response map to hold all API metadata
        Map<String, Object> info = new HashMap<>();
        
        //API version info
        info.put("version", "1.0");
        info.put("description", "Smart Campus Sensor & Room Management API");
        info.put("contact", "admin@smartcampus.ac.uk");
        
        //Resource links(HATEOAS)
        //Allows clients to navigate the API without hardcoding URLs
        Map<String, String> links = new HashMap<>();
        links.put("rooms","/api/v1/rooms");
        links.put("sensors","/api/v1/sensors");
        info.put("resources", links);
        
        //return the response status 
        return Response.ok(info).build();
    }
    
}
