package ru.volkov.cw.model;

import java.math.BigDecimal;

public class Product {

    private int id;
    private String name;
    private String articleNumber;
    private BigDecimal price;
    private String characteristic;
    private BigDecimal discount;
    private BigDecimal vat;
    private int brandId;
    private int unitId;
    private Integer categoryId;  // НОВОЕ ПОЛЕ
    private Integer subcategoryId; // НОВОЕ ПОЛЕ
    private byte[] image;

    public Product() {}

    public Product(int id, String name, String articleNumber, BigDecimal price,
                   String characteristic, BigDecimal discount, BigDecimal vat,
                   int brandId, int unitId, Integer categoryId, Integer subcategoryId, byte[] image) {
        this.id = id;
        this.name = name;
        this.articleNumber = articleNumber;
        this.price = price;
        this.characteristic = characteristic;
        this.discount = discount;
        this.vat = vat;
        this.brandId = brandId;
        this.unitId = unitId;
        this.categoryId = categoryId;
        this.subcategoryId = subcategoryId;
        this.image = image;
    }

    public Product(String name, String articleNumber, BigDecimal price,
                   String characteristic, BigDecimal discount, BigDecimal vat,
                   int brandId, int unitId, Integer categoryId, Integer subcategoryId) {
        this.name = name;
        this.articleNumber = articleNumber;
        this.price = price;
        this.characteristic = characteristic;
        this.discount = discount;
        this.vat = vat;
        this.brandId = brandId;
        this.unitId = unitId;
        this.categoryId = categoryId;
        this.subcategoryId = subcategoryId;
    }

    // Геттеры и сеттеры для новых полей
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public Integer getSubcategoryId() { return subcategoryId; }
    public void setSubcategoryId(Integer subcategoryId) { this.subcategoryId = subcategoryId; }

    // Остальные геттеры и сеттеры без изменений
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getArticleNumber() { return articleNumber; }
    public void setArticleNumber(String articleNumber) { this.articleNumber = articleNumber; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getCharacteristic() { return characteristic; }
    public void setCharacteristic(String characteristic) { this.characteristic = characteristic; }

    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }

    public BigDecimal getVat() { return vat; }
    public void setVat(BigDecimal vat) { this.vat = vat; }

    public int getBrandId() { return brandId; }
    public void setBrandId(int brandId) { this.brandId = brandId; }

    public int getUnitId() { return unitId; }
    public void setUnitId(int unitId) { this.unitId = unitId; }

    public byte[] getImage() { return image; }
    public void setImage(byte[] image) { this.image = image; }
}