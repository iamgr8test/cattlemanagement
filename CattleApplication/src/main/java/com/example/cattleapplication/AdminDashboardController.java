package com.example.cattleapplication;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.event.ActionEvent; 

import java.io.IOException;

public class AdminDashboardController {

    @FXML
    private void handleViewAuditLogs(ActionEvent event) {
        loadScene("AuditLogs.fxml", "Audit Logs", (Node) event.getSource());
    }

    @FXML
    private void handleViewClaims(ActionEvent event) {
        loadScene("SubmittedClaims.fxml", "Submitted Claims", (Node) event.getSource());
    }

    private void loadScene(String fxmlFileName, String title, Node source) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(CattleApplication.class.getResource(fxmlFileName));
            Scene scene = new Scene(fxmlLoader.load());

            
            Stage stage = (Stage) source.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show(); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
