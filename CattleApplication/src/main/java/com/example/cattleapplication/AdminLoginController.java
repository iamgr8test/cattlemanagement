package com.example.cattleapplication;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminLoginController {

    @FXML
    private TextField adminUsernameField;

    @FXML
    private PasswordField adminPasswordField;

    @FXML
    private Label adminErrorLabel;  

    // Admin login method
    @FXML
    public void loginAdmin() {
        String username = adminUsernameField.getText().trim();
        String password = adminPasswordField.getText();

        
        if (username.isEmpty() || password.isEmpty()) {
            adminErrorLabel.setText("Username and password cannot be empty.");
            return;
        }

        if (authenticateAdmin(username, password)) {
            System.out.println("Admin login successful!");
            loadAdminDashboard();  
        } else {
            adminErrorLabel.setText("Invalid admin credentials.");
        }
    }

    private boolean authenticateAdmin(String username, String password) {
        String query = "SELECT username FROM admins WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseUtil.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password); 

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String adminId = rs.getString("username");
                    UserSession.setCurrentUser(username, adminId);  
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return false;
    }

    private void loadAdminDashboard() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(CattleApplication.class.getResource("AdminDashboard.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            Stage stage = (Stage) adminUsernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Admin Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
