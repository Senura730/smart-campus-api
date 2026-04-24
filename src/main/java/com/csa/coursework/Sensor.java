package com.csa.coursework;

public class Sensor {
    private String id;
    private String name;
    private String roomId;
    private String type;
    private String status; // ACTIVE or MAINTENANCE

    public Sensor() {}

    public Sensor(String id, String name, String roomId, String type, String status) {
        this.id = id;
        this.name = name;
        this.roomId = roomId;
        this.type = type;
        this.status = status;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}