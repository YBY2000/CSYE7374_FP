package com.example.entity;

public class PendingState implements OrderState {
    @Override
    public void handleState() {
        System.out.println("Order is in pending state - waiting for admin confirmation");
    }
    
    @Override
    public String getStateName() {
        return "PENDING";
    }
    
    @Override
    public boolean canTransitionTo(String targetState) {
        return targetState.equals("PREPARING") || targetState.equals("CANCELLED");
    }
    
    @Override
    public String[] getAllowedTransitions() {
        return new String[]{"PREPARING", "CANCELLED"};
    }
} 