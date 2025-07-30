<template>
  <div style="width: 50%">
    <div class="card">
      <el-form :model="data.user" label-width="100px" style="padding-right: 50px">
        <el-form-item label="Avatar">
          <el-upload :show-file-list="false" class="avatar-uploader" action="http://localhost:9090/files/upload" :on-success="handleFileUpload">
            <img v-if="data.user.avatar" :src="data.user.avatar" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="Account">
          <el-input disabled v-model="data.user.username" autocomplete="off" />
        </el-form-item>
        <el-form-item label="Name">
          <el-input v-model="data.user.name" autocomplete="off" />
        </el-form-item>
        <el-form-item label="Gender" v-if="data.user.role === 'USER'">
          <el-radio-group v-model="data.user.sex">
            <el-radio label="Male"></el-radio>
            <el-radio label="Female"></el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="Phone" v-if="data.user.role === 'USER'">
          <el-input v-model="data.user.phone" autocomplete="off" />
        </el-form-item>
        <el-form-item label="Balance" v-if="data.user.role === 'USER'">
          <el-input readonly v-model="data.user.account" autocomplete="off" />
        </el-form-item>
        <el-form-item>
          <el-button type="success" @click="handleCharge">Top up</el-button>
          <el-button type="primary" @click="save">Save</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-dialog v-model="data.formVisible" title="TopUp" width="30%" destroy-on-close>
      <el-form :model="data.form" label-width="100px" style="padding-right: 50px">
        <el-form-item label="Amount">
          <el-input-number style="width: 200px" v-model="data.form.money" autocomplete="off" />
        </el-form-item>
      </el-form>
      <template #footer>
      <span class="dialog-footer">
        <el-button @click="data.formVisible = false">Cancel</el-button>
        <el-button type="primary" @click="saveCharge">Sure</el-button>
      </span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import {reactive} from "vue"
import request from "@/utils/request";
import {ElMessage, ElMessageBox} from "element-plus";

const data = reactive({
  user: JSON.parse(localStorage.getItem('canteen-user') || '{}'),
  formVisible: false,
  form: {}
})

// query the updated balance
request.get('/user/selectById/' + data.user.id).then(res => {
  data.user.account = res.data.account
})

const handleFileUpload = (file) => {
  data.user.avatar = file.data
}

const handleCharge = () => {
  data.formVisible = true
  data.form.money = 0
}

const saveCharge = () => {
  if (data.form.money <= 0) {
    ElMessage.warning('Top up amount must be greater than 00')
    return
  }
  data.user.account += data.form.money
  request.put('/user/update', data.user).then(res => {
    if (res.code === '200') {
      ElMessage.success('Top up successful')
      data.formVisible = false
      // save the user data store in cache
      localStorage.setItem('canteen-user', JSON.stringify(data.user))
      emit('updateUser')
    } else {
      ElMessage.error(res.msg)
    }
  })
}

const emit = defineEmits(["updateUser"])
// store the updated user data in database
const save = () => {
  if (data.user.role === 'ADMIN') {
    request.put('/admin/update', data.user).then(res => {
      if (res.code === '200') {
        ElMessage.success('Updated successful')
      } else {
        ElMessage.error(res.msg)
      }
    })
  } else {
    request.put('/user/update', data.user).then(res => {
      if (res.code === '200') {
        ElMessage.success('Updated successful')
      } else {
        ElMessage.error(res.msg)
      }
    })
  }
  // store updated user info in cache
  localStorage.setItem('canteen-user', JSON.stringify(data.user))
  emit('updateUser')
}
</script>

<style scoped>
.avatar-uploader .avatar {
  width: 120px;
  height: 120px;
  display: block;
}
</style>

<style>
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  text-align: center;
}
</style>