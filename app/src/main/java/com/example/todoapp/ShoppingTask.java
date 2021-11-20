package com.example.todoapp;

public class ShoppingTask extends TaskModel{
    private String shoppingUrl, shoppingLocation;

    public ShoppingTask(String shoppingUrl, String shoppingLocation) {
        this.shoppingUrl = shoppingUrl;
        this.shoppingLocation = shoppingLocation;
    }

    public ShoppingTask(String task, String description, String id, String date, TaskType taskType, String shoppingUrl, String shoppingLocation) {
        super(task, description, id, date, taskType);
        this.shoppingUrl = shoppingUrl;
        this.shoppingLocation = shoppingLocation;
    }

    public String getShoppingUrl() {
        return shoppingUrl;
    }

    public void setShoppingUrl(String shoppingUrl) {
        this.shoppingUrl = shoppingUrl;
    }

    public String getShoppingLocation() {
        return shoppingLocation;
    }

    public void setShoppingLocation(String shoppingLocation) {
        this.shoppingLocation = shoppingLocation;
    }
}
