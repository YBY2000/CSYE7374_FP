<template>
  <div>
    <div class="card" style="margin-bottom: 5px">
      <div v-if="data.table.no">
        <div style="display: flex; align-items: center">
          <div style="flex: 1">
            <span style="margin-right: 10px">Table No:{{ data.table.no }}  Start Ordering</span>
            <el-button type="primary" plain @click="showOrderList">Ordered Items({{ data.total }})</el-button>
          </div>
          <el-button type="primary" @click="removeOrder">Leave Table</el-button>
        </div>
      </div>
      <div style="color:#666;" v-else>
        You haven’t selected a table yet, please <a href="/home">select a table</a> before ordering
      </div>
    </div>

    <!-- Display Items-->
    <el-row :gutter="10">
      <el-col :span="6" v-for="item in data.foodsList" :key="item.id">
        <div class="card">
          <img :src="item.img" alt="" style="width: 100%; height: 280px">
          <div style="margin: 5px; color: #000; font-size: 18px; display: flex; align-items: center">
            <div style="flex: 1">{{ item.name }}</div>
            <div style="color: red; font-weight: bold">￥{{ item.price }}</div>
          </div>
          <div style="margin: 5px; color: #666">
            <el-tooltip :content="item.descr" placement="bottom" effect="customized" v-if="item.descr.length >= 20">
              <div class="line1">{{ item.descr }}</div>
            </el-tooltip>
            <div v-else>{{ item.descr }}</div>
          </div>
          <div style="margin: 10px 0; text-align: right">
            <el-input-number :min="1" v-model="item.num" style="margin-right: 5px"></el-input-number>
            <el-button type="primary" @click="addOrder(item)">Order</el-button>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-dialog v-model="data.dialogShow" title="Ordered Items" width="800">
      <el-table :data="data.orderList">
        <el-table-column label="Image">
          <template #default="scope">
            <el-image style="width: 50px; height: 50px" :src="scope.row.img" :preview-src-list="[scope.row.img]" preview-teleported></el-image>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="Name" />
        <el-table-column prop="price" label="Price" />
        <el-table-column prop="num" label="Num of item" />
      </el-table>
      <div style="text-align: right; color: red; font-weight: bold; font-size: 20px; margin-top: 10px">Total Price：${{ data.orderTotal }}</div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="data.dialogShow = false">Close</el-button>
          <el-button type="primary" @click="save">Place Order</el-button>
        </div>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import {reactive} from "vue";
import request from "@/utils/request";
import {ElMessage} from "element-plus";

const data = reactive({
  table: {},
  user: JSON.parse(localStorage.getItem('canteen-user') || '{}'),
  foodsList: [],
  dialogShow: false,
  orderList: [],
  total: 0,
  orderTotal: 0
})

const loadTable = () => {
  request.get('/tables/selectByUserId/' + data.user.id).then(res => {
    data.table = res.data || {}
  })
}
loadTable()

const removeOrder = () => {
  request.put('/tables/removeOrder', data.table).then(res => {
    if (res.code === '200') {
      ElMessage.success('Table released successfully')
      loadTable()
    } else {
      ElMessage.error(res.msg)
    }
  })
}

const loadFoods = () => {
  request.get('/foods/selectAll').then(res => {
    data.foodsList = res.data || []
    data.foodsList.forEach(item => item.num = 1)
  })
}
loadFoods()

const showOrderList = () => {
  data.dialogShow = true
}

// 点餐的逻辑
const addOrder = (foods) => {
  let f = data.orderList.find(item => item.id === foods.id)  // Find the dish that’s already in the order list
  if (f) {  //  If exists, update its quantity (num of items)
    f.num += foods.num
  } else {  // If not, add to the list
    let foods1 = JSON.parse(JSON.stringify(foods))
    data.orderList.push(foods1)
  }
  // Sum up the num field
  data.total = data.orderList.map(item => item.num).reduce((acc, cur) => acc + cur, 0)
  //  orderTotal is the total amount of the order
  data.orderTotal = 0
  data.orderList.forEach(item => {
    // Sum: unit price * quantity
    data.orderTotal += item.price * item.num
  })
  data.orderTotal.toFixed()
  ElMessage.success('Item added to order')
}

// order logic
const save = () => {
  if (data.orderList.length === 0) {
    ElMessage.warning('Please select at least one item')
    return
  }
  let content = ''
  data.orderList.forEach(item => {
    content += item.name + 'x' + item.num + '，'
  })
  content = content.substring(0 , content.length - 1)  //  Remove the last comma at the end of the item string
  let orderData = {  content:  content, total: data.orderTotal, userId: data.user.id, status: 'PENDING'}
  request.post('/orders/add', orderData).then(res => {
    if (res.code === '200') {  // Order success
      ElMessage.success('Order placed successfully. You can check the status in My Orders')
      data.dialogShow = false
    } else {
      ElMessage.error(res.msg)
    }
  })
}
</script>

<style>
.el-popper.is-customized {
  /* Set padding to ensure the height is 32px */
  padding: 6px 12px;
  background: linear-gradient(90deg, rgb(159, 229, 151), rgb(204, 229, 129));
}

.el-popper.is-customized .el-popper__arrow::before {
  background: linear-gradient(45deg, #b2e68d, #bce689);
  right: 0;
}
</style>