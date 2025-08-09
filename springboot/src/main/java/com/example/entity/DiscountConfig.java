package com.example.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DiscountConfig {
    private Integer id;
    private BigDecimal discountRate;
    private Boolean isEnabled;
    private String description;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    public DiscountConfig() {
        this.discountRate = BigDecimal.ONE;
        this.isEnabled = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }

    public Boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    public boolean isValidDiscount() {
        return discountRate != null && 
               discountRate.compareTo(BigDecimal.ZERO) > 0 && 
               discountRate.compareTo(BigDecimal.ONE) <= 0;
    }
} 