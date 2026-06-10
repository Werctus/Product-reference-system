package ru.volkov.cw.model;

public class Category {

    private int id;
    private String name;
    private Integer parentId;
    private String prefix;

    public Category() {}

    public Category(String name, Integer parentId, String prefix) {
        this.name = name;
        this.parentId = parentId;
        this.prefix = prefix;
    }

    public Category(int id, String name, Integer parentId, String prefix) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.prefix = prefix;
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

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}