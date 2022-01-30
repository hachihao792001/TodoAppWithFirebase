package com.example.todoapp;

public class ShoppingTask extends TaskModel{
    private String productUrl, shoppingLocation;

    public ShoppingTask() {

    }

    public ShoppingTask(String shoppingUrl, String shoppingLocation) {
        this.productUrl = shoppingUrl;
        this.shoppingLocation = shoppingLocation;
    }

    public ShoppingTask(String task, String description, String id, String date, TaskType taskType, String productUrl, String shoppingLocation) {
        super(task, description, id, date, taskType);
        this.productUrl = productUrl;
        this.shoppingLocation = shoppingLocation;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public String getShoppingLocation() {
        return shoppingLocation;
    }

    public void setShoppingLocation(String shoppingLocation) {
        this.shoppingLocation = shoppingLocation;
    }
}
