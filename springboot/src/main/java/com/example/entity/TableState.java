package com.example.entity;

public abstract class TableState {
    
    public abstract void occupyTable(Tables table, Integer userId);
    public abstract void releaseTable(Tables table);
    public abstract String getFreeStatus();
    
    protected void changeState(Tables table, TableState newState) {
        table.setState(newState);
    }
} 