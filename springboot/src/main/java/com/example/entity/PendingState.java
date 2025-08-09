package com.example.entity;

public class PendingState extends OrderState {
    
    @Override
    public void confirmOrder(Orders order) {
        changeState(order, new PreparingState());
        System.out.println("Order " + order.getOrderNo() + " confirmed and sent to kitchen");
    }
    
    @Override
    public void prepareOrder(Orders order) {
        throw new RuntimeException("Cannot prepare an unconfirmed order");
    }
    
    @Override
    public void completeOrder(Orders order) {
        throw new RuntimeException("Cannot complete an unconfirmed order");
    }
    
    @Override
    public void cancelOrder(Orders order) {
        changeState(order, new CancelledState());
        System.out.println("Order " + order.getOrderNo() + " has been cancelled");
    }
    
    @Override
    public String getStatusValue() {
        return "PENDING";
    }
} 