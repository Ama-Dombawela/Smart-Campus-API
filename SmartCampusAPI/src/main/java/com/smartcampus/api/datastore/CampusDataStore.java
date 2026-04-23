/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.api.datastore;

import com.smartcampus.api.model.Room;
import com.smartcampus.api.model.Sensor;
import com.smartcampus.api.model.SensorReading;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ama Dombawela 
 * UOW No: W2120682 
 * IIT Student No: 20231642
 *
 * CampusDataStore - Central In-Memory Data Storage Stores all data for the
 * Smart Campus API using ConcurrentHashMaps Static fields ensure data persists
 * across multiple JAX-RS request instances ConcurrentHashMap provides
 * thread-safety for concurrent requests.
 */
public class CampusDataStore {

    //Rooms Storage: Room id -> Room
    private static final ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>();

    //Sensors Storage: Sensor id -> Sensor
    private static final ConcurrentHashMap<String, Sensor> sensors = new ConcurrentHashMap<>();

    //Readings Storage: sensorId -> list of readings
    private static final ConcurrentHashMap<String, List<SensorReading>> readings = new ConcurrentHashMap<>();

    /**
     * @return the rooms
     */
    public static ConcurrentHashMap<String, Room> getRooms() {
        return rooms;
    }

    /**
     * @return the sensors
     */
    public static ConcurrentHashMap<String, Sensor> getSensors() {
        return sensors;
    }

    /**
     * @return the readings
     */
    public static ConcurrentHashMap<String, List<SensorReading>> getReadings() {
        return readings;
    }

}
