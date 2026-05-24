package com.electricity.backend.dto;

import java.time.LocalDateTime;

public class Notification {
    private String id;
    private String message;
    private String type; // e.g., INFO, SUCCESS, WARNING
    private LocalDateTime timestamp;

    public Notification() {}

    public Notification(String id, String message, String type) {
        this.id = id;
        this.message = message;
        this.type = type;
        this.timestamp = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
