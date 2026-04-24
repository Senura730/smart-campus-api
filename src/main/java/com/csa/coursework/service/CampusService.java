package com.csa.coursework.service;

import com.csa.coursework.SensorRoom;
import com.csa.coursework.Sensor;
import com.csa.coursework.SensorReading;
import com.csa.coursework.exceptions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Service Layer: Handles all data storage and business validation.
 * Uses ConcurrentHashMap for Thread Safety.
 */
public class CampusService {

    // Thread-safe "Database"
    private static final Map<String, SensorRoom> ROOMS = new ConcurrentHashMap<>();
    private static final Map<String, Sensor> SENSORS = new ConcurrentHashMap<>();
    private static final Map<String, List<SensorReading>> READINGS = new ConcurrentHashMap<>();

    // Initialize mock data
    static {
        ROOMS.put("101", new SensorRoom("101", "Main Lab", "Level 4", 2));
        ROOMS.put("202", new SensorRoom("202", "Innovation Hub", "Level 5", 3));

        SENSORS.put("S-001", new Sensor("S-001", "Lab Temp", "101", "Temperature", "ACTIVE"));
        SENSORS.put("S-002", new Sensor("S-002", "Air Quality", "101", "CO2", "MAINTENANCE"));
        SENSORS.put("S-003", new Sensor("S-003", "Hub Air", "202", "CO2", "ACTIVE"));
    }

    // --- ROOM LOGIC ---
    public List<SensorRoom> getAllRooms() {
        return new ArrayList<>(ROOMS.values());
    }

    public SensorRoom getRoom(String roomId) {
        if (!ROOMS.containsKey(roomId)) {
            throw new ResourceNotFoundException("Room with ID " + roomId + " was not found.");
        }
        return ROOMS.get(roomId);
    }

    public SensorRoom addRoom(SensorRoom room) {
        ROOMS.put(room.getId(), room);
        return room;
    }

    public void deleteRoom(String roomId) {
        SensorRoom room = getRoom(roomId); // Throws 404 if missing
        
        if (room.getSensorCount() > 0) {
            // Your custom 409 exception!
            throw new RoomNotEmptyException("Cannot delete room " + roomId + " because it has " + room.getSensorCount() + " sensors.");
        }
        ROOMS.remove(roomId);
    }

    // --- SENSOR LOGIC ---
    public List<Sensor> getAllSensors(String type) {
        List<Sensor> result = new ArrayList<>();
        for (Sensor s : SENSORS.values()) {
            if (type == null || type.isEmpty() || s.getType().equalsIgnoreCase(type)) {
                result.add(s);
            }
        }
        return result;
    }

    public Sensor addSensor(Sensor sensor) {
        if (!ROOMS.containsKey(sensor.getRoomId())) {
            // Your custom 422 exception!
            throw new LinkedResourceNotFoundException("Validation Failed: Room ID '" + sensor.getRoomId() + "' does not exist.");
        }
        
        SENSORS.put(sensor.getId(), sensor);
        
        // Safely update room count
        SensorRoom room = ROOMS.get(sensor.getRoomId());
        room.setSensorCount(room.getSensorCount() + 1);
        
        return sensor;
    }

    public Sensor getSensor(String sensorId) {
        if (!SENSORS.containsKey(sensorId)) {
            throw new ResourceNotFoundException("Sensor " + sensorId + " not found.");
        }
        return SENSORS.get(sensorId);
    }

    // --- READING LOGIC ---
    public List<SensorReading> getReadings(String sensorId) {
        getSensor(sensorId); // Validates sensor exists
        return READINGS.getOrDefault(sensorId, new ArrayList<>());
    }

    public SensorReading addReading(String sensorId, SensorReading reading) {
        Sensor sensor = getSensor(sensorId); // Throws 404 if missing

        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            // Your custom 403 exception!
            throw new SensorUnavailableException("Sensor " + sensorId + " is in MAINTENANCE mode.");
        }

        reading.setSensorId(sensorId);
        READINGS.computeIfAbsent(sensorId, k -> new ArrayList<>()).add(reading);
        return reading;
    }
}