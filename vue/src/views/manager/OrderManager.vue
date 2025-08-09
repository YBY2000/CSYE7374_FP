<template>
  <div>
    <div class="card" style="margin-bottom: 10px;">
      <el-input prefix-icon="Search" style="width: 300px; margin-right: 10px" placeholder="Enter username to search" v-model="data.userName"></el-input>
      <el-button type="primary" @click="load">Search</el-button>
      <el-button type="info" style="margin: 0 10px" @click="reset">Reset</el-button>
    </div>

    <div class="card" style="margin-bottom: 10px">
      <el-table :data="data.tableData">
        <el-table-column prop="id" label="ID" width="70"/>
        <el-table-column prop="orderNo" label="Order Number"/>
        <el-table-column prop="content" label="Menu Content"/>
        <el-table-column prop="total" label="Total Price">
          <template #default="scope">
            <strong style="color:red;">${{ scope.row.total }}</strong>
          </template>
        </el-table-column>
        <el-table-column prop="userName" label="Username"/>
        <el-table-column prop="status" label="Order Status">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 'PREPARING'" type="primary">PREPARING</el-tag>
            <el-tag v-else-if="scope.row.status === 'PENDING'" type="warning">PENDING</el-tag>
            <el-tag v-else-if="scope.row.status === 'COMPLETED'" type="success">COMPLETED</el-tag>
            <el-tag v-else-if="scope.row.status === 'CANCELLED'" type="danger">CANCELLED</el-tag>
            <el-tag v-else type="info">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Actions" width="280">
          <template #default="scope">
            <div class="action-buttons">
              <!-- User actions -->
              <div v-if="data.user.role === 'USER'" class="user-actions">
                <el-button 
                  v-if="scope.row.status === 'PENDING'" 
                  type="danger" 
                  size="small"
                  @click="cancelOrder(scope.row)"
                >
                  <el-icon><Close /></el-icon>
                  Cancel
                </el-button>
                <el-button 
                  v-if="scope.row.status === 'PREPARING'" 
                  type="primary" 
                  size="small"
                  @click="completeOrder(scope.row)"
                >
                  <el-icon><Check /></el-icon>
                  Checkout
                </el-button>
                <el-button 
                  v-if="scope.row.status === 'COMPLETED' || scope.row.status === 'CANCELLED'" 
                  type="success" 
                  size="small"
                  @click="reorder(scope.row)"
                >
                  <el-icon><Refresh /></el-icon>
                  Reorder
                </el-button>
              </div>
              
              <!-- Admin actions -->
              <div v-if="data.user.role === 'ADMIN'" class="admin-actions">
                <div v-if="scope.row.status === 'PENDING'" class="action-group">
                  <el-button 
                    type="primary" 
                    size="small"
                    @click="confirmOrder(scope.row)"
                  >
                    <el-icon><Check /></el-icon>
                    Confirm
                  </el-button>
                  <el-button 
                    type="warning" 
                    size="small"
                    @click="cancelOrder(scope.row)"
                  >
                    <el-icon><Close /></el-icon>
                    Cancel
                  </el-button>
                </div>
                <div v-if="scope.row.status === 'PREPARING'" class="action-group">
                  <el-button 
                    type="success" 
                    size="small"
                    @click="completeOrder(scope.row)"
                  >
                    <el-icon><CircleCheck /></el-icon>
                    Complete
                  </el-button>
                  <el-button 
                    type="warning" 
                    size="small"
                    @click="cancelOrder(scope.row)"
                  >
                    <el-icon><Close /></el-icon>
                    Cancel
                  </el-button>
                </div>
                <el-button 
                  type="danger" 
                  size="small"
                  @click="del(scope.row.id)"
                  class="delete-btn"
                >
                  <el-icon><Delete /></el-icon>
                  Delete
                </el-button>
              </div>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="card" v-if="data.total">
      <el-pagination background layout="prev, pager, next" @current-change="load" :page-size="data.pageSize" v-model:current-page="data.pageNum" :total="data.total"/>
    </div>

    <!-- Reorder Confirmation Dialog -->
    <el-dialog v-model="data.reorderVisible" title="Confirm Reorder" width="40%" destroy-on-close>
      <div style="padding: 20px 0;">
        <p style="margin-bottom: 15px; font-size: 16px;">Are you sure you want to reorder the following items?</p>
        <div style="background-color: #f5f5f5; padding: 15px; border-radius: 4px; margin-bottom: 15px;">
          <p><strong>Order Content:</strong> {{ data.reorderData.content }}</p>
          <p><strong>Total Price:</strong> <span style="color: red; font-weight: bold;">${{ data.reorderData.total }}</span></p>
        </div>
        <p style="color: #666; font-size: 14px;">Note: The new order will be created with your current table information</p>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="data.reorderVisible = false">Cancel</el-button>
          <el-button type="primary" @click="confirmReorder">Confirm Order</el-button>
        </span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import {reactive, onMounted} from "vue"
import request from "@/utils/request";
import {ElMessage, ElMessageBox} from "element-plus";
import { Close, Check, Refresh, CircleCheck, Delete } from '@element-plus/icons-vue'

const data = reactive({
  user: JSON.parse(localStorage.getItem('canteen-user') || '{}'),
  tableData: [],
  total: 0,
  pageNum: 1,
  pageSize: 5,
  formVisible: false,
  form: {},
  userName: '',
  reorderVisible: false,
  reorderData: {}
})

const load = () => {
  request.get('/orders/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      userName: data.userName,
      userId: data.user.role === 'USER' ? data.user.id : null
    }
  }).then(res => {
    data.tableData = res.data?.list || []
    data.total = res.data.total
  })
}

