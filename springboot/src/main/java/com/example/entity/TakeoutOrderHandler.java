package com.example.entity;

public class TakeoutOrderHandler implements OrderHandler {
    @Override
    public void handleOrder() {
        System.out.println("Handling takeout order - packaging and preparing for pickup");
    }
} 