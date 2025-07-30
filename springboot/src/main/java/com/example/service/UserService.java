package com.example.service;

import com.example.database.DatabaseUtil;
import com.example.entity.User;
import com.example.entity.Account;
import com.example.exception.CustomException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserService {
    
    public Account login(Account account) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM user WHERE username = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, account.getUsername());
            rs = ps.executeQuery();
            
            if (rs.next()) {
                User dbUser = new User();
                dbUser.setId(rs.getInt("id"));
                dbUser.setUsername(rs.getString("username"));
                dbUser.setPassword(rs.getString("password"));
                dbUser.setName(rs.getString("name"));
                dbUser.setAvatar(rs.getString("avatar"));
                dbUser.setRole(rs.getString("role"));
                
                if (!dbUser.getPassword().equals(account.getPassword())) {
                    throw new CustomException("Incorrect username or password");
                }
                return dbUser;
            } else {
                throw new CustomException("Account does not exist");
            }
            
        } catch (SQLException e) {
            System.err.println("User login failed: " + e.getMessage());
            throw new CustomException("Login failed");
        } finally {
            DatabaseUtil.closeResources(conn, ps, rs);
        }
    }
    
    public void register(User user) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String checkSql = "SELECT * FROM user WHERE username = ?";
            ps = conn.prepareStatement(checkSql);
            ps.setString(1, user.getUsername());
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                throw new CustomException("Account already exists");
            }
            
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                throw new CustomException("Password cannot be empty");
            }
            
            if (user.getName() == null || user.getName().isEmpty()) {
                user.setName(user.getUsername());
            }
            if (user.getRole() == null || user.getRole().isEmpty()) {
                user.setRole("USER");
            }
            
            String insertSql = "INSERT INTO user (username, password, name, avatar, role) VALUES (?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(insertSql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getName());
            ps.setString(4, user.getAvatar());
            ps.setString(5, user.getRole());
            
            int result = ps.executeUpdate();
            if (result <= 0) {
                throw new CustomException("Failed to register user");
            }
            
        } catch (SQLException e) {
            System.err.println("Register user failed: " + e.getMessage());
            throw new CustomException("Failed to register user");
        } finally {
            DatabaseUtil.closeResources(conn, ps, null);
        }
    }
    
    public void add(User user) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String checkSql = "SELECT * FROM user WHERE username = ?";
            ps = conn.prepareStatement(checkSql);
            ps.setString(1, user.getUsername());
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                throw new CustomException("Account already exists");
            }
            
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                user.setPassword("123");
            }
            if (user.getName() == null || user.getName().isEmpty()) {
                user.setName(user.getUsername());
            }
            if (user.getRole() == null || user.getRole().isEmpty()) {
                user.setRole("USER");
            }
            
            String insertSql = "INSERT INTO user (username, password, name, avatar, role) VALUES (?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(insertSql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getName());
            ps.setString(4, user.getAvatar());
            ps.setString(5, user.getRole());
            
            int result = ps.executeUpdate();
            if (result <= 0) {
                throw new CustomException("Failed to add user");
            }
            
        } catch (SQLException e) {
            System.err.println("Add user failed: " + e.getMessage());
            throw new CustomException("Failed to add user");
        } finally {
            DatabaseUtil.closeResources(conn, ps, null);
        }
    }
    
    public void deleteById(Integer id) {
        try {
            String sql = "DELETE FROM user WHERE id = ?";
            int result = DatabaseUtil.executeUpdate(sql, id);
            if (result <= 0) {
                throw new CustomException("User not found");
            }
        } catch (SQLException e) {
            System.err.println("Delete user failed: " + e.getMessage());
            throw new CustomException("Failed to delete user");
        }
    }
    
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            this.deleteById(id);
        }
    }
    
    public void updateById(User user) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "UPDATE user SET username=?, password=?, name=?, avatar=?, role=? WHERE id=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getName());
            ps.setString(4, user.getAvatar());
            ps.setString(5, user.getRole());
            ps.setInt(6, user.getId());
            
            int result = ps.executeUpdate();
            if (result <= 0) {
                throw new CustomException("User not found");
            }
            
        } catch (SQLException e) {
            System.err.println("Update user failed: " + e.getMessage());
            throw new CustomException("Failed to update user");
        } finally {
            DatabaseUtil.closeResources(conn, ps, null);
        }
    }
    
    public User selectById(Integer id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM user WHERE id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setName(rs.getString("name"));
                user.setAvatar(rs.getString("avatar"));
                user.setRole(rs.getString("role"));
                return user;
            }
            
        } catch (SQLException e) {
            System.err.println("Get user by ID failed: " + e.getMessage());
        } finally {
            DatabaseUtil.closeResources(conn, ps, rs);
        }
        
        return null;
    }
    
    public List<User> selectAll(String name) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<User> list = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM user";
            if (name != null && !name.isEmpty()) {
                sql += " WHERE name LIKE ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, "%" + name + "%");
            } else {
                ps = conn.prepareStatement(sql);
            }
            rs = ps.executeQuery();
            
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setName(rs.getString("name"));
                user.setAvatar(rs.getString("avatar"));
                user.setRole(rs.getString("role"));
                list.add(user);
            }
            
        } catch (SQLException e) {
            System.err.println("Get all users failed: " + e.getMessage());
        } finally {
            DatabaseUtil.closeResources(conn, ps, rs);
        }
        
        return list;
    }
    
    public com.github.pagehelper.PageInfo<User> selectPage(String name, Integer pageNum, Integer pageSize) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<User> list = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM user";
            if (name != null && !name.isEmpty()) {
                sql += " WHERE name LIKE ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, "%" + name + "%");
            } else {
                ps = conn.prepareStatement(sql);
            }
            rs = ps.executeQuery();
            
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setName(rs.getString("name"));
                user.setAvatar(rs.getString("avatar"));
                user.setRole(rs.getString("role"));
                list.add(user);
            }
            
        } catch (SQLException e) {
            System.err.println("Get users by page failed: " + e.getMessage());
        } finally {
            DatabaseUtil.closeResources(conn, ps, rs);
        }
        
        return new com.github.pagehelper.PageInfo<>(list);
    }
}
