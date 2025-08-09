package com.example.entity;

public class CancelledState extends OrderState {
    
    @Override
    public void confirmOrder(Orders order) {
        throw new RuntimeException("Cannot confirm a cancelled order");
    }
    
    @Override
    public void prepareOrder(Orders order) {
        throw new RuntimeException("Cannot prepare a cancelled order");
    }
    
    @Override
    public void completeOrder(Orders order) {
        throw new RuntimeException("Cannot complete a cancelled order");
    }
    
    @Override
    public void cancelOrder(Orders order) {
        throw new RuntimeException("Order is already cancelled");
    }
    
    @Override
    public String getStatusValue() {
        return "CANCELLED";
    }
} 