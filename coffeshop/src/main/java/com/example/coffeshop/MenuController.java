package com.example.coffeshop;

import com.example.coffeshop.DatabaseConnector;
import com.example.coffeshop.Product;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



public class MenuController {

    @FXML
    private TableView<Product> tableView;

    @FXML
    private TableColumn<Product, String> productNameColumn;

    @FXML
    private TableColumn<Product, Double> priceColumn;

    @FXML
    private TableView<Product> selectedTableView;

    @FXML
    private TableColumn<Product, String> selectedProductNameColumn;

    @FXML
    private TableColumn<Product, Integer> selectedQuantityColumn;

    @FXML
    private TableColumn<Product, Double> selectedPriceColumn;
    @FXML
    private TextField amountTextField;
    @FXML
    private Label totalLabel;


    private ObservableList<Product> productList = FXCollections.observableArrayList();
    private ObservableList<Product> selectedProductList = FXCollections.observableArrayList();

    // Initialize the controller
    @FXML
    public void initialize() {
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        selectedProductNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        selectedQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        selectedPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        selectedProductList.addListener((ListChangeListener<Product>) change -> updateTotalLabel());
    }

    private void updateTotalLabel() {
        double total = selectedProductList.stream()
                .mapToDouble(product -> product.getPrice() * product.getQuantity())
                .sum();

        totalLabel.setText(String.format("%.2f$", total));
    }

    // Handle the "View" button click
    @FXML
    private void handleViewButton(ActionEvent event) {
        try (Connection connection = DatabaseConnector.connect()) {
            String query = "SELECT * FROM products";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                ResultSet resultSet = preparedStatement.executeQuery();

                ObservableList<Product> productList = FXCollections.observableArrayList();

                while (resultSet.next()) {
                    String productName = resultSet.getString("ProductName");
                    double price = resultSet.getDouble("Price");

                    Product product = new Product(productName, price);
                    productList.add(product);
                }

                // Clear existing data in the table and add new data
                tableView.setItems(productList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception appropriately (e.g., show an error message)
        }
    }

    @FXML
    private void handleProductSelection(MouseEvent event) {
        if (event.getClickCount() == 1) {
            // Single-click detected
            Product selectedProduct = tableView.getSelectionModel().getSelectedItem();

            if (selectedProduct != null) {
                // Get the amount entered by the user
                int amount = Integer.parseInt(amountTextField.getText());

                // Check if the product is already in the selectedProductList
                int index = selectedProductList.indexOf(selectedProduct);

                if (index != -1) {
                    // If the product is already in the list, update its quantity and total price
                    Product existingProduct = selectedProductList.get(index);
                    existingProduct.setQuantity(amount);
                    existingProduct.setPrice(selectedProduct.getPrice() * amount);
                } else {
                    // If the product is not in the list, add it with quantity and calculated total price
                    Product newProduct = new Product(selectedProduct.getProductName(), selectedProduct.getPrice() * amount);
                    newProduct.setQuantity(amount);
                    selectedProductList.add(newProduct);
                }

                // Update the selected table
                selectedTableView.setItems(selectedProductList);
            }
        }
    }

    @FXML
    private void handleRemoveButton(ActionEvent event) {
        // Get the selected product from the selectedTableView
        Product selectedProduct = selectedTableView.getSelectionModel().getSelectedItem();

        if (selectedProduct != null) {
            // Subtract the price of the removed item from the total
            double removedPrice = selectedProduct.getPrice() * selectedProduct.getQuantity();
            double currentTotal = Double.parseDouble(totalLabel.getText().replace("$", ""));
            double newTotal = currentTotal - removedPrice;

            // Update the totalLabel
            totalLabel.setText(String.format("%.2f$", newTotal));

            // Remove the selected product from the selectedProductList
            selectedProductList.remove(selectedProduct);
            // Update the selectedTableView
            selectedTableView.setItems(selectedProductList);
        }
    }

    @FXML
    private void handleLogoutButton(ActionEvent event) {
        // Perform any necessary logout operations

        // You might want to close the current window or switch scenes
        // For example, if you're using a Stage, you can do the following:
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();  // Close the current window

        // Alternatively, you can switch to the login scene if you have one
        // You need to have a reference to your main application or scene manager
        // mainApp.showLoginScene();  // Example method for switching scenes
    }

    @FXML
    private void handleAddProductButton(ActionEvent event) {
        try {
            // Charger le fichier product.fxml
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("product.fxml"));
            Parent root = fxmlLoader.load();

            // Créer une nouvelle scène
            Scene scene = new Scene(root);

            // Obtenir le stage (fenêtre) actuel du bouton cliqué
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Créer un nouveau stage pour la nouvelle scène
            Stage newStage = new Stage();
            newStage.setScene(scene);
            newStage.setTitle("Add Product");
            newStage.show();

            // Fermer la fenêtre actuelle du menu
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ObservableList<Product> getSelectedProductList() {
        return selectedProductList;
    }

    //ultered code:
    @FXML
    private void handlePayButton(ActionEvent event) {
        try (Connection connection = DatabaseConnector.connect()) {
            // Insert selected products into the invoice table

            String insertQuery = "INSERT INTO invoice (product_name, quantity, price) VALUES (?, ?, ?)";
            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                for (Product selectedProduct : selectedProductList) {
                    insertStatement.setString(1, selectedProduct.getProductName());
                    insertStatement.setInt(2, selectedProduct.getQuantity());
                    insertStatement.setDouble(3, selectedProduct.getPrice());
                    insertStatement.executeUpdate();
                }
            }
            if (!checkStockAvailability()) {
                showAlert(Alert.AlertType.ERROR, "Stock Insufficient", "Refill stock for selected products.");
                return; // Do not proceed with the payment
            }

            // Open the report form
            openReportForm();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception appropriately (e.g., show an error message)
        }
    }

    private void openReportForm() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("repport.fxml"));
            Parent root = fxmlLoader.load();

            // Get the controller instance for the report form
            ReportController reportController = fxmlLoader.getController();

            // Load data into the report form
            reportController.loadReportData();

            // Create a new stage for the report form
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setTitle("Report");
            newStage.show();


            // Optionally, close the current menu window
            // Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            //  currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private boolean checkStockAvailability() {
        try (Connection connection = DatabaseConnector.connect()) {
            // Check stock availability for each selected product
            for (Product selectedProduct : selectedProductList) {
                String productName = selectedProduct.getProductName();
                int quantity = selectedProduct.getQuantity();

                String stockCheckQuery = "SELECT Stock FROM products WHERE ProductName = ?";
                try (PreparedStatement stockCheckStatement = connection.prepareStatement(stockCheckQuery)) {
                    stockCheckStatement.setString(1, productName);
                    ResultSet resultSet = stockCheckStatement.executeQuery();

                    if (resultSet.next()) {
                        int currentStock = resultSet.getInt("Stock");
                        if (currentStock < quantity) {
                            // Stock is not available for the product
                            return false;
                        }
                    }
                }
            }

            return true; // All products have sufficient stock
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception appropriately (e.g., show an error message)
            return false;
        }
    }
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}


