package com.example.cattleapplication;

public class AuditLog {
    private int id;
    private String action;
    private String userId;
    private String timestamp;

    public AuditLog(int id, String action, String userId, String timestamp) {
        this.id = id;
        this.action = action;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getAction() {
        return action;
    }

    public String getUserId() {
        return userId;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
