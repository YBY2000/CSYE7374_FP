package com.example.entity;

public class PreparingState extends OrderState {
    
    @Override
    public void confirmOrder(Orders order) {
        throw new RuntimeException("Order is already confirmed and being prepared");
    }
    
    @Override
    public void prepareOrder(Orders order) {
        System.out.println("Order " + order.getOrderNo() + " is being prepared in kitchen");
    }
    
    @Override
    public void completeOrder(Orders order) {
        changeState(order, new CompletedState());
        System.out.println("Order " + order.getOrderNo() + " is ready for pickup");
    }
    
    @Override
    public void cancelOrder(Orders order) {
        changeState(order, new CancelledState());
        System.out.println("Order " + order.getOrderNo() + " cancelled during preparation");
    }
    
    @Override
    public String getStatusValue() {
        return "PREPARING";
    }
} 