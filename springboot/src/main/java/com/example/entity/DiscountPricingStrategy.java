package com.example.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Discount Pricing Strategy - Use discount price during discount period
 */
public class DiscountPricingStrategy implements PricingStrategy {
    
    private final BigDecimal discountRate;
    private final String description;
    
    public DiscountPricingStrategy(BigDecimal discountRate, String description) {
        this.discountRate = discountRate;
        this.description = description;
    }
    
    @Override
    public BigDecimal calculatePrice(BigDecimal originalPrice) {
        if (originalPrice == null) {
            return BigDecimal.ZERO;
        }
        return originalPrice.multiply(discountRate).setScale(2, RoundingMode.HALF_UP);
    }
    
    @Override
    public String getStrategyType() {
        return "DISCOUNT";
    }
    
    @Override
    public String getPriceDescription(BigDecimal originalPrice) {
        return String.format("%s (%.0f%% off)", description, (1 - discountRate.doubleValue()) * 100);
    }
    
    @Override
    public boolean hasDiscount() {
        return true;
    }
    
    public BigDecimal getDiscountRate() {
        return discountRate;
    }
    
    public String getDescription() {
        return description;
    }
    
    public BigDecimal getDiscountAmount(BigDecimal originalPrice) {
        return originalPrice.subtract(calculatePrice(originalPrice));
    }
} 