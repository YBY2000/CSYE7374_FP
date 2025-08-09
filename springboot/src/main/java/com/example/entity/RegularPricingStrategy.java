package com.example.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Regular Pricing Strategy - Use original price during non-discount period
 */
public class RegularPricingStrategy implements PricingStrategy {
    
    @Override
    public BigDecimal calculatePrice(BigDecimal originalPrice) {
        if (originalPrice == null) {
            return BigDecimal.ZERO;
        }
        return originalPrice.setScale(2, RoundingMode.HALF_UP);
    }
    
    @Override
    public String getStrategyType() {
        return "REGULAR";
    }
    
    @Override
    public String getPriceDescription(BigDecimal originalPrice) {
        return String.format("Regular price: $%.2f", originalPrice.doubleValue());
    }
    
    @Override
    public boolean hasDiscount() {
        return false;
    }
} 