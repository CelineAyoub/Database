package com.example.coffeshop;

import com.example.coffeshop.DatabaseConnector;
import com.example.coffeshop.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

import static javafx.application.Application.launch;


public class ProductControler {




    @FXML
    private TableView<Product> tableView;

    @FXML
    private TableColumn<Product, Integer> productIdColumn;

    @FXML
    private TableColumn<Product, String> productNameColumn;

    @FXML
    private TableColumn<Product, String> typeColumn;

    @FXML
    private TableColumn<Product, Integer> stockColumn;

    @FXML
    private TableColumn<Product, Double> priceColumn;

    @FXML
    private TableColumn<Product, Date> dateAddedColumn;
    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private TextField productIdField;

    @FXML
    private TextField productNameField;

    @FXML
    private TextField stockField;

    @FXML
    private TextField priceField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private VBox cardProductsContainer; // assuming you have a VBox in your main layout

    private ObservableList<Product> productList;


    @FXML
    private void initialize() throws IOException {
        // Initialize columns
            productIdColumn.setCellValueFactory(new PropertyValueFactory<>("productID"));
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        dateAddedColumn.setCellValueFactory(new PropertyValueFactory<>("dateAdded"));
        ObservableList<String> types = FXCollections.observableArrayList("meal", "drink", "dessert");
        typeComboBox.setItems(types);


        // Set MenuPageController as an
    }



    @FXML
    private void handleViewButton() {
        // Fetch data from the database and populate the TableView
        try (Connection connection = DatabaseConnector.connect()) {
            String query = "SELECT * FROM products";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                ResultSet resultSet = preparedStatement.executeQuery();

                ObservableList<Product> productList = FXCollections.observableArrayList();

                while (resultSet.next()) {
                    int productID = resultSet.getInt("ProductID");
                    String productName = resultSet.getString("ProductName");
                    String type = resultSet.getString("Type");
                    int stock = resultSet.getInt("Stock");
                    double price = resultSet.getDouble("Price");
                    Date dateAdded = resultSet.getTimestamp("DateAdded");

                    Product product = new Product(productID, productName, type, stock, price, dateAdded);
                    productList.add(product);


                }

                tableView.setItems(productList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database error occurred.");
        }
    }





    private void clearInputFields() {
        // Clear input fields
        productIdField.clear();
        productNameField.clear();
        typeComboBox.getSelectionModel().clearSelection();
        stockField.clear();
        priceField.clear();
    }


    @FXML
    private void handleAddButton() {
        String productName = productNameField.getText();
        String type = typeComboBox.getValue();
        int stock = Integer.parseInt(stockField.getText());
        double price = Double.parseDouble(priceField.getText());
        int id = Integer.parseInt(productIdField.getText().trim());

        // Additional checks to ensure values are not null or empty
        if (productName == null || productName.isEmpty() || type == null || type.isEmpty()) {
            showAlert("Error", "Product name and type cannot be empty.");
            return;
        }

        try (Connection connection = DatabaseConnector.connect()) {
            String query = "INSERT INTO products (ProductID, ProductName, Type, Stock, Price, DateAdded) VALUES (?, ?, ?, ?, ?, NOW())";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, id);
                preparedStatement.setString(2, productName);
                preparedStatement.setString(3, type); // Set the type
                preparedStatement.setInt(4, stock);
                preparedStatement.setDouble(5, price);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    showAlert("Success", "Product added successfully.");
                    // Refresh the TableView after adding (you can call your refresh method here)
                } else {
                    showAlert("Error", "Failed to add product.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database error occurred.");
        }
    }






    // ... Rest of the code ...


