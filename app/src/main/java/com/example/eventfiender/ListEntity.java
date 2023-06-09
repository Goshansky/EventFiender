package com.example.eventfiender;

public class ListEntity {
    private String event_name; // Название события
    private String event_date; // Дата проведения
    private String event_age; // Возрастные ограничения
    private String event_info; // Информация о событие
    private String eventID; // id события
    private String videoLink; // Видео-анонс
    private String stadt; // Город проведения


    private String user_name; // Имя пользователя
    private String userID; // id пользователя
    private String email; // Почта пользователя
    private String user_image; // Фотка пользователя
    private String user_age; // Возраст пользователя
    private String user_info; // Информация о пользователе


    public ListEntity() {
    }

    /**
     * Конструктор для пользовательских данных
     * @param user_name имя пользователя
     * @param user_age возраст пользователя
     * @param user_info информация о пользователе
     * @param userID id пользователя
     * @param email почта пользователя
     * @param user_image фотография пользователя
     */
    public ListEntity(String user_name, String user_age, String user_info, String userID, String email, String user_image) {
        this.user_name = user_name;
        this.user_age = user_age;
        this.user_info = user_info;
        this.userID = userID;
        this.email = email;
        this.user_image = user_image;
    }


    /**
     * Конструктор для данных о событии
     * @param eventID id события
     * @param event_name название события
     * @param event_date дата проведения события
     * @param event_age возрастные ограничения
     * @param event_info информация о событии
     * @param userID id автора события
     * @param email почта автора события
     * @param videoLink видео-анонс предстоящего события
     * @param stadt город проведения
     */
    public ListEntity(String eventID, String event_name, String event_date, String event_age, String event_info, String userID, String email, String videoLink, String stadt) {
        this.eventID = eventID;
        this.event_name = event_name;
        this.event_date = event_date;
        this.event_age = event_age;
        this.event_info = event_info;
        this.userID = userID;
        this.email = email;
        this.videoLink = videoLink;
        this.stadt = stadt;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setUser_age(String user_age) {
        this.user_age = user_age;
    }

    public void setUser_info(String user_info) {
        this.user_info = user_info;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    // Геттеры

    public String getVideoLink() {
        return videoLink;
    }

    public String getEvent_name() {
        return event_name;
    }

    public String getEvent_date() {
        return event_date;
    }

    public String getEvent_age() {
        return event_age;
    }

    public String getEvent_info() {
        return event_info;
    }

    public String getEventID() {
        return eventID;
    }


    public String getStadt() {
        return stadt;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getUserID() {
        return userID;
    }

    public String getEmail() {
        return email;
    }

    public String getUser_image() {
        return user_image;
    }

    public String getUser_age() {
        return user_age;
    }

    public String getUser_info() {
        return user_info;
    }
}
