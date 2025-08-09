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
    private String status;
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
        this.status = "PENDING";
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        if (observers != null) {
            updateStateFromStatus(status);
        }
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

    public void setState(OrderState state) {
        this.state = state;
    }

    public OrderState getState() {
        return state;
    }

    public void processOrder() {
        state.handleState();
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

    /**
     * Change order state with validation
     */
    public boolean changeState(String newState) {
        if (state != null && !state.canTransitionTo(newState.toUpperCase())) {
            throw new IllegalArgumentException("Invalid state transition from " + 
                state.getStateName() + " to " + newState.toUpperCase());
        }
        
        switch (newState.toUpperCase()) {
            case "PENDING":
                setState(new PendingState());
                break;
            case "PREPARING":
                setState(new PreparingState());
                break;
            case "COMPLETED":
                setState(new CompletedState());
                break;
            case "CANCELLED":
                setState(new CancelledState());
                break;
            default:
                throw new IllegalArgumentException("Unknown state: " + newState);
        }
        this.status = newState.toUpperCase();
        notifyObservers("State changed to: " + newState);
        return true;
    }

    public void updateStateFromStatus(String status) {
        if (status != null) {
            switch (status.toUpperCase()) {
                case "PENDING":
                    setState(new PendingState());
                    break;
                case "PREPARING":
                    setState(new PreparingState());
                    break;
                case "COMPLETED":
                    setState(new CompletedState());
                    break;
                case "CANCELLED":
                    setState(new CancelledState());
                    break;
            }
        }
    }

    /**
     * Get allowed transitions for current state
     */
    public String[] getAllowedTransitions() {
        return state != null ? state.getAllowedTransitions() : new String[]{};
    }

    /**
     * Check if transition to target state is allowed
     */
    public boolean canTransitionTo(String targetState) {
        return state != null && state.canTransitionTo(targetState.toUpperCase());
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
