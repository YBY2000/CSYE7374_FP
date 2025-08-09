package com.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.entity.Orders;
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
        System.out.println("Sending order status update to user: " + userId);
        Map<String, Object> update = new HashMap<>();
        update.put("type", "ORDER_STATUS_UPDATE");
        update.put("orderId", orderId);
        update.put("status", status);
        update.put("message", message);
        update.put("timestamp", System.currentTimeMillis());
        
        messagingTemplate.convertAndSendToUser(userId, "/topic/orders", update);
        System.out.println("Order status update sent to user " + userId + " at /topic/orders");
    }

    public void sendNewOrderToAdmin(String orderData) {
        System.out.println("Sending new order notification to admin");
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "NEW_ORDER");
        notification.put("orderData", orderData);
        notification.put("timestamp", System.currentTimeMillis());
        
        messagingTemplate.convertAndSend("/topic/admin/orders", notification);
        System.out.println("New order notification sent to admin");
    }

    public void sendOrderUpdateToAdmin(Integer orderId, String status, String message) {
        System.out.println("Sending order update to admin for order: " + orderId);
        Map<String, Object> update = new HashMap<>();
        update.put("type", "ORDER_UPDATE");
        update.put("orderId", orderId);
        update.put("status", status);
        update.put("message", message);
        update.put("timestamp", System.currentTimeMillis());
        
        messagingTemplate.convertAndSend("/topic/admin/orders", update);
        System.out.println("Order update sent to admin at /topic/admin/orders");
    }
    
    public void sendOrderStatusChangeNotification(Orders order, String oldStatus, String newStatus, String actionBy) {
        Map<String, Object> userNotification = new HashMap<>();
        userNotification.put("type", "ORDER_STATUS_CHANGE");
        userNotification.put("orderId", order.getId());
        userNotification.put("orderNo", order.getOrderNo());
        userNotification.put("oldStatus", oldStatus);
        userNotification.put("newStatus", newStatus);
        userNotification.put("content", order.getContent());
        userNotification.put("total", order.getTotal());
        userNotification.put("actionBy", actionBy);
        userNotification.put("timestamp", System.currentTimeMillis());
        
        messagingTemplate.convertAndSendToUser(
            order.getUserId().toString(), 
            "/topic/order-status", 
            userNotification
        );
        
        Map<String, Object> adminNotification = new HashMap<>();
        adminNotification.put("type", "ORDER_STATUS_CHANGE");
        adminNotification.put("orderId", order.getId());
        adminNotification.put("orderNo", order.getOrderNo());
        adminNotification.put("userName", order.getUserName());
        adminNotification.put("oldStatus", oldStatus);
        adminNotification.put("newStatus", newStatus);
        adminNotification.put("content", order.getContent());
        adminNotification.put("total", order.getTotal());
        adminNotification.put("actionBy", actionBy);
        adminNotification.put("timestamp", System.currentTimeMillis());
        
        messagingTemplate.convertAndSend("/topic/admin/order-status", adminNotification);
    }
} 