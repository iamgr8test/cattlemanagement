package com.example.cattleapplication;

public class UserSession {
    private static String currentUsername;  // Stores the current user's username
    private static String currentUserId;    // Stores the current user's ID

    /**
     * Sets the current user's username and ID.
     *
     * @param username The username of the current user.
     * @param userId   The ID of the current user.
     */
    public static void setCurrentUser(String username, String userId) {
        currentUsername = username;  // Set the username
        currentUserId = userId;      // Set the user ID
    }

    /**
     * Retrieves the current user's username.
     *
     * @return The username of the current user.
     */
    public static String getCurrentUsername() {
        return currentUsername; // Return the current user's username
    }

    /**
     * Retrieves the current user's ID.
     *
     * @return The ID of the current user.
     */
    public static String getCurrentUserId() {
        return currentUserId; // Return the current user's ID
    }

    /**
     * Clears the current user session, resetting the stored username and ID.
     */
    public static void clearSession() {
        currentUsername = null; // Clear the username
        currentUserId = null;   // Clear the user ID
    }
}
