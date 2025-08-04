package com.example.entity;

import com.example.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserNotifier implements OrderObserver {
    
    @Autowired
    private WebSocketService webSocketService;
    
    @Override
    public void onOrderChanged(Orders order, String event) {
        System.out.println("User notification: Order " + order.getOrderNo() + " - " + event);
        
        try {
                    String userId = order.getUserId().toString();
        String message = "Order " + order.getOrderNo() + ": " + event;
            
            if (webSocketService != null) {
                webSocketService.sendNotificationToUser(userId, message);
                webSocketService.sendOrderStatusUpdate(userId, order.getId(), order.getStatus(), event);
            }
        } catch (Exception e) {
            System.err.println("WebSocket notification failed: " + e.getMessage());
        }
    }
} 