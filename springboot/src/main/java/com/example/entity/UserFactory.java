package com.example.entity;

public class UserFactory implements AccountFactory {
    
    @Override
    public Account createAccount() {
        return new User();
    }
    
    @Override
    public Account createAccount(String name) {
        User user = new User();
        user.setName(name);
        return user;
    }
    
    @Override
    public Account createAccountFromData(User userData) {
        User user = new User();
        user.setId(userData.getId());
        user.setUsername(userData.getUsername());
        user.setName(userData.getName());
        user.setRole(userData.getRole());
        return user;
    }
}
