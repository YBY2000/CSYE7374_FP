package com.example.entity;

public class User extends AccountBase {

    @Override
    public String getRole() {
        return role != null ? role : "user";
    }

    @Override
    public User clone() {
        try {
            return (User) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
