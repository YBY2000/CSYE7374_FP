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
            <el-button type="primary" @click="openDecoratorDialog(item)">Order</el-button>
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
        <el-table-column label="Name">
          <template #default="scope">
            <span>{{ scope.row.name }}</span>
            <span v-if="Array.isArray(scope.row.decorators) && scope.row.decorators.length" style="color:#666"> ({{ formatDecorators(scope.row.decorators) }})</span>
          </template>
        </el-table-column>
        <el-table-column label="Price">
          <template #default="scope">
            {{ decoratedUnitPrice(scope.row) }}
          </template>
        </el-table-column>
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

    <!-- Decorator selection dialog -->
    <el-dialog v-model="data.decoratorDialog.visible" title="Select options" width="400">
      <div style="margin-bottom: 12px; font-weight: bold">{{ data.decoratorDialog.item?.name }}</div>
      <el-checkbox-group v-model="data.decoratorDialog.selected">
        <el-checkbox
          v-for="d in data.availableDecorators"
          :key="d.code"
          :label="d.code"
        >{{ d.label }} (+${{ d.surcharge }})</el-checkbox>
      </el-checkbox-group>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="data.decoratorDialog.visible = false">Cancel</el-button>
          <el-button type="primary" @click="confirmDecorator">Add</el-button>
        </div>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import {reactive, onMounted, onUnmounted} from "vue";
import request from "@/utils/request";
import {ElMessage} from "element-plus";
import websocketService from "@/utils/websocket";

const data = reactive({
  table: {},
  user: JSON.parse(localStorage.getItem('canteen-user') || '{}'),
  foodsList: [],
  dialogShow: false,
  orderList: [],
  total: 0,
  orderTotal: 0,
  availableDecorators: [],
  decoratorDialog: {
    visible: false,
    item: null,
    selected: []
  }
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

// WebSocket连接
onMounted(() => {
  const user = JSON.parse(localStorage.getItem('canteen-user') || '{}');
  if (user.id) {
    websocketService.connect(user.id.toString());
  }
  // load available decorators from backend enum
  request.get('/decorators/list').then(res => {
    data.availableDecorators = res.data || []
  })
});

onUnmounted(() => {
  websocketService.disconnect();
});

const showOrderList = () => {
  data.dialogShow = true
}

const formatDecorators = (decorators) => {
  return decorators
    .map(d => (d === 'spicy' ? 'spicy' : d === 'garlic' ? 'garlic' : d))
    .join(', ')
}

const decoratedUnitPrice = (item) => {
  let unit = Number(item.price)
  if (Array.isArray(item.decorators) && Array.isArray(data.availableDecorators)) {
    const map = Object.fromEntries(data.availableDecorators.map(d => [d.code, Number(d.surcharge)]))
    for (const code of item.decorators) {
      unit += Number(map[code] || 0)
    }
  }
  return Number(unit.toFixed(2))
}

const openDecoratorDialog = (foods) => {
  data.decoratorDialog.item = foods
  data.decoratorDialog.selected = []
  data.decoratorDialog.visible = true
}

const confirmDecorator = () => {
  const foods = data.decoratorDialog.item
  const selected = [...data.decoratorDialog.selected]
  data.decoratorDialog.visible = false

  // attach decorators to the item instance to persist in order list
  const decoratedKey = (id, decorators) => id + '::' + decorators.sort().join(',')

  let f = data.orderList.find(item => item._key === decoratedKey(foods.id, selected))
  if (f) {
    f.num += foods.num
  } else {
    let foods1 = JSON.parse(JSON.stringify(foods))
    foods1.decorators = selected
    foods1._key = decoratedKey(foods.id, selected)
    data.orderList.push(foods1)
  }

  // recompute total count and price
  data.total = data.orderList.map(item => item.num).reduce((acc, cur) => acc + cur, 0)
  data.orderTotal = 0
  data.orderList.forEach(item => {
    data.orderTotal += decoratedUnitPrice(item) * item.num
  })
  data.orderTotal = Number(data.orderTotal.toFixed(2))
  ElMessage.success('Item added to order')
}

// legacy add without decorators (fallback if needed)
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
    data.orderTotal += decoratedUnitPrice(item) * item.num
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
  // build payload for server-side price verification and content rendering
  const items = data.orderList.map(item => ({
    foodId: item.id,
    quantity: item.num,
    decorators: item.decorators || []
  }))
  request.post('/orders/addWithItems', {
    userId: data.user.id,
    status: 'PENDING',
    items
  }).then(res => {
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