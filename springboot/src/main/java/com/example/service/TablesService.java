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
            
            if ("Yes".equals(table.getFree())) {
                table.setState(new TableFreeState());
            } else {
                table.setState(new TableOccupiedState());
            }
            
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
                table.setUserId(rs.getInt("user_id"));
                table.setUserName(rs.getString("userName"));
                
                String freeStatus = rs.getString("free");
                if ("Yes".equals(freeStatus)) {
                    table.setState(new TableFreeState());
                } else {
                    table.setState(new TableOccupiedState());
                }
                
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
                table.setUserId(rs.getInt("user_id"));
                table.setUserName(rs.getString("userName"));
                
                String freeStatus = rs.getString("free");
                if ("Yes".equals(freeStatus)) {
                    table.setState(new TableFreeState());
                } else {
                    table.setState(new TableOccupiedState());
                }
                
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
                table.setUserId(rs.getInt("user_id"));
                table.setUserName(rs.getString("userName"));
                
                String freeStatus = rs.getString("free");
                if ("Yes".equals(freeStatus)) {
                    table.setState(new TableFreeState());
                } else {
                    table.setState(new TableOccupiedState());
                }
                
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
            Tables existingTable = selectByUserId(table.getUserId());
            if (existingTable != null) {
                throw new RuntimeException("User already has a table assigned: " + existingTable.getNo());
            }
            
            Tables targetTable = selectById(table.getId());
            if (targetTable == null) {
                throw new RuntimeException("Table not found");
            }
            
            targetTable.occupyTable(table.getUserId());
            
            String sql = "UPDATE tables SET user_id = ?, free = ? WHERE id = ?";
            int result = DatabaseUtil.executeUpdate(sql, 
                targetTable.getUserId(), 
                targetTable.getFree(), 
                targetTable.getId());
                
            if (result <= 0) {
                throw new RuntimeException("Table assignment failed");
            }
            
        } catch (SQLException e) {
            System.err.println("Add order to table failed: " + e.getMessage());
            throw new RuntimeException("Failed to add order to table", e);
        }
    }
    
    public void removeOrder(Tables table) {
        try {
            Tables currentTable = selectById(table.getId());
            if (currentTable == null) {
                throw new RuntimeException("Table not found");
            }
            
            currentTable.releaseTable();
            
            String sql = "UPDATE tables SET user_id = ?, free = ? WHERE id = ?";
            int result = DatabaseUtil.executeUpdate(sql, 
                currentTable.getUserId(), 
                currentTable.getFree(), 
                currentTable.getId());
                
            if (result <= 0) {
                throw new RuntimeException("Table update failed");
            }
            
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

} 