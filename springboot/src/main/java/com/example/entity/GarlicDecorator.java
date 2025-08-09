package com.example.entity;

import java.math.BigDecimal;

public class GarlicDecorator extends FoodDecorator {

    public GarlicDecorator(FoodItem foodItem) {
        super(foodItem);
    }

    @Override
    public String getDescription() {
        return getFoodItem().getDescription() + " (Extra Garlic)";
    }

    @Override
    public BigDecimal getPrice() {
        return getFoodItem().getPrice().add(Decorator.GARLIC.getSurcharge());
    }
} 