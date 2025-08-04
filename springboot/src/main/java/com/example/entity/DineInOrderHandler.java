package com.example.entity;

public class DineInOrderHandler implements OrderHandler {
    @Override
    public void handleOrder() {
        System.out.println("Handling dine-in order - assigning table and preparing food");
    }
} 