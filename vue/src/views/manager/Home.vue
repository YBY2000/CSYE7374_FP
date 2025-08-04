<template>
  <div>

    <div class="card" style="line-height: 30px; margin-bottom: 10px">
      <div>Welcome，<span style="color: #1450aa">{{ user.name }}</span> Enjoy your Day！</div>
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
            <el-button type="success" @click="addOrder(item)">Start Ordering</el-button>
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
  tables: []
})

const loadTables = () => {
  request.get('/tables/selectAll').then(res => {
    data.tables = res.data || []
  })
}

onMounted(() => {
  loadTables()
})

onActivated(() => {
  loadTables()
})

const addOrder = (item) => {
  item.userId = user.id
  request.put('/tables/addOrder', item).then(res => {
    if (res.code === '200') {
      router.push('/order')
    } else {
      ElMessage.error(res.msg)
    }
  })
}

</script>