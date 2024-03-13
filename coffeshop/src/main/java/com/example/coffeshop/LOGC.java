package com.example.coffeshop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LOGC  {

    @FXML
    private TextField textField;

    @FXML
    private PasswordField passwordField;

    @FXML
    public Button button;
    @FXML
    public Button AddBtn;
    @FXML
    private TableView<ProductApp> tableView;

    @FXML
    private Button viewButton;

    // Reference to the main application.
    private Login login;

    // Setter for the main application.
    public void setLogin(Login login) {
        this.login = login;
    }

    @FXML
    private void handleLoginButtonAction(ActionEvent event) {
        String username = textField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Username and password cannot be empty.");
            return;
        }

        try (Connection connection = DatabaseConnector.connect()) {
            String query = "SELECT * FROM user WHERE name = ? AND password = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    try {

                        Stage thisStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                        thisStage.close();
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("menu.fxml"));
                        Scene scene = new Scene(fxmlLoader.load());
                        Stage stage = new Stage();
                        stage.setScene(scene);
                        stage.initStyle(StageStyle.TRANSPARENT);
                        scene.setFill(Color.TRANSPARENT);
                        stage.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    showAlert("Error", "Invalid username or password.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database error occurred.");
        }
    }


    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}



