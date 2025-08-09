package com.example.entity;

public class PreparingState implements OrderState {
    @Override
    public void handleState() {
        System.out.println("Order is being prepared in kitchen");
    }
    
    @Override
    public String getStateName() {
        return "PREPARING";
    }
    
    @Override
    public boolean canTransitionTo(String targetState) {
        return targetState.equals("COMPLETED") || targetState.equals("CANCELLED");
    }
    
    @Override
    public String[] getAllowedTransitions() {
        return new String[]{"COMPLETED", "CANCELLED"};
    }
} 