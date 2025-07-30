package com.example.entity;

public interface AccountFactory {
    Account createAccount();
    Account createAccount(String name);
    Account createAccountFromData(User userData);
}
