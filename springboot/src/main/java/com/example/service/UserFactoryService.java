package com.example.service;

import com.example.entity.Account;
import com.example.entity.User;
import com.example.entity.AccountFactoryManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserFactoryService {
    
    public Account createNormalUser() {
        return AccountFactoryManager.createUser("user", "Normal User");
    }
    
    public Account createAdminUser() {
        return AccountFactoryManager.createUser("admin", "Admin User");
    }
    
    public User cloneUser(User originalUser) {
        return originalUser.clone();
    }
    
    public Account createUserByRole(String role) {
        return AccountFactoryManager.createUser(role);
    }
    
    public List<User> cloneUsers(List<User> users) {
        List<User> clonedUsers = new ArrayList<>();
        for (User user : users) {
            clonedUsers.add(user.clone());
        }
        return clonedUsers;
    }
}
