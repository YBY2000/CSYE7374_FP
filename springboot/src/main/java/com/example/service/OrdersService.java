package com.example.service;

import com.example.database.DatabaseUtil;
import com.example.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrdersService {
    
    @Autowired
    private UserNotifier userNotifier;
    
    @Autowired
    private AdminNotifier adminNotifier;
    
    @Autowired
    private DiscountService discountService;
    
    /**
     * Add new order 
     */
    public void add(Orders order) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            
            if (order.getTime() == null || order.getTime().isEmpty()) {
                order.setTime(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }
            
            if (order.getOrderNo() == null || order.getOrderNo().isEmpty()) {
                order.setOrderNo(generateOrderId());
            }
            
            // Apply pricing strategy to calculate final price
            if (order.getTotal() != null) {
                BigDecimal finalPrice = discountService.calculateOrderPrice(order.getTotal());
                order.setTotal(finalPrice);
            }
            
            String sql = "INSERT INTO orders (content, total, user_id, time, status, order_no) VALUES (?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, order.getContent());
            ps.setBigDecimal(2, order.getTotal());
            ps.setInt(3, order.getUserId());
            ps.setString(4, order.getTime());
            ps.setString(5, order.getStatus());
            ps.setString(6, order.getOrderNo());
            
            int result = ps.executeUpdate();
            if (result <= 0) {
                throw new RuntimeException("Failed to add order");
            }
            
            if (order.getStatus() != null) {
                order.updateStateFromStatus(order.getStatus());
            }
            
            order.addObserver(userNotifier);
            order.addObserver(adminNotifier);
            
            String strategyInfo = discountService.isDiscountPeriod() ? 
                " (using discount pricing strategy)" : " (using regular pricing strategy)";
            order.notifyObservers("Order created with " + discountService.getCurrentStrategyType() + 
                                " pricing strategy: " + order.getStatus() + strategyInfo);
            
        } catch (SQLException e) {
            System.err.println("Add order failed: " + e.getMessage());
            throw new RuntimeException("Failed to add order", e);
        } finally {
            DatabaseUtil.closeResources(conn, ps, null);
        }
    }
    
    /**
     * Delete order by ID 
     */
    public void deleteById(Integer id) {
        try {
            String sql = "DELETE FROM orders WHERE id = ?";
            int result = DatabaseUtil.executeUpdate(sql, id);
            if (result <= 0) {
                throw new RuntimeException("Order not found");
            }
        } catch (SQLException e) {
            System.err.println("Delete order failed: " + e.getMessage());
            throw new RuntimeException("Failed to delete order", e);
        }
    }
    
    /**
     * Update order 
     */
    public void updateById(Orders order) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "UPDATE orders SET content=?, total=?, user_id=?, time=?, status=?, order_no=? WHERE id=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, order.getContent());
            ps.setBigDecimal(2, order.getTotal());
            ps.setInt(3, order.getUserId());
            ps.setString(4, order.getTime());
            ps.setString(5, order.getStatus());
            ps.setString(6, order.getOrderNo());
            ps.setInt(7, order.getId());
            
            int result = ps.executeUpdate();
            if (result <= 0) {
                throw new RuntimeException("Order not found");
            }
            
            if (order.getStatus() != null) {
                order.updateStateFromStatus(order.getStatus());
            }
            
            order.notifyObservers("Order updated with status: " + order.getStatus());
            
        } catch (SQLException e) {
            System.err.println("Update order failed: " + e.getMessage());
            throw new RuntimeException("Failed to update order", e);
        } finally {
            DatabaseUtil.closeResources(conn, ps, null);
        }
    }
    
    /**
     * Get order by ID 
     */
    public Orders selectById(Integer id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT o.*, u.name as userName FROM orders o LEFT JOIN user u ON o.user_id = u.id WHERE o.id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                Orders order = new Orders();
                order.setId(rs.getInt("id"));
                order.setContent(rs.getString("content"));
                order.setTotal(rs.getBigDecimal("total"));
                order.setUserId(rs.getInt("user_id"));
                order.setTime(rs.getString("time"));
                order.setStatus(rs.getString("status"));
                order.setOrderNo(rs.getString("order_no"));
                order.setUserName(rs.getString("userName"));
                
                if (order.getStatus() != null) {
                    order.updateStateFromStatus(order.getStatus());
                }
                
                return order;
            }
            
        } catch (SQLException e) {
            System.err.println("Get order by ID failed: " + e.getMessage());
        } finally {
            DatabaseUtil.closeResources(conn, ps, rs);
        }
        
        return null;
    }
    
    /**
     * Get all orders 
     */
    public List<Orders> selectAll(String userName, Integer userId) {
        List<Orders> orders = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT o.*, u.name as userName FROM orders o LEFT JOIN user u ON o.user_id = u.id";
            StringBuilder whereClause = new StringBuilder();
            List<Object> params = new ArrayList<>();
            int paramIndex = 1;
            
            if (userName != null && !userName.isEmpty()) {
                whereClause.append(" u.name LIKE ?");
                params.add("%" + userName + "%");
                paramIndex++;
            }
            
            if (userId != null) {
                if (whereClause.length() > 0) {
                    whereClause.append(" AND");
                }
                whereClause.append(" o.user_id = ?");
                params.add(userId);
            }
            
            if (whereClause.length() > 0) {
                sql += " WHERE" + whereClause.toString();
            }
            sql += " ORDER BY o.id DESC";
            
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Orders order = new Orders();
                order.setId(rs.getInt("id"));
                order.setContent(rs.getString("content"));
                order.setTotal(rs.getBigDecimal("total"));
                order.setUserId(rs.getInt("user_id"));
                order.setTime(rs.getString("time"));
                order.setStatus(rs.getString("status"));
                order.setOrderNo(rs.getString("order_no"));
                order.setUserName(rs.getString("userName"));
                
                if (order.getStatus() != null) {
                    order.updateStateFromStatus(order.getStatus());
                }
                
                orders.add(order);
            }
            
        } catch (SQLException e) {
            System.err.println("Get all orders failed: " + e.getMessage());
        } finally {
            DatabaseUtil.closeResources(conn, ps, rs);
        }
        
        return orders;
    }
    
    /**
     * Delete multiple orders by IDs 
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            this.deleteById(id);
        }
    }
    
    /**
     * Get orders with pagination 
     */
    public com.github.pagehelper.PageInfo<Orders> selectPage(String userName, Integer userId, Integer pageNum, Integer pageSize) {
        
        List<Orders> allOrders = selectAll(userName, userId);
        
        int total = allOrders.size();
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, total);
        
        List<Orders> pageData = new ArrayList<>();
        if (startIndex < total) {
            pageData = allOrders.subList(startIndex, endIndex);
        }
        
        com.github.pagehelper.PageInfo<Orders> pageInfo = new com.github.pagehelper.PageInfo<>(pageData);
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        pageInfo.setTotal(total);
        pageInfo.setPages((int) Math.ceil((double) total / pageSize));
        
        return pageInfo;
    }
    
    /**
     * Get orders by user ID 
     */
    public List<Orders> selectByUserId(Integer userId) {
        List<Orders> orders = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT o.*, u.name as userName FROM orders o LEFT JOIN user u ON o.user_id = u.id WHERE o.user_id = ? ORDER BY o.id DESC";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Orders order = new Orders();
                order.setId(rs.getInt("id"));
                order.setContent(rs.getString("content"));
                order.setTotal(rs.getBigDecimal("total"));
                order.setUserId(rs.getInt("user_id"));
                order.setTime(rs.getString("time"));
                order.setStatus(rs.getString("status"));
                order.setOrderNo(rs.getString("order_no"));
                order.setUserName(rs.getString("userName"));
                
                if (order.getStatus() != null) {
                    order.updateStateFromStatus(order.getStatus());
                }
                
                orders.add(order);
            }
            
        } catch (SQLException e) {
            System.err.println("Get orders by user ID failed: " + e.getMessage());
        } finally {
            DatabaseUtil.closeResources(conn, ps, rs);
        }
        
        return orders;
    }
    
    /**
     * Update order status 
     */
    public void updateStatus(Integer id, String status) {
        try {
            String sql = "UPDATE orders SET status = ? WHERE id = ?";
            int result = DatabaseUtil.executeUpdate(sql, status, id);
            if (result <= 0) {
                throw new RuntimeException("Order not found");
            }
            
            Orders order = selectById(id);
            if (order != null) {
                order.updateStateFromStatus(status);
                order.notifyObservers("Order status updated to: " + status);
            }
            
        } catch (SQLException e) {
            System.err.println("Update order status failed: " + e.getMessage());
            throw new RuntimeException("Failed to update order status", e);
        }
    }

    public void processOrderWithState(Integer orderId, String newState) {
        Orders order = selectById(orderId);
        if (order != null) {
            order.changeState(newState);
            updateById(order);
        }
    }

    public void addObserverToOrder(Integer orderId, OrderObserver observer) {
        Orders order = selectById(orderId);
        if (order != null) {
            order.addObserver(observer);
            updateById(order);
        }
    }

    public Orders cloneOrder(Integer orderId) {
        Orders originalOrder = selectById(orderId);
        if (originalOrder != null) {
            Orders clonedOrder = originalOrder.clone();
            clonedOrder.setOrderNo(generateOrderId());
            add(clonedOrder);
            return clonedOrder;
        }
        return null;
    }

    private String generateOrderId() {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }
} 