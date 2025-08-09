<template>
  <div>
    <!-- Page Header -->
    <div style="margin-bottom: 20px;">
      <h2 style="margin: 0; color: #303133; display: flex; align-items: center;">
        <el-icon style="margin-right: 8px; color: #67c23a;"><Discount /></el-icon>
        Pricing Strategy Management
      </h2>
      <p style="margin: 8px 0 0 32px; color: #909399; font-size: 14px;">
        Configure and manage pricing strategies for the canteen system
      </p>
    </div>

    <div class="card">
      <h3 style="margin-bottom: 20px;">Pricing Strategy Settings</h3>
      
      <!-- Current strategy status -->
      <el-card class="strategy-status" style="margin-bottom: 20px;">
        <template #header>
          <span>Current Pricing Strategy</span>
        </template>
        <div style="display: flex; align-items: center; justify-content: space-between;">
          <div>
            <div style="display: flex; align-items: center; margin-bottom: 8px;">
              <el-tag :type="currentDiscount.isEnabled ? 'success' : 'info'" size="large">
                {{ currentDiscount.isEnabled ? 'Discount Strategy Active' : 'Regular Strategy Active' }}
              </el-tag>
              <span v-if="currentDiscount.isEnabled" style="margin-left: 10px; color: #67c23a; font-weight: bold; font-size: 16px;">
                {{ Math.round((1 - currentDiscount.discountRate) * 100) }}% OFF
              </span>
            </div>
            <p style="margin: 5px 0; color: #606266;">
              {{ currentDiscount.description }}
            </p>
          </div>
          <div style="text-align: right; color: #909399; font-size: 12px;">
            <div>Updated: {{ new Date(currentDiscount.updatedTime).toLocaleString() }}</div>
          </div>
        </div>
      </el-card>
      
      <!-- Strategy configuration form -->
      <div style="background: #f9f9f9; padding: 20px; border-radius: 8px; margin-bottom: 20px;">
        <div style="display: flex; align-items: center; margin-bottom: 20px;">
          <span style="font-weight: 500; margin-right: 15px;">Pricing Strategy:</span>
          <el-switch 
            v-model="discountForm.isEnabled" 
            size="large"
            active-text="Discount Strategy"
            inactive-text="Regular Strategy"
            @change="onStrategyChange"
          />
        </div>
        
        <!-- Discount configuration (only show when discount is enabled) -->
        <div v-if="discountForm.isEnabled" style="margin-left: 20px; border-left: 3px solid #67c23a; padding-left: 15px;">
          <el-form :model="discountForm" label-width="140px" style="max-width: 500px;">
            <el-form-item label="Discount Rate">
              <div style="display: flex; align-items: center;">
                <el-input-number 
                  v-model="discountPercent"
                  :min="5"
                  :max="50"
                  :step="5"
                  :precision="0"
                  style="width: 120px"
                />
                <span style="margin-left: 10px; color: #67c23a; font-weight: 500;">
                  % off (Final price: {{ (100 - discountPercent) }}% of original)
                </span>
              </div>
            </el-form-item>
            
            <el-form-item label="Description">
              <el-input 
                v-model="discountForm.description" 
                placeholder="e.g., Spring Festival Special Offer" 
                style="width: 320px;"
              />
            </el-form-item>
          </el-form>
        </div>
        
        <!-- Action buttons -->
        <div style="margin-top: 20px; text-align: right;">
          <el-button @click="loadCurrentDiscount" style="margin-right: 10px;">Reset</el-button>
          <el-button type="primary" @click="saveStrategy" :loading="saving">
            Apply Changes
          </el-button>
        </div>
      </div>
      
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, computed } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import { Discount } from '@element-plus/icons-vue'

const currentDiscount = reactive({
  id: null,
  discountRate: 1.0,
  isEnabled: false,
  description: 'Regular pricing strategy'
})

const discountForm = reactive({
  isEnabled: false,
  description: ''
})

const discountPercent = ref(15) // Display as percentage
const saving = ref(false)

const actualDiscountRate = computed(() => {
  return (100 - discountPercent.value) / 100
})

const loadCurrentDiscount = async () => {
  try {
    const res = await request.get('/discount/current')
    if (res.data) {
      Object.assign(currentDiscount, res.data)
      discountForm.isEnabled = res.data.isEnabled
      discountForm.description = res.data.description || ''
      discountPercent.value = Math.round((1 - res.data.discountRate) * 100)
    }
  } catch (error) {
    ElMessage.error('Failed to load pricing strategy configuration')
  }
}

const onStrategyChange = (value) => {
  if (!value) {
    // When switching to regular strategy, clear description
    discountForm.description = 'Regular pricing strategy'
  } else {
    // When switching to discount strategy, set default description if empty
    if (!discountForm.description || discountForm.description === 'Regular pricing strategy') {
      discountForm.description = `${discountPercent.value}% off special offer`
    }
  }
}

const saveStrategy = async () => {
  saving.value = true
  
  try {
    const data = {
      discountRate: discountForm.isEnabled ? actualDiscountRate.value : 1.0,
      isEnabled: discountForm.isEnabled,
      description: discountForm.isEnabled ? discountForm.description : 'Regular pricing strategy'
    }
    
    await request.post('/discount/update', data)
    
    if (discountForm.isEnabled) {
      ElMessage.success(`Discount strategy applied! ${discountPercent.value}% off for all new orders.`)
    } else {
      ElMessage.success('Regular pricing strategy applied! All orders at original price.')
    }
    
    await loadCurrentDiscount()
  } catch (error) {
    ElMessage.error('Failed to apply strategy: ' + (error.response?.data?.msg || error.message))
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  loadCurrentDiscount()
})
</script>

<style scoped>
.card {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.strategy-status {
  background: #f8f9fa;
}
</style> 