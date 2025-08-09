package com.example.service;

import com.example.database.DatabaseUtil;
import com.example.entity.Tables;
import com.example.entity.TableFreeState;
import com.example.entity.TableOccupiedState;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TablesService {
    
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
            
            updateTableState(table);
            
        } catch (SQLException e) {
            System.err.println("Update table failed: " + e.getMessage());
            throw new RuntimeException("Failed to update table", e);
        } finally {
            DatabaseUtil.closeResources(conn, ps, null);
        }
    }
    
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
                
                updateTableStateFromDatabase(table);
                return table;
            }
            
        } catch (SQLException e) {
            System.err.println("Get table by ID failed: " + e.getMessage());
        } finally {
            DatabaseUtil.closeResources(conn, ps, rs);
        }
        
        return null;
    }
    
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
                
                updateTableStateFromDatabase(table);
                tables.add(table);
            }
            
        } catch (SQLException e) {
            System.err.println("Get all tables failed: " + e.getMessage());
        } finally {
            DatabaseUtil.closeResources(conn, ps, rs);
        }
        
        return tables;
    }
    
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
                
                updateTableStateFromDatabase(table);
                return table;
            }
            
        } catch (SQLException e) {
            System.err.println("Get table by user ID failed: " + e.getMessage());
        } finally {
            DatabaseUtil.closeResources(conn, ps, rs);
        }
        
        return null;
    }
    
    public void addOrder(Tables table) {
        try {
            // First check if user already has a table assigned
            Tables existingTable = selectByUserId(table.getUserId());
            if (existingTable != null) {
                throw new RuntimeException("User already has a table assigned: " + existingTable.getNo());
            }
            
            // Check if the target table is actually available
            Tables targetTable = selectById(table.getId());
            if (targetTable == null || !"Yes".equals(targetTable.getFree())) {
                throw new RuntimeException("Table is not available");
            }
            
            // Use conditional update to prevent race conditions
            String sql = "UPDATE tables SET user_id = ?, free = 'No' WHERE id = ? AND free = 'Yes' AND user_id IS NULL";
            int result = DatabaseUtil.executeUpdate(sql, table.getUserId(), table.getId());
            if (result <= 0) {
                throw new RuntimeException("Table assignment failed - table may have been taken by another user");
            }
            
            table.setFree("No");
            updateTableState(table);
            
        } catch (SQLException e) {
            System.err.println("Add order to table failed: " + e.getMessage());
            throw new RuntimeException("Failed to add order to table", e);
        }
    }
    
    public void removeOrder(Tables table) {
        try {
            String sql = "UPDATE tables SET user_id = NULL, free = 'Yes' WHERE id = ?";
            int result = DatabaseUtil.executeUpdate(sql, table.getId());
            if (result <= 0) {
                throw new RuntimeException("Table not found");
            }
            
            table.setFree("Yes");
            table.setUserId(null);
            updateTableState(table);
            
        } catch (SQLException e) {
            System.err.println("Remove order from table failed: " + e.getMessage());
            throw new RuntimeException("Failed to remove order from table", e);
        }
    }
    
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            this.deleteById(id);
        }
    }
    
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
    
    public void updateStatus(Integer id, String status) {
        try {
            String sql = "UPDATE tables SET free = ? WHERE id = ?";
            int result = DatabaseUtil.executeUpdate(sql, status, id);
            if (result <= 0) {
                throw new RuntimeException("Table not found");
            }
            
            Tables table = selectById(id);
            if (table != null) {
                table.setFree(status);
                updateTableState(table);
            }
            
        } catch (SQLException e) {
            System.err.println("Update table status failed: " + e.getMessage());
            throw new RuntimeException("Failed to update table status", e);
        }
    }
    
    public void assignTable(Integer tableId, Integer userId) {
        try {
            String sql = "UPDATE tables SET user_id = ?, free = 'No' WHERE id = ?";
            int result = DatabaseUtil.executeUpdate(sql, userId, tableId);
            if (result <= 0) {
                throw new RuntimeException("Table not found");
            }
            
            Tables table = selectById(tableId);
            if (table != null) {
                table.setUserId(userId);
                table.setFree("No");
                updateTableState(table);
            }
            
        } catch (SQLException e) {
            System.err.println("Assign table failed: " + e.getMessage());
            throw new RuntimeException("Failed to assign table", e);
        }
    }
    
    public void releaseTable(Integer tableId) {
        try {
            String sql = "UPDATE tables SET user_id = NULL, free = 'Yes' WHERE id = ?";
            int result = DatabaseUtil.executeUpdate(sql, tableId);
            if (result <= 0) {
                throw new RuntimeException("Table not found");
            }
            
            Tables table = selectById(tableId);
            if (table != null) {
                table.setUserId(null);
                table.setFree("Yes");
                updateTableState(table);
            }
            
        } catch (SQLException e) {
            System.err.println("Release table failed: " + e.getMessage());
            throw new RuntimeException("Failed to release table", e);
        }
    }
    
    public void reserveTable(Integer tableId) {
        try {
            String sql = "UPDATE tables SET free = 'No' WHERE id = ?";
            int result = DatabaseUtil.executeUpdate(sql, tableId);
            if (result <= 0) {
                throw new RuntimeException("Table not found");
            }
            
            Tables table = selectById(tableId);
            if (table != null) {
                table.setFree("No");
                updateTableState(table);
            }
            
        } catch (SQLException e) {
            System.err.println("Reserve table failed: " + e.getMessage());
            throw new RuntimeException("Failed to reserve table", e);
        }
    }
    
    private void updateTableState(Tables table) {
        String status = table.getFree();
        if ("Yes".equals(status)) {
            table.setState(new TableFreeState());
        } else if ("No".equals(status)) {
            table.setState(new TableOccupiedState());
        }
    }
    
    private void updateTableStateFromDatabase(Tables table) {
        String status = table.getFree();
        if ("Yes".equals(status)) {
            table.setState(new TableFreeState());
        } else if ("No".equals(status)) {
            table.setState(new TableOccupiedState());
        }
    }
} 