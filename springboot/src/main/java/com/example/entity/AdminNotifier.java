package com.example.entity;

import com.example.service.WebSocketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AdminNotifier implements OrderObserver {
    
    @Autowired
    private WebSocketService webSocketService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public void onOrderChanged(Orders order, String event) {
        System.out.println("Admin notification: Order " + order.getOrderNo() + " - " + event);
        
        try {
            if (webSocketService != null) {
                Map<String, Object> orderData = new HashMap<>();
                orderData.put("id", order.getId());
                orderData.put("orderNo", order.getOrderNo());
                orderData.put("content", order.getContent());
                orderData.put("total", order.getTotal());
                orderData.put("userName", order.getUserName());
                orderData.put("status", order.getStatus());
                
                String orderDataJson = objectMapper.writeValueAsString(orderData);
                
                if (event.contains("Order created")) {
                    webSocketService.sendNewOrderToAdmin(orderDataJson);
                } else {
                    webSocketService.sendOrderUpdateToAdmin(order.getId(), order.getStatus(), event);
                }
            }
        } catch (Exception e) {
            System.err.println("Error sending admin notification: " + e.getMessage());
        }
    }
} 