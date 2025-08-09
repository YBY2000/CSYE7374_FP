package com.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Tables {
    private Integer id;
    private String no;
    private String unit;
    private Integer userId;
    private String userName;
    @JsonIgnore
    private TableState state;

    public Tables() {
        this.state = new TableFreeState();
    }

    public void occupyTable(Integer userId) {
        state.occupyTable(this, userId);
    }
    
    public void releaseTable() {
        state.releaseTable(this);
    }
    
    public String getFree() {
        return state.getFreeStatus();
    }
    
    public void setState(TableState state) {
        this.state = state;
    }
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getNo() {
        return no;
    }
    
    public void setNo(String no) {
        this.no = no;
    }
    
    public String getUnit() {
        return unit;
    }
    
    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
}
