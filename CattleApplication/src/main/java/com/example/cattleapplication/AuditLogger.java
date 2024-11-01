package com.example.cattleapplication;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AuditLogger {

    public static void logAudit(String action, String userId) {
        try (Connection conn = DatabaseUtil.connect()) {
            String query = "INSERT INTO audit_logs (action, user_id) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, action);
            pstmt.setString(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // Log or handle any SQL errors as needed
        }
    }
}
