package com.example.cattleapplication;

import java.nio.file.Files;
import java.nio.file.Paths;

public class DocumentHandler {

    public static boolean saveDocument(String filePath, byte[] fileData) {
        try {
            Files.write(Paths.get(filePath), fileData);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean validateDocumentUpload(String filePath) {
        return Files.exists(Paths.get(filePath));
    }
}
