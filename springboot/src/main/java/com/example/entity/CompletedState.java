package com.example.entity;

public class CompletedState extends OrderState {
    
    @Override
    public void confirmOrder(Orders order) {
        throw new RuntimeException("Order is already completed");
    }
    
    @Override
    public void prepareOrder(Orders order) {
        throw new RuntimeException("Order is already completed");
    }
    
    @Override
    public void completeOrder(Orders order) {
        throw new RuntimeException("Order is already completed");
    }
    
    @Override
    public void cancelOrder(Orders order) {
        throw new RuntimeException("Cannot cancel a completed order");
    }
    
    @Override
    public String getStatusValue() {
        return "COMPLETED";
    }
} 