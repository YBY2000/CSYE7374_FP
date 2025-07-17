<template>
  <div>
    <el-space direction="vertical" alignment="left" style="width: 100%">
      <el-card>
        <el-form ref="searchFormComponents" :model="searchForm" inline>
          <el-form-item label="username" prop="username">
            <el-input v-model="searchForm.username" clearable></el-input>
          </el-form-item>
          <el-form-item label="tel" prop="tel">
            <el-input v-model="searchForm.tel" clearable></el-input>
          </el-form-item>
          <el-form-item label="status" prop="status">
            <el-select v-model="searchForm.status" placeholder="Please Choose" clearable style="width: 150px">
              <el-option label="Enalbe" value="Enalbe"/>
              <el-option label="Disable" value="Disable"/>
            </el-select>
          </el-form-item>
          <el-form-item label="">
            <el-button type="primary" :icon="Search" @click="search">Search</el-button>
            <el-button :icon="Refresh" @click="resetSearch">Reset</el-button>
          </el-form-item>
        </el-form>
        <el-space>
          <el-button type="primary" @click="add" :icon="Plus">Add</el-button>
          <el-button type="danger" :icon="Delete" @click="batchDelete(null)" :disabled="selectionRows.length<=0">
            Batch Delete
          </el-button>
        </el-space>
      </el-card>
      <el-card>
        <el-table ref="tableComponents"
                  :data="listData"
                  tooltip-effect="dark"
                  style="width: 100%"
                  border
                  @selection-change="selectionChange">
          <el-table-column type="selection" width="55"></el-table-column>
          <el-table-column prop="id" label="ID" width="50"></el-table-column>
          <el-table-column prop="username" label="Username"></el-table-column>
          <el-table-column prop="nickname" label="Nickname"></el-table-column>
          <el-table-column label="Avatar">
            <template #default="scope">
              <el-image style="width: 50px; height: 50px" :src="scope.row.avatarUrl"
                        :preview-src-list="[scope.row.avatarUrl]"
                        :preview-teleported="true"></el-image>
            </template>
          </el-table-column>
          <el-table-column prop="tel" label="Tel"></el-table-column>
          <el-table-column prop="email" label="Email" width="150"></el-table-column>
          <el-table-column prop="status" label="Status">
            <template #default="scope">
              <el-tag type="success" v-if="scope.row.status==='Enable'">Enable</el-tag>
              <el-tag type="danger" v-if="scope.row.status==='Disable'">Disable</el-tag>
            </template>
          </el-table-column>
          <el-table-column fixed="right" label="Advanced Actions" width="140">
            <template #default="scope">
              <el-button type="success" :icon="RefreshLeft" @click="resetPassword( scope.row)">Reset Password</el-button>
            </template>
          </el-table-column>
          <el-table-column fixed="right" label="Actions" width="200">
            <template #default="scope">
              <el-button :icon="Edit" @click="edit(scope.$index, scope.row)">Edit</el-button>
              <el-button :icon="Delete" type="danger" @click="deleteOne(scope.$index, scope.row)">Delete</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div style="margin-top: 20px">
          <el-pagination
              @current-change="currentChange"
              @size-change="sizeChange"
              :page-size="pageInfo.pageSize"
              :current-page="pageInfo.pageNum"
              background
              layout="total,sizes, prev, pager, next"
              :total="pageInfo.total">
          </el-pagination>
        </div>
      </el-card>
    </el-space>
    <el-dialog
        v-model="dialogOpen"
        v-if="dialogOpen"
        :title="formData.id?'编辑':'新增'"
        width="800px"
    >
      <el-form ref="formRef" :model="formData" label-width="100px" inline>
        <el-form-item label="Avatar" prop="avatarUrl" style="width: 100%"
                      :rules="[{required:true,message:'Cannot be empty',trigger:[ 'blur','change']}]">
          <MyUpLoad type="imageCard" :limit="1" :files="formData.avatarUrl"
                    @setFiles="formData.avatarUrl =$event"></MyUpLoad>
        </el-form-item>
        <el-form-item label="Username" prop="username"
                      :rules="[{required:true,message:'Cannot be empty',trigger:[ 'blur','change']}]">
          <el-input v-model="formData.username" placeholder="Username" :disabled="formData.id!=null"></el-input>
        </el-form-item>
        <el-form-item label="Name" prop="nickname"
                      :rules="[{required:true,message:'Cannot be empty',trigger:[ 'blur','change']}]">
          <el-input v-model="formData.nickname" placeholder="Nickname"></el-input>
        </el-form-item>
        <el-form-item label="Phone" prop="tel"
                      :rules="[{required:true,message:'Cannot be empty',trigger:[ 'blur','change']}]">
          <el-input v-model="formData.tel" placeholder="Phone"></el-input>
        </el-form-item>
        <el-form-item label="Email" prop="email"
                      :rules="[{required:true,message:'Cannot be empty',trigger:[ 'blur','change']}]">
          <el-input v-model="formData.email" placeholder="Email"></el-input>
        </el-form-item>
        <el-form-item label="Status" prop="status"
                      :rules="[{required:true,message:'Cannot be empty',trigger:[ 'blur','change']}]">
          <el-radio-group v-model="formData.status">
            <el-radio label="Enable"></el-radio>
            <el-radio label="Disable"></el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submit" :icon="Check">Submit</el-button>
          <el-button @click="closeDialog" :icon="Close">Cancel</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import request from "@/utils/http.js";
