package com.example.entity;

import java.math.BigDecimal;

public class SpicyDecorator extends FoodDecorator {
    private static final BigDecimal SPICY_SURCHARGE = new BigDecimal("2.0");

    public SpicyDecorator(FoodItem foodItem) {
        super(foodItem);
    }

    @Override
    public String getDescription() {
        return getFoodItem().getDescription() + " (Spicy)";
    }

    @Override
    public BigDecimal getPrice() {
        return getFoodItem().getPrice().add(SPICY_SURCHARGE);
    }
} 