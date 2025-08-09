package com.example.entity;

public class TableOccupiedState extends TableState {
    
    @Override
    public void occupyTable(Tables table, Integer userId) {
        throw new RuntimeException("Cannot occupy a table that is already occupied");
    }
    
    @Override
    public void releaseTable(Tables table) {
        Integer releasedUserId = table.getUserId();
        
        table.setUserId(null);
        changeState(table, new TableFreeState());
        
        System.out.println("✅ Table " + table.getNo() + " has been released from user " + releasedUserId);
    }
    
    @Override
    public String getFreeStatus() {
        return "No";
    }
} 