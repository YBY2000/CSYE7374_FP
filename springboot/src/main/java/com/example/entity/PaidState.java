package com.example.entity;

public class PaidState implements OrderState {
    @Override
    public void handleState() {
        System.out.println("Order is paid - ready for preparation");
    }
} 