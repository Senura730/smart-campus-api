package com.csa.coursework;

public class SensorRoom {
    private String id;
    private String name;
    private String location;
    private int sensorCount; // This field is required for the Deletion Safety Logic

    // 1. Default constructor (REQUIRED for JSON conversion)
    public SensorRoom() {}

    // 2. The 4-argument constructor (THIS WAS MISSING OR MISMATCHED)
    public SensorRoom(String id, String name, String location, int sensorCount) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.sensorCount = sensorCount;
    }

    // Getters and Setters (REQUIRED for Jersey to read/write JSON)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getSensorCount() { return sensorCount; }
    public void setSensorCount(int sensorCount) { this.sensorCount = sensorCount; }
}