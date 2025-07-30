package com.example.service;

import com.example.database.DatabaseUtil;
import com.example.entity.Admin;
import com.example.entity.Account;
import com.example.exception.CustomException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminService {
    
    /**
     * Admin login 
     */
    public Account login(Account account) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM admin WHERE username = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, account.getUsername());
            rs = ps.executeQuery();
            
            if (rs.next()) {
                Admin dbAdmin = new Admin();
                dbAdmin.setId(rs.getInt("id"));
                dbAdmin.setUsername(rs.getString("username"));
                dbAdmin.setPassword(rs.getString("password"));
                dbAdmin.setName(rs.getString("name"));
                dbAdmin.setAvatar(rs.getString("avatar"));
                dbAdmin.setRole(rs.getString("role"));
                
                // Validate password
                if (!dbAdmin.getPassword().equals(account.getPassword())) {
                    throw new CustomException("Incorrect username or password");
                }
                return dbAdmin;
            } else {
                throw new CustomException("Account does not exist");
            }
            
        } catch (SQLException e) {
            System.err.println("Admin login failed: " + e.getMessage());
            throw new CustomException("Login failed");
        } finally {
            DatabaseUtil.closeResources(conn, ps, rs);
        }
    }
    
    /**
     * Add new admin 
     */
    public void add(Admin admin) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            // Check if admin already exists
            conn = DatabaseUtil.getConnection();
            String checkSql = "SELECT * FROM admin WHERE username = ?";
            ps = conn.prepareStatement(checkSql);
            ps.setString(1, admin.getUsername());
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                throw new CustomException("Account already exists");
            }
            
            // Set default values
            if (admin.getPassword() == null || admin.getPassword().isEmpty()) {
                admin.setPassword("admin");
            }
            if (admin.getName() == null || admin.getName().isEmpty()) {
                admin.setName(admin.getUsername());
            }
            if (admin.getRole() == null || admin.getRole().isEmpty()) {
                admin.setRole("ADMIN");
            }
            
            // Insert new admin
            String insertSql = "INSERT INTO admin (username, password, name, avatar, role) VALUES (?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(insertSql);
            ps.setString(1, admin.getUsername());
            ps.setString(2, admin.getPassword());
            ps.setString(3, admin.getName());
            ps.setString(4, admin.getAvatar());
            ps.setString(5, admin.getRole());
            
            int result = ps.executeUpdate();
            if (result <= 0) {
                throw new CustomException("Failed to add admin");
            }
            
        } catch (SQLException e) {
            System.err.println("Add admin failed: " + e.getMessage());
            throw new CustomException("Failed to add admin");
        } finally {
            DatabaseUtil.closeResources(conn, ps, null);
        }
    }
    
    /**
     * Delete admin by ID 
     */
    public void deleteById(Integer id) {
        try {
            String sql = "DELETE FROM admin WHERE id = ?";
            int result = DatabaseUtil.executeUpdate(sql, id);
            if (result <= 0) {
                throw new CustomException("Admin not found");
            }
        } catch (SQLException e) {
            System.err.println("Delete admin failed: " + e.getMessage());
            throw new CustomException("Failed to delete admin");
        }
    }
    
    /**
     * Delete multiple admins by IDs
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            this.deleteById(id);
        }
    }
    
    /**
     * Update admin 
     */
    public void updateById(Admin admin) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "UPDATE admin SET username=?, password=?, name=?, avatar=?, role=? WHERE id=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, admin.getUsername());
            ps.setString(2, admin.getPassword());
            ps.setString(3, admin.getName());
            ps.setString(4, admin.getAvatar());
            ps.setString(5, admin.getRole());
            ps.setInt(6, admin.getId());
            
            int result = ps.executeUpdate();
            if (result <= 0) {
                throw new CustomException("Admin not found");
            }
            
        } catch (SQLException e) {
            System.err.println("Update admin failed: " + e.getMessage());
            throw new CustomException("Failed to update admin");
        } finally {
            DatabaseUtil.closeResources(conn, ps, null);
        }
    }
    
    /**
     * Get admin by ID 
     */
    public Admin selectById(Integer id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM admin WHERE id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                Admin admin = new Admin();
                admin.setId(rs.getInt("id"));
                admin.setUsername(rs.getString("username"));
                admin.setPassword(rs.getString("password"));
                admin.setName(rs.getString("name"));
                admin.setAvatar(rs.getString("avatar"));
                admin.setRole(rs.getString("role"));
                return admin;
            }
            
        } catch (SQLException e) {
            System.err.println("Get admin by ID failed: " + e.getMessage());
        } finally {
            DatabaseUtil.closeResources(conn, ps, rs);
        }
        
        return null;
    }
    
    /**
     * Get all admins 
     */
    public List<Admin> selectAll(String name) {
        List<Admin> admins = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM admin";
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
                Admin admin = new Admin();
                admin.setId(rs.getInt("id"));
                admin.setUsername(rs.getString("username"));
                admin.setPassword(rs.getString("password"));
                admin.setName(rs.getString("name"));
                admin.setAvatar(rs.getString("avatar"));
                admin.setRole(rs.getString("role"));
                admins.add(admin);
            }
            
        } catch (SQLException e) {
            System.err.println("Get all admins failed: " + e.getMessage());
        } finally {
            DatabaseUtil.closeResources(conn, ps, rs);
        }
        
        return admins;
    }
    
    /**
     * Get admins with pagination 
     */
    public com.github.pagehelper.PageInfo<Admin> selectPage(String name, Integer pageNum, Integer pageSize) {
        
        List<Admin> allAdmins = selectAll(name);
        
        int total = allAdmins.size();
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, total);
        
        List<Admin> pageData = new ArrayList<>();
        if (startIndex < total) {
            pageData = allAdmins.subList(startIndex, endIndex);
        }
        
        com.github.pagehelper.PageInfo<Admin> pageInfo = new com.github.pagehelper.PageInfo<>(pageData);
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        pageInfo.setTotal(total);
        pageInfo.setPages((int) Math.ceil((double) total / pageSize));
        
        return pageInfo;
    }
} 