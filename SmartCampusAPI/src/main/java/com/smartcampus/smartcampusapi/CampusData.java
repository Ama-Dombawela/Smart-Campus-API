/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.smartcampusapi;

import com.smartcampus.smartcampusapi.model.Room;
import com.smartcampus.smartcampusapi.model.Sensor;
import com.smartcampus.smartcampusapi.model.SensorReading;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author User
 */
public class CampusData {

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
