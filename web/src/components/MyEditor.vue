<template>
  <div>
    <div class="nav" style="border: 1px solid #ccc; margin-top: 10px;color: #000">
      <!-- Tool bar-->
      <Toolbar
          style="border-bottom: 1px solid #ccc"
          :editor="myEditor"
          :defaultConfig="toolbarConfig"
      />
      <!-- Editor -->
      <Editor
          style="height: 400px; overflow-y: hidden"
          :defaultConfig="editorConfig"
          v-model="html"
          @onChange="onChange"
          @onCreated="onCreated"
      />
    </div>
  </div>
</template>
<script setup>
import {ref, defineProps, onMounted, onBeforeUnmount} from 'vue';
import {Editor, Toolbar} from "@wangeditor/editor-for-vue";
import utils from "@/utils/tools.js";

const props = defineProps({
  content: {
    type: String,
    default: "",
  }
});

const myEditor = ref(null);
const html = ref(props.content);
const toolbarConfig = ref({
// toolbarKeys: [ /* Which menus to display, how to sort and group them */ ],
// excludeKeys: [ /* Which menus to hide */ ],
});

const editorConfig = ref({
  placeholder: "Please input content...",
  // autoFocus: false,
  MENU_CONF: {
    uploadImage: {
      server: import.meta.env.VITE_APP_API_URL + "/file/upload",
      headers: {
        token: utils.getToken()
      },
      fieldName: 'file',
      customInsert(res, insertFn) {
        insertFn(res.data.url, res.data.name, res.data.url);
      },
    },
    uploadVideo: {
      maxFileSize: 100 * 1024 * 1024, // 10M
      server: import.meta.env.VITE_APP_API_URL + "/file/upload",
      headers: {
        token: utils.getToken()
      },
      fieldName: 'file',
      customInsert(res, insertFn) {
        insertFn(res.data.url, res.data.name, res.data.url);
      },
    }
  }
});

function onCreated(editor) {
  myEditor.value = Object.seal(editor);
}

const emit = defineEmits(['content-change']);

function onChange(editor) {
  html.value = editor.getHtml();
  // using Vue 3 Composition API，no need to use this.$emit
  emit("content-change", html.value);
}

onMounted(() => {
  // If you need to perform some actions after the component is mounted, you can add them here
});

onBeforeUnmount(() => {
  if (myEditor.value != null) {
    myEditor.value.destroy();
  }
});
</script>
<style src="@wangeditor/editor/dist/css/style.css"></style>
<style>
.nav .title {
  margin-top: 10px;
  color: #000 !important;
}
</style>
