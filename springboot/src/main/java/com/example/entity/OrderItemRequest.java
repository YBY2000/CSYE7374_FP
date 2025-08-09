package com.example.entity;

import java.util.List;

public class OrderItemRequest {
    private Integer foodId;
    private Integer quantity;
    private List<String> decorators; // e.g. ["spicy", "garlic"]

    public Integer getFoodId() {
        return foodId;
    }

    public void setFoodId(Integer foodId) {
        this.foodId = foodId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public List<String> getDecorators() {
        return decorators;
    }

    public void setDecorators(List<String> decorators) {
        this.decorators = decorators;
    }
}

