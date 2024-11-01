package com.example.cattleapplication;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ViewCattleController {

    @FXML
    private TableView<Cattle> cattleTable;
    @FXML
    private TableColumn<Cattle, String> cattleIdColumn;
    @FXML
    private TableColumn<Cattle, String> cattleTypeColumn;
    @FXML
    private TableColumn<Cattle, String> breedColumn;
    @FXML
    private TableColumn<Cattle, Integer> ageColumn;
    @FXML
    private TableColumn<Cattle, Double> weightColumn;
    @FXML
    private TableColumn<Cattle, String> healthStatusColumn;

    private ObservableList<Cattle> cattleList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Set up the columns to display data
        cattleIdColumn.setCellValueFactory(new PropertyValueFactory<>("cattleId"));
        cattleTypeColumn.setCellValueFactory(new PropertyValueFactory<>("cattleType"));
        breedColumn.setCellValueFactory(new PropertyValueFactory<>("breed"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));
        healthStatusColumn.setCellValueFactory(new PropertyValueFactory<>("healthStatus"));

        loadCattleData(); // Load data when the view is initialized
    }

    private void loadCattleData() {
        // SQL query to fetch cattle data for the logged-in user
        String query = "SELECT * FROM cattle WHERE user_id = ?";

        // Fetch the logged-in user's ID from UserSession
        String userId = UserSession.getCurrentUserId(); // Assuming this method returns the user ID

        try (Connection connection = DatabaseUtil.connect(); // Use DatabaseUtil for connection
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, userId); // Set the user ID
            ResultSet resultSet = preparedStatement.executeQuery();

            // Clear the list before adding new data
            cattleList.clear();

            // Check if any records were returned
            if (!resultSet.isBeforeFirst()) {
                showAlert("No Records Found", "No cattle records found for the logged-in user.");
                return; // Exit if no records are found
            }

            while (resultSet.next()) {
                // Create a new Cattle object for each row
                Cattle cattle = new Cattle(
                        resultSet.getString("cattle_id"),
                        resultSet.getString("cattle_type"),
                        resultSet.getString("breed"),
                        resultSet.getInt("age"),
                        resultSet.getDouble("weight"),
                        resultSet.getString("health_status"),
                        resultSet.getString("owner_name") // Include owner name if needed
                );
                cattleList.add(cattle); // Add cattle to the list
            }

            // Set the items in the table
            cattleTable.setItems(cattleList);

        } catch (Exception e) {
            showAlert("Database Error", "Failed to load cattle data: " + e.getMessage());
            e.printStackTrace(); // Log the error for debugging
        }
    }

    @FXML
    private void handleBackToDashboard(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(CattleApplication.class.getResource("Dashboard.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);

            // Use the same stage (window) that the event came from
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Dashboard");
        } catch (IOException e) {
            showAlert("Loading Error", "Failed to load the dashboard: " + e.getMessage());
            e.printStackTrace(); // Log the error for debugging
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION); // Use INFORMATION type for alerts
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
