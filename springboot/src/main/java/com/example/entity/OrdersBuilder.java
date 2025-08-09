package com.example.entity;

import java.math.BigDecimal;
import java.util.List;

public class OrdersBuilder {
    private Integer id;
    private String content;
    private BigDecimal total;
    private Integer userId;
    private String time;
    private String status;
    private String orderNo;
    private String userName;

    public OrdersBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public OrdersBuilder content(String content) {
        this.content = content;
        return this;
    }

    public OrdersBuilder total(BigDecimal total) {
        this.total = total;
        return this;
    }

    public OrdersBuilder userId(Integer userId) {
        this.userId = userId;
        return this;
    }

    public OrdersBuilder time(String time) {
        this.time = time;
        return this;
    }

    public OrdersBuilder status(String status) {
        this.status = status;
        return this;
    }

    public OrdersBuilder orderNo(String orderNo) {
        this.orderNo = orderNo;
        return this;
    }

    public OrdersBuilder userName(String userName) {
        this.userName = userName;
        return this;
    }

    public Orders build() {
        Orders order = new Orders();
        order.setId(id);
        order.setContent(content);
        order.setTotal(total);
        order.setUserId(userId);
        order.setTime(time);
        order.setOrderNo(orderNo);
        order.setUserName(userName);
        
        // Set state based on status if provided
        if (status != null) {
            switch (status.toUpperCase()) {
                case "PENDING":
                    order.setState(new PendingState());
                    break;
                case "PREPARING":
                    order.setState(new PreparingState());
                    break;
                case "COMPLETED":
                    order.setState(new CompletedState());
                    break;
                case "CANCELLED":
                    order.setState(new CancelledState());
                    break;
                default:
                    order.setState(new PendingState());
                    break;
            }
        }
        
        return order;
    }
} 