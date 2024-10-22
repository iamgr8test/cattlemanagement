package com.example.cattleapplication;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CattleValidator {

    public static boolean validateCattleOwnership(String cattleId, String farmerId) {
        String query = "SELECT COUNT(*) FROM cattle WHERE cattle_id = ? AND owner_id = ?";
        try (Connection connection = DatabaseUtil.connect();  // Updated to use DatabaseUtil
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, cattleId);
            statement.setString(2, farmerId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
