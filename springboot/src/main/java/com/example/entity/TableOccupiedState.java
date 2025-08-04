package com.example.entity;

public class TableOccupiedState implements TableState {
    
    @Override
    public void handleState() {
        System.out.println("Table is currently occupied");
    }
    
    @Override
    public String getStateName() {
        return "No";
    }
} 