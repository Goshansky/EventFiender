package com.example.eventfiender;

public class ListEntity {
    private String header;
    private String description;
    private String userID;
    private String info;
    private String email;

    private String user_name;


    public ListEntity() {
    }
    public ListEntity(String user_name, String userID) {
        this.user_name = user_name;
        this.userID = userID;
    }


    public ListEntity(String header, String description, String userID, String email) {
        this.header = header;
        this.description = description;
        this.userID = userID;
        this.email = email;
    }
    public String setUser_name(){return user_name;}

    public String getUser_name() {
        return user_name;
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

    public String getEmail() {
        return email;
    }
}
