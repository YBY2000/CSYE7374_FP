package com.example.entity;

public class AccountFactoryManager {
    
    public static Account createUser(String role) {
        AccountFactory factory = getFactory(role);
        return factory.createAccount();
    }
    
    public static Account createUser(String role, String name) {
        AccountFactory factory = getFactory(role);
        return factory.createAccount(name);
    }
    
    public static Account createUserFromData(User userData) {
        AccountFactory factory = getFactory(userData.getRole());
        return factory.createAccountFromData(userData);
    }
    
    private static AccountFactory getFactory(String role) {
        if ("admin".equalsIgnoreCase(role)) {
            return new AdminFactory();
        } else {
            return new UserFactory();
        }
    }
}
