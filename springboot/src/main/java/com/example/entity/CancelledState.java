package com.example.entity;

public class CancelledState implements OrderState {
    @Override
    public void handleState() {
        System.out.println("Order has been cancelled");
    }
    
    @Override
    public String getStateName() {
        return "CANCELLED";
    }
    
    @Override
    public boolean canTransitionTo(String targetState) {
        return false; // Cancelled state is final
    }
    
    @Override
    public String[] getAllowedTransitions() {
        return new String[]{};
    }
} 