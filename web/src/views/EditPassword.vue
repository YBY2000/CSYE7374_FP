<template>
  <div style="width:600px;margin: 0 auto">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>密码修改</span>
        </div>
      </template>
      <el-form :model="formData" label-width="100px"
      >
        <el-form-item prop="oldPassword" label="OldPassword">
          <el-input type="password"
                    v-model="formData.oldPassword"
                    auto-complete="off"
                    placeholder="Password"
          ></el-input>
        </el-form-item>
        <el-form-item prop="newPassword" label="NewPassword">
          <el-input type="password"
                    v-model="formData.newPassword"
                    auto-complete="off"
                    placeholder="New Password"
          ></el-input>
        </el-form-item>
        <el-form-item style="width:100%;">
          <el-button type="primary" style="width:100px;" @click="handleSubmit">Change Password</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>
<script setup>
import http from "@/utils/http.js";
import {ref} from "vue";
import {ElMessage} from "element-plus";
import router from "@/router/index.js";

const formData = ref({
  id: "",
  oldPassword: '',
  newPassword: '',
})

function handleSubmit() {
  http.post('/common/updatePassword', formData.value).then(res => {
    if (!res) {
      return
    }
    localStorage.clear()
    ElMessage({
      message: 'Password change succeed. Please login',
      type: 'success'
    });
    router.push({path: "/login"})
  })
}
</script>
