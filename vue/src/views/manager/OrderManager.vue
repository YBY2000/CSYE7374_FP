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
            <strong style="color:red;">￥{{ scope.row.total }}</strong>
          </template>
        </el-table-column>
        <el-table-column prop="userName" label="Username"/>
        <el-table-column prop="status" label="Order Status">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 'PREPARING'" type="primary">{{ scope.row.status }}</el-tag>
            <el-tag v-if="scope.row.status === 'PENDING'" type="warning">{{ scope.row.status }}</el-tag>
            <el-tag v-if="scope.row.status === 'COMPLETED'" type="success">{{ scope.row.status }}</el-tag>
            <span v-else>{{ scope.row.status }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" >
          <template #default="scope">
            <el-button v-if="data.user.role === 'USER' && scope.row.status === 'PENDING'" type="primary" @click="done(scope.row)">Checkout</el-button>
            <el-button v-if="data.user.role === 'ADMIN' && scope.row.status === 'PREPARING'" type="primary" @click="handleEdit(scope.row)">Edit</el-button>
            <el-button v-if="data.user.role === 'ADMIN'" type="danger" @click="del(scope.row.id)">Delete</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="card" v-if="data.total">
      <el-pagination background layout="prev, pager, next" @current-change="load" :page-size="data.pageSize" v-model:current-page="data.pageNum" :total="data.total"/>
    </div>

    <el-dialog v-model="data.formVisible" title="Information" width="30%" destroy-on-close>
      <el-form :model="data.form" label-width="100px" style="padding-right: 50px">
        <el-form-item label="Order Status">
          <el-select style="width: 100%" v-model="data.form.status">
            <el-option value="PREPARING"></el-option>
            <el-option value="PENDING"></el-option>
<!--            <el-option value="COMPLETED"></el-option>-->
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
      <span class="dialog-footer">
        <el-button @click="data.formVisible = false">Cancel</el-button>
        <el-button type="primary" @click="save">Save</el-button>
      </span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import {reactive, onMounted, onUnmounted} from "vue"
import request from "@/utils/request";
import {ElMessage, ElMessageBox} from "element-plus";
import websocketService from "@/utils/websocket";

const data = reactive({
  user: JSON.parse(localStorage.getItem('canteen-user') || '{}'),
  tableData: [],
  total: 0,
  pageNum: 1,  // Current page number
  pageSize: 5,  // Items per page
  formVisible: false,
  form: {},
  userName: '',
})

const done = (row) => {
  let form = JSON.parse(JSON.stringify(row))
  form.status = 'COMPLETED'
  request.put('/orders/update', form).then(res => {
    if (res.code === '200') {  // Success
      ElMessage.success('Operation successful')
      load()  // Reload table data
    } else {
      ElMessage.error(res.msg)
    }
  })
}

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

load()

// WebSocket连接
onMounted(() => {
  const user = JSON.parse(localStorage.getItem('canteen-user') || '{}');
  if (user.id) {
    websocketService.connect(user.id.toString());
  }
});

onUnmounted(() => {
  websocketService.disconnect();
});

// 暴露刷新方法给WebSocket服务
window.refreshOrders = load;

const reset = () => {
  data.userName = null
  load()
}

// 保存数据
const save = () => {
  request.request({
    method: data.form.id ? 'PUT' : 'POST',
    url: data.form.id ? '/orders/update' : '/orders/add',
    data: data.form
  }).then(res => {
    if (res.code === '200') {  // Success
      ElMessage.success('Operation successful')
      data.formVisible = false // Close dialog
      load()  // Reload table data
    } else {
      ElMessage.error(res.msg)
    }
  })
}

const handleEdit = (row) => {
  data.form = JSON.parse(JSON.stringify(row))
  data.formVisible = true
}

const del = (id) => {
  ElMessageBox.confirm('This action cannot be undone. Are you sure you want to delete it?', 'Confirm Delete', { type: 'warning' }).then(res => {
    request.delete('/orders/delete/' + id).then(res => {
      if (res.code === '200') {  // Success
        ElMessage.success('Operation successful')
        load()  // Reload table data
      } else {
        ElMessage.error(res.msg)
      }
    })
  }).catch(err => {
    console.log(err)
  })
}

</script>