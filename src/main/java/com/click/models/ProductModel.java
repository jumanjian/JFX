package com.click.models;


public class ProductModel {

    private Integer ProductID;
    private String ProductName;
    private Integer Inventory;
    private Integer OldPrice;
    private Integer NewPrice;

    public ProductModel(Integer productID, String productName, Integer oldPrice, Integer newPrice,Integer inventory ) {
        ProductID = productID;
        ProductName = productName;
        OldPrice = oldPrice;
        NewPrice = newPrice;
        Inventory = inventory;
    }

    public Integer getProductID() {
        return ProductID;
    }

    public String getProductName() {
        return ProductName;
    }

    public Integer getOldPrice() {
        return OldPrice;
    }

    public Integer getNewPrice() {
        return NewPrice;
    }

    public Integer getInventory() {
        return Inventory;
    }

}
