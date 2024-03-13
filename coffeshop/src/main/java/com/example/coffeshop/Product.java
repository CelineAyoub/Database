package com.example.coffeshop;


import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.Date;

import static javafx.application.Application.launch;

public class Product {
    private int productID;
    private String productName;
    private String type;
    private int stock;
    private double price;
    private Date dateAdded;
    private final IntegerProperty quantity = new SimpleIntegerProperty(0);
    public Product(String productName,double price){
        this.productName=productName;
        this.price=price;
    }


    // Constructor
    public Product(int productID, String productName, String type, int stock, double price, Date dateAdded) {
        this.productID = productID;
        this.productName = productName;
        this.type = type; // Set the type
        this.stock = stock;
        this.price = price;
        this.dateAdded = dateAdded;
    }

    // Getters and setters (You can generate these using your IDE)
    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }


    public int getQuantity() {
        return quantity.get();
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }
    // toString method (optional, for debugging or displaying information)
    @Override
    public String toString() {
        return "Product{" +
                "productID=" + productID +
                ", productName='" + productName + '\'' +
                ", type='" + type + '\'' +
                ", stock=" + stock +
                ", price=" + price +
                ", dateAdded=" + dateAdded +
                '}';

    }

}
