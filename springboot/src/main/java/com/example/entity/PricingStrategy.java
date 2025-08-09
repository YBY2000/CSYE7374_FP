package com.example.entity;

import java.math.BigDecimal;

/**
 * Pricing Strategy Interface - Strategy Pattern
 * Different pricing strategies for different periods (discount/regular)
 */
public interface PricingStrategy {
    /**
     * Calculate order price
     * @param originalPrice original price
     * @return final price
     */
    BigDecimal calculatePrice(BigDecimal originalPrice);
    
    /**
     * Get strategy type
     * @return strategy type name
     */
    String getStrategyType();
    
    /**
     * Get price description information
     * @param originalPrice original price
     * @return price description
     */
    String getPriceDescription(BigDecimal originalPrice);
    
    /**
     * Whether has discount
     * @return true means has discount, false means no discount
     */
    boolean hasDiscount();
} 