// Cancel order
const cancelOrder = (row) => {
  ElMessageBox.confirm('Are you sure you want to cancel this order?', 'Confirm Cancel', {
    type: 'warning'
  }).then(() => {
    request.put(`/orders/${row.id}/cancel`).then(res => {
      if (res.code === '200') {
        ElMessage.success('Order cancelled successfully')
        load()
      } else {
        ElMessage.error(res.msg || 'Failed to cancel order')
      }
    }).catch(error => {
      ElMessage.error('Failed to cancel order')
      console.error('Cancel order failed:', error)
    })
  }).catch(() => {
    // User cancelled the confirmation
  })
}

// Confirm order (admin only)
const confirmOrder = (row) => {
  ElMessageBox.confirm('Are you sure you want to confirm this order for preparation?', 'Confirm Order', {
    type: 'info'
  }).then(() => {
    request.put(`/orders/${row.id}/confirm`).then(res => {
      if (res.code === '200') {
        ElMessage.success('Order confirmed successfully')
        load()
      } else {
        ElMessage.error(res.msg || 'Failed to confirm order')
      }
    }).catch(error => {
      ElMessage.error('Failed to confirm order')
      console.error('Confirm order failed:', error)
    })
  }).catch(() => {
    // User cancelled the confirmation
  })
}

// Complete order
const completeOrder = (row) => {
  ElMessageBox.confirm('Are you sure you want to complete this order?', 'Confirm Complete', {
    type: 'info'
  }).then(() => {
    request.put(`/orders/${row.id}/complete`).then(res => {
      if (res.code === '200') {
        ElMessage.success('Order completed successfully')
        load()
      } else {
        ElMessage.error(res.msg || 'Failed to complete order')
      }
    }).catch(error => {
      ElMessage.error('Failed to complete order')
      console.error('Complete order failed:', error)
    })
  }).catch(() => {
    // User cancelled the confirmation
  })
}

onMounted(() => {
  load();
});

load()

const reset = () => {
  data.userName = null
  load()
}

window.refreshOrders = load;

const reorder = (row) => {
  data.reorderData = JSON.parse(JSON.stringify(row))
  data.reorderVisible = true
}

const confirmReorder = () => {
  request.get('/tables/selectByUserId/' + data.user.id).then(res => {
    const currentTable = res.data
    if (!currentTable || !currentTable.no) {
      ElMessage.warning('Please select a table before placing an order')
      data.reorderVisible = false
      return
    }
    
    const newOrder = {
      content: data.reorderData.content,
      total: data.reorderData.total,
      userId: data.user.id,
      status: 'PENDING'
    }
    
    request.post('/orders/add', newOrder).then(res => {
      if (res.code === '200') {
        ElMessage.success('Reorder successful! New order has been created')
        data.reorderVisible = false
        load() 
      } else {
        ElMessage.error(res.msg || 'Order failed')
      }
    }).catch(error => {
      ElMessage.error('Order failed, please try again')
      console.error('Reorder failed:', error)
    })
  }).catch(error => {
    ElMessage.error('Failed to get table information')
    console.error('Get table failed:', error)
  })
}

const del = (id) => {
  ElMessageBox.confirm('This action cannot be undone. Are you sure you want to delete it?', 'Confirm Delete', { type: 'warning' }).then(res => {
    request.delete('/orders/delete/' + id).then(res => {
      if (res.code === '200') {
        ElMessage.success('Operation successful')
        load()
      } else {
        ElMessage.error(res.msg)
      }
    })
  }).catch(err => {
    console.log(err)
  })
}



</script>

<style scoped>
.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 4px 0;
}

.user-actions {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
  justify-content: flex-start;
}

.admin-actions {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.action-group {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
  align-items: center;
}

.delete-btn {
  align-self: flex-start;
  margin-top: 2px;
}

/* Ensure buttons don't wrap awkwardly */
.el-button {
  white-space: nowrap;
  min-width: 70px;
  height: 28px;
  font-size: 12px;
  border-radius: 4px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

/* Icon styling */
.el-button .el-icon {
  font-size: 14px;
  margin-right: 2px;
}

/* Button hover effects */
.el-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: all 0.2s ease;
}

/* Responsive design for smaller screens */
@media (max-width: 768px) {
  .action-buttons {
    gap: 4px;
  }
  
  .user-actions,
  .action-group {
    gap: 4px;
  }
  
  .el-button {
    min-width: 60px;
    font-size: 11px;
    padding: 4px 8px;
    height: 24px;
  }
}

/* Status-specific button styling */
.el-button--primary {
  background: linear-gradient(135deg, #409eff 0%, #337ecc 100%);
  border: none;
}

.el-button--success {
  background: linear-gradient(135deg, #67c23a 0%, #529b2e 100%);
  border: none;
}

.el-button--warning {
  background: linear-gradient(135deg, #e6a23c 0%, #cf9236 100%);
  border: none;
}

.el-button--danger {
  background: linear-gradient(135deg, #f56c6c 0%, #dd6161 100%);
  border: none;
}

.order-status {
  transition: all 0.3s ease;
}

.status-pending {
  color: #e6a23c;
  animation: pulse 2s infinite;
}

.status-preparing {
  color: #409eff;
  animation: preparing 1.5s infinite;
}

.status-completed {
  color: #67c23a;
}

.status-cancelled {
  color: #f56c6c;
}

@keyframes pulse {
  0% { opacity: 1; }
  50% { opacity: 0.6; }
  100% { opacity: 1; }
}

@keyframes preparing {
  0% { transform: scale(1); }
  50% { transform: scale(1.05); }
  100% { transform: scale(1); }
}

.order-notification {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 9999;
  background: white;
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 15px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  max-width: 300px;
  animation: slideIn 0.3s ease;
}

@keyframes slideIn {
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}
</style>