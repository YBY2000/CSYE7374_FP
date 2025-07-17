<template>
  <div class="not-found">
    <el-card class="card" shadow="hover">
      <h1 class="error-code">404</h1>
      <h2 class="error-message">Page Not Found</h2>
      <el-button type="primary" @click="goHome">Return Home</el-button>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'NotFound',
  methods: {
    goHome() {
      this.$router.push({ path: '/' }); // change to home page router
    }
  }
}
</script>

<style scoped>
.not-found {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f9f9f9;
}

.card {
  text-align: center;
  padding: 20px;
  border-radius: 10px;
}

.error-code {
  font-size: 100px;
  margin: 0;
  color: #f56c6c; /* red */
}

.error-message {
  font-size: 24px;
  margin: 10px 0;
  color: #606266; /* dark grey */
}
</style>