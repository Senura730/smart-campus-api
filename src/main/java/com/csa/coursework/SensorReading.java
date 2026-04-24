package com.csa.coursework;

public class SensorReading {
    private String id;
    private String sensorId; // Links the reading to a specific sensor
    private double value;
    private String timestamp;

    public SensorReading() {}

    public SensorReading(String id, String sensorId, double value, String timestamp) {
        this.id = id;
        this.sensorId = sensorId;
        this.value = value;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSensorId() { return sensorId; }
    public void setSensorId(String sensorId) { this.sensorId = sensorId; }

    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}