<template>
  <div class="main-context">
    <el-card class="box-card">
      <el-space direction="vertical" style="width: 100%" size="large">
        <el-space>
          <img src="../assets/logo.png" width="100%" style="width: 55px">
          <el-space direction="vertical" style="width: 100%" size="small">
            <h2 style="font-style: oblique">Management System</h2>
            <!--            <span style="font-style: oblique;font-size: 15px">javadh.com</span>-->
          </el-space>
        </el-space>

        <el-form :model="formData" label-width="100px" :rules="rules" ref="formRef" style="width: 100%">
          <el-form-item label="User Type" prop="type">
            <el-select v-model="formData.type" placeholder="Please select user type">
              <el-option label="Administrator" value="ADMIN"></el-option>
              <el-option label="User" value="USER"></el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="Phone Number" prop="tel"
                        :rules="[{ required: true, message: 'Please enter phone number', trigger: ['blur', 'change'] }]">
            <el-input
                style="width: 180px"
                placeholder="Enter phone number"
                v-model.trim="formData.tel"
                clearable />
          </el-form-item>

          <el-form-item label="Verification Code" prop="code"
                        :rules="[{ required: true, message: 'Please enter verification code', trigger: ['blur', 'change'] }]">
            <el-input
                style="width: 180px"
                placeholder="Enter verification code"
                v-model.trim="formData.code"
                clearable />
          </el-form-item>

          <el-form-item label="New Password" prop="password"
                        :rules="[{ required: true, message: 'Please enter new password', trigger: ['blur', 'change'] }]">
            <el-input
                style="width: 180px"
                placeholder="Enter new password"
                show-password
                v-model.trim="formData.password"
                clearable />
          </el-form-item>

          <el-form-item label="" style="width: 100%">
            <el-space direction="vertical" alignment="left" style="width: 100%">
              <el-button @click="submitForm()" type="success" style="width: 100%">Reset Password</el-button>
              <router-link tag="span" :to="{ path: 'login' }">
                <el-button type="text" class="button" style="float: right">Back to Login</el-button>
              </router-link>
            </el-space>
          </el-form-item>
        </el-form>
      </el-space>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import http from "@/utils/http.js";
import router from "@/router/index.js";

const formRef = ref(null);
const formData = ref({
  type: 'USER',
  tel: '',
  code: '',
  password: ''
});

const submitForm = () => {
  formRef.value.validate((valid) => {
    if (!valid) return;

    http.post("/common/retrievePassword", formData.value).then(res => {
      if (!res) return;

      ElMessage({
        message: "Password reset successfully. Redirecting...",
        type: "success"
      });
      router.push({ path: "/login" });
    });
  });
}
</script>

<style scoped>
.main-context {
  height: 100vh;
  background: url("../assets/login.png") no-repeat center center fixed;
  background-size: cover;
  display: flex;
  justify-content: center;
  align-items: center;
  color: white;
}

.box-card {
  width: 350px;
  margin: 0 auto;
  text-align: center;
}
</style>
