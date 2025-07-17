<template>
  <div style="width:600px;margin: 0 auto">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>Change Profile</span>
        </div>
      </template>
      <el-form :model="formData" label-width="100px">
        <el-form-item prop="img" label="Avatar">
          <MyUpLoad type="imageCard" :limit="1" :files="formData.avatarUrl"
                    @setFiles="formData.avatarUrl =$event"></MyUpLoad>
        </el-form-item>
        <el-form-item prop="username" label="Username">
          <el-input type="text"
                    v-model="formData.username"
                    auto-complete="off"
                    placeholder="Username"
                    :disabled="true"
          ></el-input>
        </el-form-item>
        <el-form-item prop="nickname" label="Nickname">
          <el-input type="text"
                    v-model="formData.nickname"
                    auto-complete="off"
                    placeholder="Username"
                    :disabled="true"
          ></el-input>
        </el-form-item>
        <el-form-item prop="email" label="Email">
          <el-input
              v-model="formData.email"
              auto-complete="off"
              placeholder="Email"
          ></el-input>
        </el-form-item>
        <el-form-item prop="tel" label="Phone">
          <el-input type="text"
                    v-model="formData.tel"
                    auto-complete="off"
                    placeholder="Phone"
          ></el-input>
        </el-form-item>
        <el-form-item style="width:100%;">
          <el-button type="primary" style="width:100px;" @click="handleSubmit">Change</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>
<script setup>
import {ref} from 'vue';
import utils from "@/utils/tools.js";
import MyUpLoad from "@/components/MyUpload.vue";
import {ElMessage} from 'element-plus';
import http from "@/utils/http.js";

const formData = ref({});

load();

function load() {
  formData.value = utils.getCurrentUser();
}


function handleSubmit() {
  http.post('/common/updateCurrentUser', formData.value).then(res => {
    http.get("/common/currentUser").then(res1 => {
      let currentUser = res1.data;
      localStorage.setItem("currentUser", JSON.stringify(currentUser));
      ElMessage({
        message: 'Successfully Changed',
        type: 'success'
      });
    })
  });
};
</script>