import {Check, Close, Delete, Edit, Refresh, Plus, Search, RefreshLeft} from '@element-plus/icons-vue'
import {ref, toRaw} from "vue";
import {ElMessage, ElMessageBox} from "element-plus";
import MyUpLoad from "@/components/MyUpload.vue";
import MyEditor from "@/components/MyEditor.vue";

const searchFormComponents = ref();
const tableComponents = ref();

const listData = ref([]);
const pageInfo = ref({
  //current page
  pageNum: 1,
  //page size
  pageSize: 10,
  //total page
  total: 0
});
const searchForm = ref({});


getPageList()

/**
 * get page info
 */
function getPageList() {
  let data = Object.assign(toRaw(searchForm.value), toRaw(pageInfo.value))
  request.get("/admin/page", {
    params: data
  }).then(res => {
    listData.value = res.data.list
    pageInfo.value.total = res.data.total
  })
}

/**
 * get page
 * @param e
 */
function currentChange(e) {
  pageInfo.value.pageNum = e
  getPageList()
}

/**
 * change page size
 * @param e
 */
function sizeChange(e) {
  pageInfo.value.pageSize = e
  getPageList()
  console.log(e)
}

/**
 * search
 */
function search() {
  pageInfo.value.pageNum = 1
  getPageList()
}

/**
 * reset search bar
 */
function resetSearch() {
  searchFormComponents.value.resetFields();
  getPageList()
}


const dialogOpen = ref(false);
const formData = ref({});
const formRef = ref();

/**
 * add
 */
function add() {
  dialogOpen.value = true
  formData.value = {}
}

/**
 * edit
 * @param index
 * @param row
 */
function edit(index, row) {
  dialogOpen.value = true
  formData.value = Object.assign({}, row)
}

/**
 * close dialog
 */
function closeDialog() {
  dialogOpen.value = false
}

/**
 * submit data
 */
function submit() {
  // add
  formRef.value.validate((valid) => {
    if (!valid) {
      ElMessage({
        message: "Validation failed, please check required fields!",
        type: 'warning'
      });
      return;
    }
    if (!formData.value.id) {
      request.post("/admin/add", formData.value).then(res => {
        if (!res) {
          return;
        }
        dialogOpen.value = false;
        ElMessage({
          message: res.msg + " The new user password defaults to: 123456",
          type: 'success'
        });
        getPageList();
      });
    } else {
      // update
      request.put("/admin/update", formData.value).then(res => {
        if (!res) {
          return;
        }
        dialogOpen.value = false;
        ElMessage({
          message: "Operation successful",
          type: 'success'
        });
        getPageList();
      });
    }
  });
}


const selectionRows = ref([]);

/**
 * multi select
 * @param rows
 */
function selectionChange(rows) {
  selectionRows.value = rows
}

/**
 * single delete
 * @param index
 * @param row
 */
function deleteOne(index, row) {
  batchDelete([row])
}

/**
 * batch delete
 * @param rows
 */
function batchDelete(rows) {
  if (!rows) {
    rows = selectionRows.value;
  }
  let ids = rows.map(item => item.id);
  ElMessageBox.confirm(`This operation will permanently delete data with IDs [${ids}]. Continue?`, 'Notice', {
    confirmButtonText: 'Confirm',
    cancelButtonText: 'Cancel',
    type: 'warning',
    center: true
  }).then(() => {
    request.delete("/admin/delBatch", {data: ids}).then(res => {
      if (!res) {
        return;
      }
      ElMessage({
        message: "Operation successful",
        type: 'success'
      });
      getPageList();
    });
  }).catch(() => {
    ElMessage({
      type: 'info',
      message: 'Deletion cancelled'
    });
    tableComponents.value.clearSelection();
  });
}


/**
 * reset password
 * @param row
 */
function resetPassword(row) {
  request.post("/common/resetPassword?type=ADMIN&id=" + row.id).then(res => {
    if (!res) {
      return
    }
    ElMessage({
      message: "Operation successful",
      type: 'success'
    });
  })
}


</script>

<style scoped>

</style>
