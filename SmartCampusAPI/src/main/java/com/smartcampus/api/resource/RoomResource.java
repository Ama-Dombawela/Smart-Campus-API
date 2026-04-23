/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.api.resource;

import com.smartcampus.api.datastore.CampusDataStore;
import com.smartcampus.api.exception.RoomNotEmptyException;
import com.smartcampus.api.model.Room;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Room Resource - Part 2 Manages all room operations for the Smart Campus API
 * Base path: /api/v1/rooms
 */
@Path("/rooms")
public class RoomResource extends BaseResource {

    /**
     * GET /api/v1/rooms Returns a list of all the rooms in the system
     *
     * @return full room objects including id,name,capacity and sensorIds
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRooms() {
        List<Room> roomList = new ArrayList<>(CampusDataStore.getRooms().values());
        return Response.ok(roomList).build();
    }

    /**
     * POST /api/v1/rooms
     *
     * @param room - the room object to be created
     * @return 409 conflict if room already exists, 201 Created on success
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createRoom(Room room) {
        if (room == null) {
            return badRequestResponse("Room payload is required.");
        }

        if (room.getId() == null || room.getId().trim().isEmpty()) {
            return badRequestResponse("Room ID is required.");
        }

        //Check if room with same ID already exists
        if (CampusDataStore.getRooms().containsKey(room.getId())) {
            return conflictResponse("Room " + room.getId() + " already exists");

        }
        //Store the new room in CampusData using its ID as the key
        CampusDataStore.getRooms().put(room.getId(), room);
        return createdResponse("Room " + room.getId() + " created successfully ! ");

    }

    /**
     * GET /api/v1/rooms/{roomId}
     *
     * @param roomId - the ID of the room to retrieve
     * @return 404 Not Found if room does not exist, 200 OK on success
     */
    @GET
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoomById(@PathParam("roomId") String roomId) {
        //Look up room by ID in CampusData
        Room room = findRoom(roomId);
        if (room == null) {
            return notFoundResponse("Room " + roomId + " not found.");
        }
        return Response.ok(room).build();
    }

    /**
     * DELETE /api/v1/rooms/{roomId} delete a room from the system
     *
     * @param roomId - ID of the room to delete
     * @return 404 if not found, 409 if sensors assigned, 200 on success
     */
    @DELETE
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        //Look up room by ID in CampusData
        Room room = findRoom(roomId);

        //Checking if the room exists
        if (room == null) {
            return notFoundResponse("Room " + roomId + " not found.");
        }

        //Checking if any sensors are assigned to that room
        if (room.getSensorIds() != null && !room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException("Room " + roomId + " cannot be deleted as it has sensors assigned.");

        }

        //Delete the room
        CampusDataStore.getRooms().remove(roomId);
        return successResponse("Room " + roomId + " deleted successfully !");
    }
}
