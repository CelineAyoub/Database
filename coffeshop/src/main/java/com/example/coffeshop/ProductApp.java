package com.example.coffeshop;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Optional;


public class ProductApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the MenuPageController


            // Load the ProductControler and set the MenuPageController
            FXMLLoader productLoader = new FXMLLoader(getClass().getResource("product.fxml"));
            Parent productRoot = productLoader.load();
            ProductControler productController = productLoader.getController();


            // Set up the scene and show the stage
            primaryStage.setTitle("Coffee Shop Product App");
            Scene scene = new Scene(productRoot, 800, 600);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
