<template>
  <div>
    <div style="height: 60px; line-height: 60px; background-color: #385bde; padding: 0 10px; position: relative;">
      <img src="@/assets/imgs/logo.png" alt="" style="width: 40px; height: 40px; margin-right: 20px; position: relative; top: 10px">
      <span style="color: white; font-size: 18px; line-height: 60px">Canteen management</span>
      <div style="position: absolute; top: 30px; right: 10px; color: white">{{ data.user.name }}</div>
    </div>

    <div style="display: flex">
      <div style="width: 200px; border-right: 1px solid #ddd; min-height: calc(100vh - 60px)">
        <el-menu
            router
            style="border: none"
            :default-active="$route.path"
            :default-openeds="['/home', '2']"
        >
          <el-menu-item index="/home">
            <el-icon><HomeFilled /></el-icon>
            <span>Home Page</span>
          </el-menu-item>
          <el-menu-item index="/order">
            <el-icon><Dish /></el-icon>
            <span>My Order</span>
          </el-menu-item>
          <el-menu-item index="/orderManager">
            <el-icon><List /></el-icon>
            <span>Order Management</span>
          </el-menu-item>
          <el-menu-item index="/discount" v-if="data.user.role === 'ADMIN'">
            <el-icon><Discount /></el-icon>
            <span>Pricing Strategy</span>
          </el-menu-item>
          <el-sub-menu index="2" v-if="data.user.role === 'ADMIN'">
            <template #title>
              <el-icon><User /></el-icon>
              <span>Info Management</span>
            </template>
            <el-menu-item index="/tables">
              <el-icon><Dish /></el-icon>
              <span>Table Info</span>
            </el-menu-item>
            <el-menu-item index="/foods">
              <el-icon><Bowl /></el-icon>
              <span>Item Info</span>
            </el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="3"  v-if="data.user.role === 'ADMIN'">
            <template #title>
              <el-icon><User /></el-icon>
              <span>User Management</span>
            </template>
            <el-menu-item index="/admin">
              <el-icon><UserFilled /></el-icon>
              <span>Admin Info</span>
            </el-menu-item>
            <el-menu-item index="/user">
              <el-icon><UserFilled /></el-icon>
              <span>Customer Info</span>
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item index="/person">
            <el-icon><User /></el-icon>
            <span>Profile</span>
          </el-menu-item>
          <el-menu-item index="login" @click="logout">
            <el-icon><SwitchButton /></el-icon>
            <span>EXIT</span>
          </el-menu-item>
        </el-menu>
      </div>

      <div style="flex: 1; width: 0; background-color: #f8f8ff; padding: 10px">
        <router-view @updateUser="updateUser" />
      </div>
    </div>

  </div>
</template>

<script setup>
import {reactive, onMounted, onUnmounted} from "vue";
import { useRoute } from 'vue-router'
import {User, UserFilled, HomeFilled, Dish, List, Bowl, SwitchButton, Discount} from "@element-plus/icons-vue";
import { ElMessage } from 'element-plus';
import websocketService from "@/utils/websocket";

const $route = useRoute()

const data = reactive({
  user: JSON.parse(localStorage.getItem('canteen-user') || '{}')
})

onMounted(() => {
  const user = JSON.parse(localStorage.getItem('canteen-user') || '{}');
  console.log('Manager.vue mounted, user:', user);
  
  // 确保 ElMessage 可以全局访问
  window.ElMessage = ElMessage;
  
  if (user.id) {
    console.log('Connecting WebSocket for user ID:', user.id);
    websocketService.connect(user.id.toString());
  } else {
    console.warn('No user ID found, cannot connect WebSocket');
  }
  
  if ('Notification' in window && Notification.permission === 'default') {
    console.log('Requesting notification permission');
    Notification.requestPermission();
  }
});

onUnmounted(() => {
  websocketService.disconnect();
});

const logout = () => {
  websocketService.disconnect();
  localStorage.removeItem('canteen-user')
}

const updateUser = () => {
  data.user = JSON.parse(localStorage.getItem('canteen-user') || '{}')
}
</script>

<style scoped>
.el-menu-item.is-active {
  background-color: #e0e4ff !important;
}
.el-menu-item:hover {
  background-color: #e9eeff !important;
  color: #1450aa;
}
</style>