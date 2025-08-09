package com.example.controller;

import com.example.common.Result;
import com.example.entity.DiscountConfig;
import com.example.service.DiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/discount")
public class DiscountController {
    
    @Autowired
    private DiscountService discountService;
    
    /**
     * Get current discount configuration
     */
    @GetMapping("/current")
    public Result getCurrentDiscount() {
        DiscountConfig config = discountService.getCurrentDiscountConfig();
        return Result.success(config);
    }
    
    /**
     * Update discount configuration
     */
    @PostMapping("/update")
    public Result updateDiscount(@RequestBody DiscountConfig config) {
        try {
            // Validate discount rate
            if (config.getDiscountRate() == null || 
                config.getDiscountRate().compareTo(BigDecimal.ZERO) <= 0 || 
                config.getDiscountRate().compareTo(BigDecimal.ONE) > 0) {
                return Result.error("Discount rate must be between 0 and 1");
            }
            
            discountService.updateDiscountConfig(
                config.getDiscountRate(), 
                config.getIsEnabled(), 
                config.getDescription()
            );
            
            return Result.success("Discount configuration updated successfully");
        } catch (Exception e) {
            return Result.error("Failed to update discount configuration: " + e.getMessage());
        }
    }
    
    /**
     * Get current pricing strategy type
     */
    @GetMapping("/strategy")
    public Result getCurrentStrategy() {
        String strategyType = discountService.getCurrentStrategyType();
        boolean isDiscountPeriod = discountService.isDiscountPeriod();
        
        return Result.success(new StrategyInfo(strategyType, isDiscountPeriod));
    }
    
    /**
     * Strategy information class
     */
    public static class StrategyInfo {
        private String strategyType;
        private boolean isDiscountPeriod;
        
        public StrategyInfo(String strategyType, boolean isDiscountPeriod) {
            this.strategyType = strategyType;
            this.isDiscountPeriod = isDiscountPeriod;
        }
        
        public String getStrategyType() {
            return strategyType;
        }
        
        public void setStrategyType(String strategyType) {
            this.strategyType = strategyType;
        }
        
        public boolean isDiscountPeriod() {
            return isDiscountPeriod;
        }
        
        public void setDiscountPeriod(boolean discountPeriod) {
            isDiscountPeriod = discountPeriod;
        }
    }
} 