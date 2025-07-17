<template>
  <div>
    <el-upload
        ref="upload"
        :action="uploadUrl"
        :list-type="listType"
        :on-preview="handlePreview"
        :file-list="fileList"
        :limit="limit"
        :accept="accept"
        :headers="uploadHeaders"
        :on-success="handleFileSuccess"
        :on-exceed="handleExceed"
        :on-remove="handleRemove">
      <el-button size="small" type="primary"> {{
          limit === 1 && fileList.length > 0 ? 'Click to replace' : 'Click to upload'
        }}
      </el-button>
      <div slot="tip" class="el-upload__tip">{{ tip }}</div>
    </el-upload>
    <el-dialog v-model="dialogVisible" v-if="dialogVisible">
      <div>
        <img v-if="type==='image'||type==='imageCard'" width="100%" :src="previewFile.url" alt="">
        <video v-if="type==='video'" width="100%" :src="previewFile.url" controls></video>
        <audio v-if="type==='audio'" width="100%" :src="previewFile.url" controls></audio>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, reactive, onMounted} from 'vue';
import utils from "@/utils/tools.js";
import {ElMessage, genFileId} from "element-plus";

const props = defineProps({
  /**
   * file type
   */
  type: {
    type: String,
    default: "file"
  },
  /**
   * file list
   */
  files: {
    type: String,
    default: ""
  },
  /**
   * prompt msg
   */
  tip: {
    type: String,
    default: ""
  },
  /**
   * limit of upload file
   */
  limit: {
    type: Number,
    default: 100
  }
});// Component object
const upload = ref()
// Upload request headers, need to carry token
const uploadHeaders = reactive({
  // Set token
  token: utils.getToken()
});
// Server upload URL
const uploadUrl = ref('');
// Uploaded file list
const fileList = ref([]);
// Allowed file types for upload
const accept = ref('');
// Default file list display type
const listType = ref('picture-card');
// Preview dialog visibility
const dialogVisible = ref(false);
// Preview file info
const previewFile = ref('');

onMounted(() => {
  // Set upload URL
  uploadUrl.value = import.meta.env.VITE_APP_API_URL + "/file/upload";
  load();
});

// Load files
function load() {
  if (props.files) {
    // Split files by comma
    let files = props.files.split(",");
    for (let file of files) {
      // Split filename from URL
      let strings = file.split("/");
      fileList.value.push({
        name: strings[strings.length - 1],
        url: file
      });
    }
  }
  switch (props.type) {
      /**
       * Image card type
       */
    case "imageCard":
      listType.value = "picture-card";
      accept.value = "image/*";
      break;
      /**
       * Image
       */
    case "image":
      listType.value = "picture";
      accept.value = "image/*";
      break;
      /**
       * Video
       */
    case "video":
      accept.value = "video/*";
      listType.value = "text";
      break;
      /**
       * Audio
       */
    case "audio":
      accept.value = "audio/*";
      listType.value = "text";
      break;
      /**
       * File / Attachment
       */
    case "file":
      listType.value = "text";
      break;
  }
}

// Emit event to parent component to set file data
const emit = defineEmits(['setFiles']);

/**
 * Notify parent component when files change
 */
function setFiles() {
  let files = fileList.value.map((item) => {
    return item.url;
  });
  emit("setFiles", files.join(","));
}

/**
 * Handle successful file upload
 * @param response
 * @param file
 * @param fileListRes
 */
function handleFileSuccess(response, file, fileListRes) {
  // Add file to file list
  fileList.value.push({
    name: response.data.name,
    url: response.data.url
  });
  // Notify parent component about file change
  setFiles();
}

/**
 * Delete file
 * @param file
 * @param fileListRes
 */
function handleRemove(file, fileListRes) {
  fileList.value = fileListRes;
  // Notify parent component about file change
  setFiles();
}

/**
 * Preview file
 * @param file
 */
function handlePreview(file) {
  // Set preview file
  previewFile.value = file;
  if (props.type === "file") {
    // For file type, download directly
    downloadFile();
    return;
  }
  // Open preview dialog
  dialogVisible.value = true;
}

/**
 * Handle files exceeding limit
 * @param files
 */
function handleExceed(files) {
  // If only one file allowed, replace it
  if (props.limit === 1) {
    // This method removes the last element in the array and returns it. Returns undefined if array is empty.
    fileList.value.pop()
    upload.value.clearFiles()
    const file = files[0];
    file.uid = genFileId()
    upload.value.handleStart(file)
    upload.value.submit()
  } else {
    ElMessage.warning(`Only up to ${props.limit} images are allowed`);
  }
}

/**
 * file download
 */
function downloadFile() {
  const link = document.createElement('a');
  link.style.display = 'none';
  document.body.appendChild(link);
  link.href = previewFile.value.url;
  console.log(previewFile.value);
  link.setAttribute('download', previewFile.value.name); // can custom filename when downloading
  link.click();
  link.remove();
}
</script>


<style>
</style>
