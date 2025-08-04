package com.example.entity;

public class PendingState implements OrderState {
    @Override
    public void handleState() {
        System.out.println("Order is in pending state - waiting for payment");
    }
} 