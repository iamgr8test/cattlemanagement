module com.example.cattleapplication {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql; // <-- Add this line to import the java.sql package

    opens com.example.cattleapplication to javafx.fxml;
    exports com.example.cattleapplication;
}
