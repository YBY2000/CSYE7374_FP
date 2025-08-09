package com.example.entity;

public class TableFreeState extends TableState {
    
    @Override
    public void occupyTable(Tables table, Integer userId) {
        if (userId == null) {
            throw new RuntimeException("User ID cannot be null");
        }
        
        table.setUserId(userId);
        changeState(table, new TableOccupiedState());
        
        System.out.println("✅ Table " + table.getNo() + " has been occupied by user " + userId);
    }
    
    @Override
    public void releaseTable(Tables table) {
        throw new RuntimeException("Cannot release a table that is already free");
    }
    
    @Override
    public String getFreeStatus() {
        return "Yes";
    }
} 