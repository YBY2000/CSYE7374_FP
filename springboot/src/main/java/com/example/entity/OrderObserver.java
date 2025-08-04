package com.example.entity;

public interface OrderObserver {
    void onOrderChanged(Orders order, String event);
} 