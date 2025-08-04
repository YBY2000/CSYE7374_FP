package com.example.entity;

public class TableFreeState implements TableState {
    
    @Override
    public void handleState() {
        System.out.println("Table is available for use");
    }
    
    @Override
    public String getStateName() {
        return "Yes";
    }
} 