package com.example.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseUtil {
    
    private static final DatabaseConnectionPool connectionPool = DatabaseConnectionPool.getInstance();
    
    /**
     * Get database connection
     */
    public static Connection getConnection() throws SQLException {
        return connectionPool.getConnection();
    }
    
    /**
     * Close resources safely
     */
    public static void closeResources(Connection conn, PreparedStatement ps, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            System.err.println("Failed to close ResultSet: " + e.getMessage());
        }
        
        try {
            if (ps != null) {
                ps.close();
            }
        } catch (SQLException e) {
            System.err.println("Failed to close PreparedStatement: " + e.getMessage());
        }
        
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Failed to close Connection: " + e.getMessage());
        }
    }
    
    /**
     * Execute query operation
     */
    public static ResultSet executeQuery(String sql, Object... params) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            
            // Set parameters
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            
            rs = ps.executeQuery();
            return rs;
        } catch (SQLException e) {
            closeResources(conn, ps, rs);
            throw e;
        }
    }
    
    /**
     * Execute update operation (INSERT, UPDATE, DELETE)
     */
    public static int executeUpdate(String sql, Object... params) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            
            // Set parameters
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            
            return ps.executeUpdate();
        } finally {
            closeResources(conn, ps, null);
        }
    }
    
    /**
     * Get connection pool status
     */
    public static String getPoolStatus() {
        return connectionPool.getPoolStats();
    }
    
    /**
     * Check connection pool health status
     */
    public static boolean isPoolHealthy() {
        return connectionPool.isHealthy();
    }
    
    /**
     * Test database connection
     */
    public static boolean testConnection() {
        Connection conn = null;
        try {
            conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        } finally {
            closeResources(conn, null, null);
        }
    }
} 