<template>
  <div>

    <div class="card" style="margin-bottom: 10px;">
      <el-input prefix-icon="Search" style="width: 300px; margin-right: 10px" placeholder="Enter table number to search" v-model="data.no"></el-input>
      <el-button type="primary" @click="load">Search</el-button>
      <el-button type="info" style="margin: 0 10px" @click="reset">Reset</el-button>
    </div>

    <div class="card" style="margin-bottom: 10px">
      <div style="margin-bottom: 10px">
        <el-button type="primary" @click="handleAdd">Add</el-button>
      </div>
      <el-table :data="data.tableData">
        <el-table-column prop="id" label="ID" width="70"/>
        <el-table-column prop="no" label="TableNumber"/>
        <el-table-column prop="unit" label="Unit"/>
        <el-table-column prop="free" label="IsFree"></el-table-column>
        <el-table-column prop="userName" label="Customer username"></el-table-column>
        <el-table-column label="Actions" width="180">
          <template #default="scope">
            <el-button type="primary" @click="handleEdit(scope.row)">Edit</el-button>
            <el-button type="danger" @click="del(scope.row.id)">Delete</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="card" v-if="data.total">
      <el-pagination background layout="prev, pager, next" @current-change="load" :page-size="data.pageSize" v-model:current-page="data.pageNum" :total="data.total"/>
    </div>

    <el-dialog v-model="data.formVisible" title="Information" width="40%" destroy-on-close>
      <el-form :model="data.form" label-width="100px" style="padding-right: 50px">
        <el-form-item label="TableNumber">
          <el-input v-model="data.form.no" autocomplete="off" />
        </el-form-item>
        <el-form-item label="Unit">
          <el-input v-model="data.form.unit" autocomplete="off" />
        </el-form-item>
        <el-form-item label="IsFree">
          <el-radio-group v-model="data.form.free">
            <el-radio label="Yes"></el-radio>
            <el-radio label="No"></el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
      <span class="dialog-footer">
        <el-button @click="data.formVisible = false">Cancel</el-button>
        <el-button type="primary" @click="save">Save</el-button>
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
  tableData: [],
  total: 0,
  pageNum: 1,  // Current page number
  pageSize: 5,  //Items per page
  formVisible: false,
  form: {},
  no: '',
})

const load = () => {
  request.get('/tables/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      no: data.no
    }
  }).then(res => {
    data.tableData = res.data?.list || []
    data.total = res.data.total
  })
}

load()

const reset = () => {
  data.no = null
  load()
}

const handleAdd = () => {
  data.form = {}  // Initialize form
  data.formVisible = true  // Open dialog
}

// Save Data
const save = () => {
  request.request({
    method: data.form.id ? 'PUT' : 'POST',
    url: data.form.id ? '/tables/update' : '/tables/add',
    data: data.form
  }).then(res => {
    if (res.code === '200') {  // Success
      ElMessage.success('Operation successful')
      data.formVisible = false // Close dialog
      load()  //Reload table data
    } else {
      ElMessage.error(res.msg)
    }
  })
}

const handleEdit = (row) => {
  data.form = JSON.parse(JSON.stringify(row))
  data.formVisible = true
}

const del = (id) => {
  ElMessageBox.confirm('This action cannot be undone. Are you sure you want to delete it?', 'Confirm Delete', { type: 'warning' }).then(res => {
    request.delete('/tables/delete/' + id).then(res => {
      if (res.code === '200') {  // Success
        ElMessage.success('Operation successful')
        load()  // Reload table data
      } else {
        ElMessage.error(res.msg)
      }
    })
  }).catch(err => {
    console.log(err)
  })
}

</script>