package com.example.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class WebSocketController {

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public String greeting(String message) throws Exception {
        Thread.sleep(1000);
        return "Hello, " + message + "!";
    }

    @MessageMapping("/order/status")
    @SendTo("/topic/order-status")
    public Map<String, Object> handleOrderStatus(Map<String, Object> message) {
        return message;
    }
} 