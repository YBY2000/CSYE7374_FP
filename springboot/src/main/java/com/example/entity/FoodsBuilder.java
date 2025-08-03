package com.example.entity;

import java.math.BigDecimal;

public class FoodsBuilder {
    private Integer id;
    private String name;
    private BigDecimal price;
    private String descr;
    private String img;

    public FoodsBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public FoodsBuilder name(String name) {
        this.name = name;
        return this;
    }

    public FoodsBuilder price(BigDecimal price) {
        this.price = price;
        return this;
    }

    public FoodsBuilder descr(String descr) {
        this.descr = descr;
        return this;
    }

    public FoodsBuilder img(String img) {
        this.img = img;
        return this;
    }

    public Foods build() {
        Foods foods = new Foods(descr, price != null ? price.doubleValue() : 0.0);
        foods.setId(id);
        foods.setName(name);
        foods.setImg(img);
        return foods;
    }
} 