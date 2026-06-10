package ru.volkov.cw.model;

import java.math.BigDecimal;

public class Company {

    private int id;
    private String name;
    private String inn;
    private BigDecimal rating;
    private String phone;
    private String address;

    public Company() {}

    public Company(String name, String inn, BigDecimal rating, String phone, String address) {
        this.name = name;
        this.inn = inn;
        this.rating = rating;
        this.phone = phone;
        this.address = address;
    }

    public Company(int id, String name, String inn, BigDecimal rating, String phone, String address) {
        this.id = id;
        this.name = name;
        this.inn = inn;
        this.rating = rating;
        this.phone = phone;
        this.address = address;
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

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}