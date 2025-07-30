package com.example.service;

import com.example.database.DatabaseUtil;
import com.example.entity.Tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TablesService {
    
    /**
     * Add new table 
     */
    public void add(Tables table) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "INSERT INTO tables (no, unit, free, user_id) VALUES (?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, table.getNo());
            ps.setString(2, table.getUnit());
            ps.setString(3, table.getFree());
            ps.setInt(4, table.getUserId());
            
            int result = ps.executeUpdate();
            if (result <= 0) {
                throw new RuntimeException("Failed to add table");
            }
            
        } catch (SQLException e) {
            System.err.println("Add table failed: " + e.getMessage());
            throw new RuntimeException("Failed to add table", e);
        } finally {
            DatabaseUtil.closeResources(conn, ps, null);
        }
    }
    
    /**
     * Delete table by ID 
     */
    public void deleteById(Integer id) {
        try {
            String sql = "DELETE FROM tables WHERE id = ?";
            int result = DatabaseUtil.executeUpdate(sql, id);
            if (result <= 0) {
                throw new RuntimeException("Table not found");
            }
        } catch (SQLException e) {
            System.err.println("Delete table failed: " + e.getMessage());
            throw new RuntimeException("Failed to delete table", e);
        }
    }
    
    /**
     * Update table 
     */
    public void updateById(Tables table) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "UPDATE tables SET no=?, unit=?, free=?, user_id=? WHERE id=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, table.getNo());
            ps.setString(2, table.getUnit());
            ps.setString(3, table.getFree());
            ps.setInt(4, table.getUserId());
            ps.setInt(5, table.getId());
            
            int result = ps.executeUpdate();
            if (result <= 0) {
                throw new RuntimeException("Table not found");
            }
            
        } catch (SQLException e) {
            System.err.println("Update table failed: " + e.getMessage());
            throw new RuntimeException("Failed to update table", e);
        } finally {
            DatabaseUtil.closeResources(conn, ps, null);
        }
    }
    
    /**
     * Get table by ID 
     */
    public Tables selectById(Integer id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT t.*, u.name as userName FROM tables t LEFT JOIN user u ON t.user_id = u.id WHERE t.id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                Tables table = new Tables();
                table.setId(rs.getInt("id"));
                table.setNo(rs.getString("no"));
                table.setUnit(rs.getString("unit"));
                table.setFree(rs.getString("free"));
                table.setUserId(rs.getInt("user_id"));
                table.setUserName(rs.getString("userName"));
                return table;
            }
            
        } catch (SQLException e) {
            System.err.println("Get table by ID failed: " + e.getMessage());
        } finally {
            DatabaseUtil.closeResources(conn, ps, rs);
        }
        
        return null;
    }
    
    /**
     * Get all tables 
     */
    public List<Tables> selectAll(String name) {
        List<Tables> tables = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT t.*, u.name as userName FROM tables t LEFT JOIN user u ON t.user_id = u.id";
            if (name != null && !name.isEmpty()) {
                sql += " WHERE t.no LIKE ?";
            }
            sql += " ORDER BY t.id DESC";
            
            ps = conn.prepareStatement(sql);
            if (name != null && !name.isEmpty()) {
                ps.setString(1, "%" + name + "%");
            }
            
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Tables table = new Tables();
                table.setId(rs.getInt("id"));
                table.setNo(rs.getString("no"));
                table.setUnit(rs.getString("unit"));
                table.setFree(rs.getString("free"));
                table.setUserId(rs.getInt("user_id"));
                table.setUserName(rs.getString("userName"));
                tables.add(table);
            }
            
        } catch (SQLException e) {
            System.err.println("Get all tables failed: " + e.getMessage());
        } finally {
            DatabaseUtil.closeResources(conn, ps, rs);
        }
        
        return tables;
    }
    
    /**
     * Get table by user ID 
     */
    public Tables selectByUserId(Integer userId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT t.*, u.name as userName FROM tables t LEFT JOIN user u ON t.user_id = u.id WHERE t.user_id = ? ORDER BY t.id DESC LIMIT 1";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                Tables table = new Tables();
                table.setId(rs.getInt("id"));
                table.setNo(rs.getString("no"));
                table.setUnit(rs.getString("unit"));
                table.setFree(rs.getString("free"));
                table.setUserId(rs.getInt("user_id"));
                table.setUserName(rs.getString("userName"));
                return table;
            }
            
        } catch (SQLException e) {
            System.err.println("Get table by user ID failed: " + e.getMessage());
        } finally {
            DatabaseUtil.closeResources(conn, ps, rs);
        }
        
        return null;
    }
    
    /**
     * Add order to table 
     */
    public void addOrder(Tables table) {
        try {
            String sql = "UPDATE tables SET user_id = ?, free = 'occupied' WHERE id = ?";
            int result = DatabaseUtil.executeUpdate(sql, table.getUserId(), table.getId());
            if (result <= 0) {
                throw new RuntimeException("Table not found");
            }
        } catch (SQLException e) {
            System.err.println("Add order to table failed: " + e.getMessage());
            throw new RuntimeException("Failed to add order to table", e);
        }
    }
    
    /**
     * Remove order from table 
     */
    public void removeOrder(Tables table) {
        try {
            String sql = "UPDATE tables SET user_id = NULL, free = 'free' WHERE id = ?";
            int result = DatabaseUtil.executeUpdate(sql, table.getId());
            if (result <= 0) {
                throw new RuntimeException("Table not found");
            }
        } catch (SQLException e) {
            System.err.println("Remove order from table failed: " + e.getMessage());
            throw new RuntimeException("Failed to remove order from table", e);
        }
    }
    
    /**
     * Delete multiple tables by IDs 
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            this.deleteById(id);
        }
    }
    
    /**
     * Get tables with pagination 
     */
    public com.github.pagehelper.PageInfo<Tables> selectPage(String name, Integer pageNum, Integer pageSize) {
        
        List<Tables> allTables = selectAll(name);
        
        int total = allTables.size();
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, total);
        
        List<Tables> pageData = new ArrayList<>();
        if (startIndex < total) {
            pageData = allTables.subList(startIndex, endIndex);
        }
        
        com.github.pagehelper.PageInfo<Tables> pageInfo = new com.github.pagehelper.PageInfo<>(pageData);
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        pageInfo.setTotal(total);
        pageInfo.setPages((int) Math.ceil((double) total / pageSize));
        
        return pageInfo;
    }
    

    
    /**
     * Update table status (free/occupied) 
     */
    public void updateStatus(Integer id, String status) {
        try {
            String sql = "UPDATE tables SET free = ? WHERE id = ?";
            int result = DatabaseUtil.executeUpdate(sql, status, id);
            if (result <= 0) {
                throw new RuntimeException("Table not found");
            }
        } catch (SQLException e) {
            System.err.println("Update table status failed: " + e.getMessage());
            throw new RuntimeException("Failed to update table status", e);
        }
    }
    
    /**
     * Assign table to user 
     */
    public void assignTable(Integer tableId, Integer userId) {
        try {
            String sql = "UPDATE tables SET user_id = ?, free = 'occupied' WHERE id = ?";
            int result = DatabaseUtil.executeUpdate(sql, userId, tableId);
            if (result <= 0) {
                throw new RuntimeException("Table not found");
            }
        } catch (SQLException e) {
            System.err.println("Assign table failed: " + e.getMessage());
            throw new RuntimeException("Failed to assign table", e);
        }
    }
    
    /**
     * Release table from user 
     */
    public void releaseTable(Integer tableId) {
        try {
            String sql = "UPDATE tables SET user_id = NULL, free = 'free' WHERE id = ?";
            int result = DatabaseUtil.executeUpdate(sql, tableId);
            if (result <= 0) {
                throw new RuntimeException("Table not found");
            }
        } catch (SQLException e) {
            System.err.println("Release table failed: " + e.getMessage());
            throw new RuntimeException("Failed to release table", e);
        }
    }
} 