package com.example.entity;

public class PreparingState implements OrderState {
    @Override
    public void handleState() {
        System.out.println("Order is being prepared in kitchen");
    }
} 