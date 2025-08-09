package com.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Orders implements Cloneable, OrderSubject {
    private Integer id;
    private String content;
    private BigDecimal total;
    private Integer userId;
    private String time;
    private String orderNo;
    private String userName;
    
    private List<Foods> foodList;
    @JsonIgnore
    private OrderState state;
    @JsonIgnore
    private List<OrderObserver> observers;

    public Orders() {
        this.observers = new ArrayList<>();
        this.state = new PendingState();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void confirmOrder() {
        state.confirmOrder(this);
    }
    
    public void prepareOrder() {
        state.prepareOrder(this);
    }
    
    public void completeOrder() {
        state.completeOrder(this);
    }
    
    public void cancelOrder() {
        state.cancelOrder(this);
    }
    
    public String getStatus() {
        return state.getStatusValue();
    }
    
    public void setState(OrderState state) {
        this.state = state;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<Foods> getFoodList() {
        return foodList;
    }

    public void setFoodList(List<Foods> foodList) {
        this.foodList = foodList;
    }



    @Override
    public void addObserver(OrderObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(OrderObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String event) {
        for (OrderObserver observer : observers) {
            observer.onOrderChanged(this, event);
        }
    }





    @Override
    public Orders clone() {
        try {
            Orders cloned = (Orders) super.clone();
            if (this.foodList != null) {
                cloned.foodList = new ArrayList<>(this.foodList);
            }
            if (this.observers != null) {
                cloned.observers = new ArrayList<>(this.observers);
            }
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone not supported", e);
        }
    }
}
