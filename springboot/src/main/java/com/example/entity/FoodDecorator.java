package com.example.entity;

import java.math.BigDecimal;

public abstract class FoodDecorator implements FoodItem {
    private FoodItem foodItem;

    public FoodDecorator(FoodItem foodItem) {
        this.foodItem = foodItem;
    }

    @Override
    public String getDescription() {
        return foodItem.getDescription();
    }

    @Override
    public BigDecimal getPrice() {
        return foodItem.getPrice();
    }

    protected FoodItem getFoodItem() {
        return foodItem;
    }
} 