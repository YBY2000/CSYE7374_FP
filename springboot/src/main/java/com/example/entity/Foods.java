package com.example.entity;

import java.math.BigDecimal;

public class Foods implements FoodItem {
    private Integer id;
    private String name;
    private String descr;
    private BigDecimal price;
    private String img;

    public Foods(String description, double price) {
        this.descr = description;
        this.price = BigDecimal.valueOf(price);
    }

    public Foods() {}

    @Override
    public String getDescription() {
        return descr;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.descr = description;
    }

    public void setPrice(double price) {
        this.price = BigDecimal.valueOf(price);
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
