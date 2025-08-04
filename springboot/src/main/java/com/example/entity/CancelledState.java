package com.example.entity;

public class CancelledState implements OrderState {
    @Override
    public void handleState() {
        System.out.println("Order has been cancelled");
    }
} 