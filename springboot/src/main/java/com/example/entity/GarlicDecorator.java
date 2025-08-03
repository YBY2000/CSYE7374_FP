package com.example.entity;

import java.math.BigDecimal;

public class GarlicDecorator extends FoodDecorator {
    private static final BigDecimal GARLIC_SURCHARGE = new BigDecimal("1.5");

    public GarlicDecorator(FoodItem foodItem) {
        super(foodItem);
    }

    @Override
    public String getDescription() {
        return getFoodItem().getDescription() + " (Extra Garlic)";
    }

    @Override
    public BigDecimal getPrice() {
        return getFoodItem().getPrice().add(GARLIC_SURCHARGE);
    }
} 