    @FXML
    private void handleDeleteButton() {
        // Get the selected product from the TableView
        Product selectedProduct = tableView.getSelectionModel().getSelectedItem();

        if (selectedProduct != null) {
            // Confirm deletion (you may add a confirmation dialog)
            boolean confirmed = showConfirmationAlert("Confirmation", "Are you sure you want to delete this product?");

            if (confirmed) {
                // Delete data from the database
                try (Connection connection = DatabaseConnector.connect()) {
                    String query = "DELETE FROM products WHERE ProductID = ?";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                        preparedStatement.setInt(1, selectedProduct.getProductID());

                        int rowsAffected = preparedStatement.executeUpdate();

                        if (rowsAffected > 0) {
                            showAlert("Success", "Product deleted successfully.");
                            handleViewButton(); // Refresh the TableView after deletion
                        } else {
                            showAlert("Error", "Failed to delete product.");
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Error", "Database error occurred.");
                }
            }
        } else {
            showAlert("Error", "Please select a product to delete.");
        }
    }

    @FXML
    private void handleUpdateButton() {
        // Get the selected product from the TableView
        Product selectedProduct = tableView.getSelectionModel().getSelectedItem();

        if (selectedProduct != null) {
            // Show a dialog to get the updated price and stock
            TextInputDialog priceDialog = new TextInputDialog(String.valueOf(selectedProduct.getPrice()));
            priceDialog.setTitle("Update Price");
            priceDialog.setHeaderText("Enter the new price:");
            Optional<String> priceResult = priceDialog.showAndWait();

            if (priceResult.isPresent()) {
                // Show a dialog to get the updated stock
                TextInputDialog stockDialog = new TextInputDialog(String.valueOf(selectedProduct.getStock()));
                stockDialog.setTitle("Update Stock");
                stockDialog.setHeaderText("Enter the new stock:");
                Optional<String> stockResult = stockDialog.showAndWait();

                if (stockResult.isPresent()) {
                    // Parse the new price and stock from the user input
                    try {
                        double newPrice = Double.parseDouble(priceResult.get());
                        int newStock = Integer.parseInt(stockResult.get());

                        // Update data in the database
                        try (Connection connection = DatabaseConnector.connect()) {
                            String query = "UPDATE products SET Price = ?, Stock = ? WHERE ProductID = ?";
                            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                                preparedStatement.setDouble(1, newPrice);
                                preparedStatement.setInt(2, newStock);
                                preparedStatement.setInt(3, selectedProduct.getProductID());

                                int rowsAffected = preparedStatement.executeUpdate();

                                if (rowsAffected > 0) {
                                    showAlert("Success", "Price and stock updated successfully.");
                                    handleViewButton();

                                } else {
                                    showAlert("Error", "Failed to update price and stock.");
                                }
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                            showAlert("Error", "Database error occurred.");
                        }
                    } catch (NumberFormatException e) {
                        showAlert("Error", "Invalid price or stock format. Please enter valid numbers.");
                    }
                }
            }
        } else {
            showAlert("Error", "Please select a product to update.");
        }
    }



    private boolean showConfirmationAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(okButton, cancelButton);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == okButton;
    }

    @FXML
    private void handleBackButton(ActionEvent event) {

        try {
            Stage thisStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            thisStage.close();

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("menu.fxml"));
            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.TRANSPARENT);
            scene.setFill(Color.TRANSPARENT);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception appropriately (e.g., show an error message)
        }

    }
    @FXML
    private ImageView selectedImageView;

    @FXML
    private void handleImportButton() {
        // Create a FileChooser for selecting a file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an image file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image files", "*.png", "*.jpg", "*.jpeg", "*.gif"));

        // Show the file chooser dialog
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            // Process the selected file
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());

            // Extract the directory from the selected file
            File selectedDirectory = selectedFile.getParentFile();
            System.out.println("Selected directory: " + selectedDirectory.getAbsolutePath());

            // Load the selected image into the ImageView
            Image selectedImage = new Image(selectedFile.toURI().toString());
            selectedImageView.setImage(selectedImage);
        } else {
            // User canceled the operation
            System.out.println("Import canceled by the user.");
        }

    }





    // Add a method to get the selected image path


    private void showAlert(String title, String content) {
        // Implement your alert logic here
    }

}






