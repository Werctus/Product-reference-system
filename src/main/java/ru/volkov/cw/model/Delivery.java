package ru.volkov.cw.model;

import java.time.LocalDateTime;

public class Delivery {

    private int id;
    private String documentNumber;
    private String status;
    private int companyId;
    private int storeId;
    private LocalDateTime createdAt;

    public Delivery() {}

    public Delivery(String documentNumber, int companyId, int storeId) {
        this.documentNumber = documentNumber;
        this.companyId = companyId;
        this.storeId = storeId;
    }

    public Delivery(int id, String documentNumber, String status, int companyId, int storeId, LocalDateTime createdAt) {
        this.id = id;
        this.documentNumber = documentNumber;
        this.status = status;
        this.companyId = companyId;
        this.storeId = storeId;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}