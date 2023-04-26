package com.example.eventfiender;

public class ListEntity {
    private String header;
    private String description;
    private String userID;
    private String info;


    public ListEntity() {
    }


    public ListEntity(String header, String description) {
        this.header = header;
        this.description = description;
        this.userID = userID;
        this.info = info;
    }

    public String getHeader() {
        return header;
    }

    public String getDescription() {
        return description;
    }

    public String getUserID() {
        return userID;
    }

    public String getInfo() {
        return info;
    }
}
