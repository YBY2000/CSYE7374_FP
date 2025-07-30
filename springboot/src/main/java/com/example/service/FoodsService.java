package com.example.service;

import com.example.database.DatabaseUtil;
import com.example.entity.Foods;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FoodsService {
    
    /**
     * Add new food 
     */
    public void add(Foods food) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "INSERT INTO foods (name, descr, price, img) VALUES (?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, food.getName());
            ps.setString(2, food.getDescr());
            ps.setBigDecimal(3, food.getPrice());
            ps.setString(4, food.getImg());
            
            int result = ps.executeUpdate();
            if (result <= 0) {
                throw new RuntimeException("Failed to add food");
            }
            
        } catch (SQLException e) {
            System.err.println("Add food failed: " + e.getMessage());
            throw new RuntimeException("Failed to add food", e);
        } finally {
            DatabaseUtil.closeResources(conn, ps, null);
        }
    }
    
    /**
     * Delete food by ID 
     */
    public void deleteById(Integer id) {
        try {
            String sql = "DELETE FROM foods WHERE id = ?";
            int result = DatabaseUtil.executeUpdate(sql, id);
            if (result <= 0) {
                throw new RuntimeException("Food not found");
            }
        } catch (SQLException e) {
            System.err.println("Delete food failed: " + e.getMessage());
            throw new RuntimeException("Failed to delete food", e);
        }
    }
    
    /**
     * Update food 
     */
    public void updateById(Foods food) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "UPDATE foods SET name=?, descr=?, price=?, img=? WHERE id=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, food.getName());
            ps.setString(2, food.getDescr());
            ps.setBigDecimal(3, food.getPrice());
            ps.setString(4, food.getImg());
            ps.setInt(5, food.getId());
            
            int result = ps.executeUpdate();
            if (result <= 0) {
                throw new RuntimeException("Food not found");
            }
            
        } catch (SQLException e) {
            System.err.println("Update food failed: " + e.getMessage());
            throw new RuntimeException("Failed to update food", e);
        } finally {
            DatabaseUtil.closeResources(conn, ps, null);
        }
    }
    
    /**
     * Get food by ID 
     */
    public Foods selectById(Integer id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM foods WHERE id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                Foods food = new Foods();
                food.setId(rs.getInt("id"));
                food.setName(rs.getString("name"));
                food.setDescr(rs.getString("descr"));
                food.setPrice(rs.getBigDecimal("price"));
                food.setImg(rs.getString("img"));
                return food;
            }
            
        } catch (SQLException e) {
            System.err.println("Get food by ID failed: " + e.getMessage());
        } finally {
            DatabaseUtil.closeResources(conn, ps, rs);
        }
        
        return null;
    }
    
    /**
     * Get all foods 
     */
    public List<Foods> selectAll(String name) {
        List<Foods> foods = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM foods";
            if (name != null && !name.isEmpty()) {
                sql += " WHERE name LIKE ?";
            }
            sql += " ORDER BY id DESC";
            
            ps = conn.prepareStatement(sql);
            if (name != null && !name.isEmpty()) {
                ps.setString(1, "%" + name + "%");
            }
            
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Foods food = new Foods();
                food.setId(rs.getInt("id"));
                food.setName(rs.getString("name"));
                food.setDescr(rs.getString("descr"));
                food.setPrice(rs.getBigDecimal("price"));
                food.setImg(rs.getString("img"));
                foods.add(food);
            }
            
        } catch (SQLException e) {
            System.err.println("Get all foods failed: " + e.getMessage());
        } finally {
            DatabaseUtil.closeResources(conn, ps, rs);
        }
        
        return foods;
    }
    
    /**
     * Delete multiple foods by IDs 
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            this.deleteById(id);
        }
    }
    
    /**
     * Get foods with pagination 
     */
    public com.github.pagehelper.PageInfo<Foods> selectPage(String name, Integer pageNum, Integer pageSize) {

        List<Foods> allFoods = selectAll(name);
        
        int total = allFoods.size();
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, total);
        
        List<Foods> pageData = new ArrayList<>();
        if (startIndex < total) {
            pageData = allFoods.subList(startIndex, endIndex);
        }
        
        com.github.pagehelper.PageInfo<Foods> pageInfo = new com.github.pagehelper.PageInfo<>(pageData);
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        pageInfo.setTotal(total);
        pageInfo.setPages((int) Math.ceil((double) total / pageSize));
        
        return pageInfo;
    }
} 