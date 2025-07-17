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
        <el-form :model="formData" label-width="0px" ref="formRef">
          <el-form-item label="" prop="username"
                        :rules="[{required:true,message:'Please enter your username',trigger:[ 'blur','change']}]">
            <el-input
                :prefix-icon="User"
                placeholder="Enter your username"
                v-model.trim="formData.username"
                clearable
            ></el-input>
          </el-form-item>
          <el-form-item label="" prop="password"
                        :rules="[{required:true,message:'Please enter your password',trigger:[ 'blur','change']}]">
            <el-input
                :prefix-icon="Lock"
                placeholder="Enter your password"
                show-password
                style="width: 240px"
                v-model.trim="formData.password"
                clearable
            ></el-input>
          </el-form-item>
          <el-form-item label="" prop="type">
            <el-select v-model="formData.type" placeholder="Select user type" style="width: 240px">
              <el-option label="Administrator" value="ADMIN"></el-option>
              <el-option label="User" value="USER"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="">
            <el-space direction="vertical" alignment="left" style="width: 100%">
              <el-button @click="submitForm()" type="primary" style="width: 100%">Log In</el-button>
              <router-link tag="span" :to="{path:'register'}">
                <span style="float: right">No account? Register now</span>
              </router-link>
              <router-link tag="span" :to="{path:'retrievePassword'}">
                <span class="button" style="float: right">Forgot password?</span>
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
import { User, Lock } from "@element-plus/icons-vue";
import router from "@/router/index.js";

const formData = ref({
  username: '',
  password: '',
  type: 'ADMIN'
});

const formRef = ref(null);
const submitForm = () => {
  formRef.value.validate((valid) => {
    if (!valid) return;

    http.post("/common/login", formData.value).then(res => {
      if (!res) return;

      ElMessage({
        message: "Login successful, redirecting...",
        type: "success"
      });

      localStorage.setItem("token", res.data);

      http.get("/common/currentUser").then(res1 => {
        let currentUser = res1.data;
        localStorage.setItem("currentUser", JSON.stringify(currentUser));

        if (currentUser.type === "USER") {
          router.push({ path: "/" });
        } else {
          router.push({ path: "/admin" });
        }
      });
    });
  });
};
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
  width: 300px;
  margin: 0 auto;
  text-align: center;
}
</style>
