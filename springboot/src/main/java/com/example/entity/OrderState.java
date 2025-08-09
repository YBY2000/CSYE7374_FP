package com.example.entity;

public interface OrderState {
    /**
     * Handle the current state
     */
    void handleState();
    
    /**
     * Get the state name
     */
    String getStateName();
    
    /**
     * Check if transition to target state is allowed
     */
    boolean canTransitionTo(String targetState);
    
    /**
     * Get allowed transitions from current state
     */
    String[] getAllowedTransitions();
} 