package com.example.entity;

public class AdminFactory implements AccountFactory {
    
    @Override
    public Account createAccount() {
        return new Admin();
    }
    
    @Override
    public Account createAccount(String name) {
        Admin admin = new Admin();
        admin.setName(name);
        return admin;
    }
    
    @Override
    public Account createAccountFromData(User userData) {
        Admin admin = new Admin();
        admin.setId(userData.getId());
        admin.setUsername(userData.getUsername());
        admin.setName(userData.getName());
        admin.setRole(userData.getRole());
        return admin;
    }
}
