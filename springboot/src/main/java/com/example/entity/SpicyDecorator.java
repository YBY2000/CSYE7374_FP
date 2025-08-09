package com.example.entity;

import java.math.BigDecimal;

public class SpicyDecorator extends FoodDecorator {

    public SpicyDecorator(FoodItem foodItem) {
        super(foodItem);
    }

    @Override
    public String getDescription() {
        return getFoodItem().getDescription() + " (Spicy)";
    }

    @Override
    public BigDecimal getPrice() {
        return getFoodItem().getPrice().add(Decorator.SPICY.getSurcharge());
    }
} 