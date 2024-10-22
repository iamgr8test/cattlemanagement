package com.example.cattleapplication;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class PolicyValidator {

    // HMAC algorithm updated to SHA-512 for stronger hashing
    private static final String HMAC_ALGORITHM = "HmacSHA512";
    private static final String SECRET_KEY = "your-secret-key";  // Use your secure key

    // Generate HMAC checksum for the given data using SHA-512
    public static String generateHmac(String data) throws Exception {
        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), HMAC_ALGORITHM);
        mac.init(secretKeySpec);
        byte[] hmacBytes = mac.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(hmacBytes);  // Return Base64-encoded HMAC string
    }

    // Validate whether the policy exists in the database for the given policy ID
    public static boolean validatePolicy(String policyId) {
        boolean isValid = false;
        String query = "SELECT COUNT(*) FROM policies WHERE policy_id = ?";

        try (Connection connection = DatabaseUtil.connect();  // Use DatabaseUtil to connect
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, policyId);  // Set the policy ID
            ResultSet resultSet = preparedStatement.executeQuery();

            // If at least one result is returned, the policy is valid
            if (resultSet.next() && resultSet.getInt(1) > 0) {
                isValid = true;
            }

        } catch (SQLException e) {
            System.out.println("Error during policy validation: " + e.getMessage());
        }

        return isValid;
    }

    // Validate checksum to ensure that cattle data hasn't been tampered with
    public static boolean validateChecksum(String cattleId, String checksum) {
        String query = "SELECT cattle_id, cattle_type, breed, age, weight, health_status FROM cattle WHERE cattle_id = ?";

        try (Connection connection = DatabaseUtil.connect();  // Use DatabaseUtil to connect
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, cattleId);  // Set the cattle ID
            ResultSet resultSet = preparedStatement.executeQuery();

            // If the cattle record is found, we proceed with checksum validation
            if (resultSet.next()) {
                // Concatenate data fields to create a string representing the cattle data
                String data = resultSet.getString("cattle_id")
                        + resultSet.getString("cattle_type")
                        + resultSet.getString("breed")
                        + resultSet.getInt("age")
                        + resultSet.getDouble("weight")
                        + resultSet.getString("health_status");

                // Generate HMAC checksum using the concatenated data
                String generatedChecksum = generateHmac(data);

                // Return true if the generated checksum matches the provided checksum
                return generatedChecksum.equals(checksum);
            }

        } catch (SQLException e) {
            System.out.println("Error during checksum validation: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error generating HMAC: " + e.getMessage());
        }

        return false;
    }

    // New method to validate both policy existence and checksum integrity
    public static boolean validateInsurancePolicy(String policyId, String cattleId, String checksum) {
        boolean isPolicyValid = validatePolicy(policyId);  // Validate if policy exists
        boolean isChecksumValid = validateChecksum(cattleId, checksum);  // Validate cattle data integrity

        return isPolicyValid && isChecksumValid;  // Return true only if both are valid
    }
}
