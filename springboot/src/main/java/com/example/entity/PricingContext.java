package com.example.entity;

import java.math.BigDecimal;

/**
 * Pricing Context - Strategy Pattern Context
 * Responsible for selecting appropriate pricing strategy
 */
public class PricingContext {
    
    private PricingStrategy pricingStrategy;
    
    public PricingContext() {
        this.pricingStrategy = new RegularPricingStrategy();
    }
    
    /**
     * Set pricing strategy
     * @param strategy pricing strategy
     */
    public void setPricingStrategy(PricingStrategy strategy) {
        this.pricingStrategy = strategy;
    }
    
    /**
     * Get current pricing strategy
     * @return current pricing strategy
     */
    public PricingStrategy getPricingStrategy() {
        return pricingStrategy;
    }
    
    /**
     * Calculate price
     * @param originalPrice original price
     * @return final price
     */
    public BigDecimal calculatePrice(BigDecimal originalPrice) {
        return pricingStrategy.calculatePrice(originalPrice);
    }
    

    
    /**
     * Whether in discount period
     * @return true means discount period, false means non-discount period
     */
    public boolean isDiscountPeriod() {
        return pricingStrategy.hasDiscount();
    }
    
    /**
     * Get strategy type
     * @return strategy type
     */
    public String getStrategyType() {
        return pricingStrategy.getStrategyType();
    }
} 