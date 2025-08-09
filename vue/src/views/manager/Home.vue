<template>
  <div>

    <div class="card" style="line-height: 30px; margin-bottom: 10px">
      <div>Welcome，<span style="color: #1450aa">{{ user.name }}</span> Enjoy your Day！</div>
    </div>

    <!-- Show alert if user already has a table -->
    <div v-if="data.userTable" class="card" style="margin-bottom: 10px; background: #f0f9ff; border: 1px solid #409eff;">
      <el-alert 
        title="You currently have a table assigned" 
        :description="`Table: ${data.userTable.no}, Seats: ${data.userTable.unit}`"
        type="info" 
        show-icon
        style="margin-bottom: 10px">
      </el-alert>
      <div style="text-align: center;">
        <el-button type="primary" @click="router.push('/order')" style="margin-right: 10px">
          Continue Ordering
        </el-button>
        <el-button @click="releaseCurrentTable" :loading="data.releaseLoading">
          Release Table
        </el-button>
      </div>
    </div>

    <div class="card">
      <div style="display: flex; flex-wrap: wrap">
        <div v-for="item in data.tables" :key="item.id" style="text-align: center; margin-right: 20px; margin-bottom: 20px">
          <div><img src="@/assets/imgs/餐饮.png" alt="" style="width: 100px"></div>
          <div>{{item.no }}</div>
          <div style="font-size: 12px; margin: 10px 0">{{item.unit }} available seats</div>
          <div style="margin: 10px 0">
            <span style="color: #04c46d" v-if="item.free === 'Yes'">Available</span>
            <span style="color: #b20130" v-else>Occupied</span>
          </div>
          <div v-if="item.free === 'Yes'">
            <el-button 
              type="success" 
              @click="addOrder(item)"
              :disabled="data.userTable || data.loading"
              :loading="data.loading && data.selectedTableId === item.id">
              Start Ordering
            </el-button>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import {reactive, onMounted, onActivated} from "vue";
import request from "@/utils/request";
import router from "@/router";
import {ElMessage} from "element-plus";

const user = JSON.parse(localStorage.getItem('canteen-user') || '{}')

const data = reactive({
  tables: [],
  userTable: null,
  loading: false,
  releaseLoading: false,
  selectedTableId: null
})

const loadTables = () => {
  request.get('/tables/selectAll').then(res => {
    data.tables = res.data || []
  })
}

const checkUserTable = () => {
  request.get('/tables/selectByUserId/' + user.id).then(res => {
    data.userTable = res.data || null
  }).catch(err => {
    console.log('No table found for user:', err)
    data.userTable = null
  })
}

const loadData = () => {
  loadTables()
  checkUserTable()
}

onMounted(() => {
  loadData()
})

onActivated(() => {
  loadData()
})

const addOrder = async (item) => {
  // Prevent multiple clicks
  if (data.loading) return;
  
  data.loading = true;
  data.selectedTableId = item.id;
  
  try {
    // First check if user already has a table
    const userTableRes = await request.get('/tables/selectByUserId/' + user.id);
    if (userTableRes.data && userTableRes.data.id) {
      ElMessage.warning('You already have a table assigned: ' + userTableRes.data.no);
      data.userTable = userTableRes.data;
      return;
    }
    
    // Assign the table
    item.userId = user.id;
    const res = await request.put('/tables/addOrder', item);
    if (res.code === '200') {
      ElMessage.success('Table assigned successfully!');
      router.push('/order');
    } else {
      ElMessage.error(res.msg || 'Failed to assign table');
    }
  } catch (error) {
    console.error('Error assigning table:', error);
    ElMessage.error('Operation failed, please try again');
  } finally {
    data.loading = false;
    data.selectedTableId = null;
    loadData(); // Refresh table status
  }
}

const releaseCurrentTable = async () => {
  if (!data.userTable) return;
  
  data.releaseLoading = true;
  
  try {
    const res = await request.put('/tables/removeOrder', data.userTable);
    if (res.code === '200') {
      ElMessage.success('Table released successfully!');
      data.userTable = null;
      loadData(); // Refresh data
    } else {
      ElMessage.error(res.msg || 'Failed to release table');
    }
  } catch (error) {
    console.error('Error releasing table:', error);
    ElMessage.error('Operation failed, please try again');
  } finally {
    data.releaseLoading = false;
  }
}

</script>