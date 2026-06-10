package ru.volkov.cw.model;

public class UnitOfMeasure {

    private int id;
    private String name;

    public UnitOfMeasure() {}

    public UnitOfMeasure(String name) {
        this.name = name;
    }

    public UnitOfMeasure(int id, String name) {
        this.id = id;
        this.name = name;
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
}
