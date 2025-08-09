package com.example.entity;

public class CompletedState implements OrderState {
    @Override
    public void handleState() {
        System.out.println("Order is completed and ready for pickup/delivery");
    }
    
    @Override
    public String getStateName() {
        return "COMPLETED";
    }
    
    @Override
    public boolean canTransitionTo(String targetState) {
        return false; // Completed state is final
    }
    
    @Override
    public String[] getAllowedTransitions() {
        return new String[]{};
    }
} 