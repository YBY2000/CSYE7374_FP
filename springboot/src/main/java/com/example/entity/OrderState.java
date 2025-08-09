package com.example.entity;

public abstract class OrderState {
    
    public abstract void confirmOrder(Orders order);
    public abstract void prepareOrder(Orders order);
    public abstract void completeOrder(Orders order);
    public abstract void cancelOrder(Orders order);
    public abstract String getStatusValue();
    
    protected void changeState(Orders order, OrderState newState) {
        order.setState(newState);
    }
} 