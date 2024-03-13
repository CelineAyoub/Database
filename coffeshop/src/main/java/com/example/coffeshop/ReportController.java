package com.example.coffeshop;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;


public class ReportController {
    @FXML
    private TableView<InvoiceItem> reportTableView;

    @FXML
    private Label totalLabel;
    @FXML
    private TableColumn<InvoiceItem, String> productNameColumn;

    @FXML
    private TableColumn<InvoiceItem, Integer> quantityColumn;

    @FXML
    private TableColumn<InvoiceItem, Double> priceColumn;
    @FXML
    private Label dateLabel;
    private ObservableList<InvoiceItem> invoiceItemList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Check if reportTableView is not null
        System.out.println("ReportController initialized");
        // Check if reportTableView is not null
        if (reportTableView != null) {
            // Initialize the TableView with the ObservableList
            reportTableView.setItems(invoiceItemList);

            // Load data during initialization
            loadReportData();
        }
    }

    public void loadReportData() {
        try (Connection connection = DatabaseConnector.connect()) {
            String query = "SELECT * FROM invoice";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                ResultSet resultSet = preparedStatement.executeQuery();

                double total = 0;

                // Clear the existing items in the table before adding new data
                invoiceItemList.clear();

                while (resultSet.next()) {
                    String productName = resultSet.getString("product_name");
                    int quantity = resultSet.getInt("quantity");
                    double price = resultSet.getDouble("price");

                    InvoiceItem invoiceItem = new InvoiceItem(productName, quantity, price);
                    invoiceItemList.add(invoiceItem);

                    total += price * quantity;
                }

                // Set the items in the table after loading the data
                reportTableView.setItems(invoiceItemList);

                // Set cell values using the IDs
                productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
                quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
                priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

                // Optionally, you can set other properties of the columns, e.g., cell factory, etc.

                totalLabel.setText(String.format("%.2f$", total));
                setCurrentDateInLabel();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception appropriately (e.g., show an error message)
        }
    }
    private void setCurrentDateInLabel() {
        // Assuming dateLabel is the name of your label in repport.fxml
        dateLabel.setText("Date: " + getCurrentDate());
    }

    private String getCurrentDate() {
        Timestamp currentDate = new Timestamp(System.currentTimeMillis());
        return currentDate.toString();
    }
}

