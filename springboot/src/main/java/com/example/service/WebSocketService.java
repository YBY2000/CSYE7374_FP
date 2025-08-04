package com.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WebSocketService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void sendNotificationToUser(String userId, String message) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "NOTIFICATION");
        notification.put("message", message);
        notification.put("timestamp", System.currentTimeMillis());
        
        messagingTemplate.convertAndSendToUser(userId, "/topic/notifications", notification);
    }

    public void sendOrderStatusUpdate(String userId, Integer orderId, String status, String message) {
        Map<String, Object> update = new HashMap<>();
        update.put("type", "ORDER_STATUS_UPDATE");
        update.put("orderId", orderId);
        update.put("status", status);
        update.put("message", message);
        update.put("timestamp", System.currentTimeMillis());
        
        messagingTemplate.convertAndSendToUser(userId, "/topic/orders", update);
    }

    public void sendNewOrderToAdmin(String orderData) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "NEW_ORDER");
        notification.put("orderData", orderData);
        notification.put("timestamp", System.currentTimeMillis());
        
        messagingTemplate.convertAndSend("/topic/admin/orders", notification);
    }

    public void sendOrderUpdateToAdmin(Integer orderId, String status, String message) {
        Map<String, Object> update = new HashMap<>();
        update.put("type", "ORDER_UPDATE");
        update.put("orderId", orderId);
        update.put("status", status);
        update.put("message", message);
        update.put("timestamp", System.currentTimeMillis());
        
        messagingTemplate.convertAndSend("/topic/admin/orders", update);
    }
} 