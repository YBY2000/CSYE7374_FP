package com.example.service;

import com.example.database.DatabaseUtil;
import com.example.entity.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class DiscountService {
    
    private PricingContext pricingContext;
    
    public DiscountService() {
        this.pricingContext = new PricingContext();
        // Initialize pricing strategy on startup
        initializePricingStrategy();
    }
    
    /**
     * Initialize pricing strategy
     * Select appropriate strategy based on current discount configuration
     */
    private void initializePricingStrategy() {
        DiscountConfig config = getCurrentDiscountConfig();
        PricingStrategy strategy = createStrategy(config);
        pricingContext.setPricingStrategy(strategy);
    }
    
    /**
     * Create pricing strategy based on discount configuration
     */
    private PricingStrategy createStrategy(DiscountConfig discountConfig) {
        if (discountConfig == null || !discountConfig.getIsEnabled() || !discountConfig.isValidDiscount()) {
            return new RegularPricingStrategy();
        } else {
            return new DiscountPricingStrategy(
                discountConfig.getDiscountRate(), 
                discountConfig.getDescription()
            );
        }
    }
    
    /**
     * Get current active discount configuration
     */
    public DiscountConfig getCurrentDiscountConfig() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM discount_config WHERE is_enabled = 1 ORDER BY updated_time DESC LIMIT 1";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                DiscountConfig config = new DiscountConfig();
                config.setId(rs.getInt("id"));
                config.setDiscountRate(rs.getBigDecimal("discount_rate"));
                config.setIsEnabled(rs.getInt("is_enabled") == 1);
                config.setDescription(rs.getString("description"));
                if (rs.getTimestamp("created_time") != null) {
                    config.setCreatedTime(rs.getTimestamp("created_time").toLocalDateTime());
                }
                if (rs.getTimestamp("updated_time") != null) {
                    config.setUpdatedTime(rs.getTimestamp("updated_time").toLocalDateTime());
                }
                return config;
            }
        } catch (SQLException e) {
            System.err.println("Failed to get discount config: " + e.getMessage());
        } finally {
            DatabaseUtil.closeResources(conn, ps, rs);
        }
        
        // Return default configuration (no discount)
        DiscountConfig defaultConfig = new DiscountConfig();
        defaultConfig.setId(0);
        defaultConfig.setDiscountRate(BigDecimal.ONE);
        defaultConfig.setIsEnabled(false);
        defaultConfig.setDescription("No discount");
        return defaultConfig;
    }
    
    /**
     * Update discount configuration and switch pricing strategy
     */
    public void updateDiscountConfig(BigDecimal discountRate, Boolean isEnabled, String description) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            
            // First disable all existing configurations
            String disableSql = "UPDATE discount_config SET is_enabled = 0";
            ps = conn.prepareStatement(disableSql);
            ps.executeUpdate();
            ps.close();
            
            // Insert new configuration
            String insertSql = "INSERT INTO discount_config (discount_rate, is_enabled, description) VALUES (?, ?, ?)";
            ps = conn.prepareStatement(insertSql);
            ps.setBigDecimal(1, discountRate);
            ps.setBoolean(2, isEnabled);
            ps.setString(3, description);
            
            int result = ps.executeUpdate();
            if (result <= 0) {
                throw new RuntimeException("Failed to update discount config");
            }
            
            // Re-initialize pricing strategy
            initializePricingStrategy();
            
        } catch (SQLException e) {
            System.err.println("Failed to update discount config: " + e.getMessage());
            throw new RuntimeException("Failed to update discount config", e);
        } finally {
            DatabaseUtil.closeResources(conn, ps, null);
        }
    }
    
    /**
     * Calculate order price (using current pricing strategy)
     * @param originalPrice original price
     * @return final price
     */
    public BigDecimal calculateOrderPrice(BigDecimal originalPrice) {
        return pricingContext.calculatePrice(originalPrice);
    }
    
    /**
     * Check if currently in discount period
     * @return true means discount period, false means non-discount period
     */
    public boolean isDiscountPeriod() {
        return pricingContext.isDiscountPeriod();
    }
    
    /**
     * Get current pricing strategy type
     * @return strategy type
     */
    public String getCurrentStrategyType() {
        return pricingContext.getStrategyType();
    }
} 