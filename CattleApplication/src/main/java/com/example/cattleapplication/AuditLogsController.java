package com.example.cattleapplication;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class AuditLogsController {

    @FXML
    private TableView<AuditLog> auditLogsTable;
    @FXML
    private TableColumn<AuditLog, Integer> idColumn;
    @FXML
    private TableColumn<AuditLog, String> actionColumn;
    @FXML
    private TableColumn<AuditLog, String> userIdColumn;
    @FXML
    private TableColumn<AuditLog, String> timestampColumn;
    @FXML
    private Button backButton; // Assuming there's a back button in your FXML

    public void initialize() {
        loadAuditLogs();
    }

    private void loadAuditLogs() {
        ObservableList<AuditLog> logs = FXCollections.observableArrayList();
        String query = "SELECT * FROM audit_logs";

        try (Connection connection = DatabaseUtil.connect(); // Updated to use connect()
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                AuditLog log = new AuditLog(
                        resultSet.getInt("id"),
                        resultSet.getString("action"),
                        resultSet.getString("user_id"),
                        resultSet.getTimestamp("timestamp").toString()
                );
                logs.add(log);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        actionColumn.setCellValueFactory(new PropertyValueFactory<>("action"));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        timestampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));

        auditLogsTable.setItems(logs);
    }

    @FXML
    public void goBackToDashboard() {
        try {
            // Load the dashboard FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminDashboard.fxml")); // Change the FXML file name as needed
            Parent dashboardRoot = loader.load();

            // Get the current stage (window)
            Stage currentStage = (Stage) auditLogsTable.getScene().getWindow(); // Assuming the TableView is in the current scene

            // Create a new scene with the loaded dashboard
            Scene dashboardScene = new Scene(dashboardRoot,600,400);

            // Set the new scene on the current stage
            currentStage.setScene(dashboardScene);

            // Optionally, you can set the stage title
            currentStage.setTitle("Admin Dashboard");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
