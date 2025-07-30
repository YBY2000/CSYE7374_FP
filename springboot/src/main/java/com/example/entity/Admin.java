package com.example.entity;

public class Admin extends AccountBase {
    
    @Override
    public String getRole() {
        return role != null ? role : "admin";
    }

    @Override
    public Admin clone() {
        try {
            return (Admin) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
