<template>
  <div class="main-context">
    <el-card class="box-card">
      <el-space direction="vertical" style="width: 100%" size="large">
        <el-space>
          <img src="../assets/logo.png" width="100%" style="width: 55px">
          <el-space direction="vertical" style="width: 100%" size="small">
            <h2 style="font-style: oblique">Management System</h2>
<!--            <span style="font-style: oblique; font-size: 15px">javadh.com</span>-->
          </el-space>
        </el-space>
        <el-form :model="formData" label-width="100px" :rules="rules" ref="formRef" style="width: 100%">
          <el-form-item label="Avatar" prop="avatarUrl"
                        style="width: 100%"
                        :rules="[{ required: true, message: 'Please select an avatar', trigger: ['blur', 'change'] }]">
            <MyUpLoad type="imageCard" :limit="1" :files="formData.avatarUrl"
                      @setFiles="formData.avatarUrl = $event" />
          </el-form-item>

          <el-form-item label="User Type" prop="type"
                        :rules="[{ required: true, message: 'Please select user type', trigger: ['blur', 'change'] }]">
            <el-select v-model="formData.type" placeholder="Select user type" style="width: 180px">
              <el-option label="Administrator" value="ADMIN"></el-option>
              <el-option label="User" value="USER"></el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="Username" prop="username"
                        :rules="[{ required: true, message: 'Please enter username', trigger: ['blur', 'change'] }]">
            <el-input
                style="width: 180px"
                placeholder="Enter username"
                v-model.trim="formData.username"
                clearable />
          </el-form-item>

          <el-form-item label="Password" prop="password"
                        :rules="[{ required: true, message: 'Please enter password', trigger: ['blur', 'change'] }]">
            <el-input
                style="width: 180px"
                placeholder="Enter password"
                show-password
                v-model.trim="formData.password"
                clearable />
          </el-form-item>

          <el-form-item label="Nickname" prop="nickname"
                        :rules="[{ required: true, message: 'Please enter nickname', trigger: ['blur', 'change'] }]">
            <el-input
                style="width: 180px"
                placeholder="Enter nickname"
                v-model.trim="formData.nickname"
                clearable />
          </el-form-item>

          <el-form-item label="" style="width: 100%">
            <el-space direction="vertical" alignment="left" style="width: 100%">
              <el-button @click="submitForm()" type="success" style="width: 100%">Register</el-button>
              <router-link tag="span" :to="{ path: 'login' }">
                <span style="float: right">Already have an account? Log in</span>
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
import MyUpLoad from "@/components/MyUpload.vue";
import router from "@/router/index.js";

const formRef = ref(null);
const formData = ref({
  type: 'USER',
  username: '',
  nickname: '',
  avatarUrl: '',
  password: ''
});

const rules = ref({
  username: [
    { required: true, message: 'Please enter username', trigger: 'blur' },
  ],
  password: [
    { required: true, message: 'Please enter password', trigger: 'blur' },
  ],
});

const submitForm = () => {
  formRef.value.validate((valid) => {
    if (!valid) return;

    http.put("/common/register", formData.value).then(res => {
      if (!res) return;

      ElMessage({
        message: "Registration successful, redirecting...",
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
