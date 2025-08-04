package com.example.entity;

public class OrderHandlerFactory {
    public static OrderHandler getHandler(String type) {
        switch (type.toLowerCase()) {
            case "dinein":
                return new DineInOrderHandler();
            case "takeout":
                return new TakeoutOrderHandler();
            default:
                throw new IllegalArgumentException("Unknown order type: " + type);
        }
    }
} 