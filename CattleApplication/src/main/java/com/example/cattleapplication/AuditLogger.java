package com.example.cattleapplication;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class AuditLogger {

    public static void logAction(String actionType, String entityId, String details, String userId) {
        String insertQuery = "INSERT INTO audit_logs (action_type, entity_id, details, user_id, timestamp) VALUES (?, ?, ?, ?, NOW())";
        try (Connection connection = DatabaseUtil.connect();  // Updated to use DatabaseUtil
             PreparedStatement statement = connection.prepareStatement(insertQuery)) {

            statement.setString(1, actionType);
            statement.setString(2, entityId);
            statement.setString(3, details);
            statement.setString(4, userId);

            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
