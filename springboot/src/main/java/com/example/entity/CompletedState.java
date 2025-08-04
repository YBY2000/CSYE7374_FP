package com.example.entity;

public class CompletedState implements OrderState {
    @Override
    public void handleState() {
        System.out.println("Order is completed and ready for pickup/delivery");
    }
} 