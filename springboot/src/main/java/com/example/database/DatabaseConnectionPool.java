package com.example.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnectionPool {
    
    private static volatile DatabaseConnectionPool instance;
    
    private HikariDataSource dataSource;
    
    private static final String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/canteen?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false&serverTimezone=GMT%2b8&allowPublicKeyRetrieval=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "password"; //use your own password
    
    private DatabaseConnectionPool() {
        initializeDataSource();
    }
    
    public static DatabaseConnectionPool getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnectionPool.class) {
                if (instance == null) {
                    instance = new DatabaseConnectionPool();
                }
            }
        }
        return instance;
    }
    
    /**
     * Initialize data source
     */
    private void initializeDataSource() {
        try {
            HikariConfig config = new HikariConfig();
            config.setDriverClassName(DRIVER_CLASS_NAME);
            config.setJdbcUrl(URL);
            config.setUsername(USERNAME);
            config.setPassword(PASSWORD);
            
            // Connection pool configuration
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(5);
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);
            config.setConnectionTestQuery("SELECT 1");
            config.setPoolName("CanteenDatabasePool");
            
            dataSource = new HikariDataSource(config);
            
            System.out.println("Database connection pool initialized successfully");
        } catch (Exception e) {
            System.err.println("Database connection pool initialization failed: " + e.getMessage());
            throw new RuntimeException("Database connection pool initialization failed", e);
        }
    }
    
    /**
     * Get database connection
     */
    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("Data source not initialized");
        }
        return dataSource.getConnection();
    }
    
    /**
     * Get data source
     */
    public HikariDataSource getDataSource() {
        return dataSource;
    }
    
    /**
     * Close connection pool
     */
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("Database connection pool closed");
        }
    }
    
    /**
     * Check connection pool health status
     */
    public boolean isHealthy() {
        return dataSource != null && !dataSource.isClosed();
    }
    
    /**
     * Get connection pool statistics
     */
    public String getPoolStats() {
        if (dataSource == null) {
            return "Connection pool not initialized";
        }
        
        return String.format(
            "Pool Status: %s, Active Connections: %d, Idle Connections: %d, Total Connections: %d",
            dataSource.isClosed() ? "Closed" : "Running",
            dataSource.getHikariPoolMXBean().getActiveConnections(),
            dataSource.getHikariPoolMXBean().getIdleConnections(),
            dataSource.getHikariPoolMXBean().getTotalConnections()
        );
    }
} 