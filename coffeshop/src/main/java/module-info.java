module org.example.coffeshop {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.example.coffeshop to javafx.fxml;
    exports com.example.coffeshop;

    }
    // other module declarations...


