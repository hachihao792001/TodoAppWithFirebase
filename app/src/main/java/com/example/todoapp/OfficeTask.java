package com.example.todoapp;

public class OfficeTask extends  TaskModel{
    private String fileName;

    public OfficeTask(){
        fileName="";
    }

    public OfficeTask(String fileName){
        this.fileName=fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName(){
        return fileName;
    }
}
