package ru.volkov.cw.model;

import java.time.LocalDateTime;

public class ProductCollection {

    private int id;
    private String name;
    private String userName;
    private LocalDateTime createdAt;

    public ProductCollection() {}

    public ProductCollection(String name, String userName) {
        this.name = name;
        this.userName = userName;
    }

    public ProductCollection(int id, String name, String userName, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.userName = userName;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}