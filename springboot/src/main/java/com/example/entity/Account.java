package com.example.entity;

public interface Account extends Cloneable {
    Integer getId();
    
    void setId(Integer id);
    
    String getUsername();
    
    void setUsername(String username);
    
    String getPassword();
    
    void setPassword(String password);
    
    String getName();
    
    void setName(String name);
    
    String getAvatar();
    
    void setAvatar(String avatar);
    
    String getRole();
    
    void setRole(String role);
}
