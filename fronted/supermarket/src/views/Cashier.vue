<!-- filepath: c:\Users\gaoyuhang\Desktop\Supermarket Front\fronted\supermarket\src\views\Cashier.vue -->
<template>
  <div class="cashier">
    <div class="cashier-layout">
      <!-- 左侧商品扫描和商品信息区域 -->
      <div class="left-panel">
        <!-- 扫描区域 -->
        <div class="scan-section">
          <div class="scan-header">
            <h3>🛒 商品扫描</h3>
            <div class="scan-stats">
              <span class="scan-count">已扫描: {{ cartItems.length }}件</span>
              <span class="total-preview">合计: ¥{{ totalAmount.toFixed(2) }}</span>
            </div>
          </div>
          <div class="barcode-input">
            <el-input
              v-model="barcodeInput"
              placeholder="请扫描或输入商品条码/商品名称搜索"
              @keyup.enter="addProduct"
              @input="handleSearchInput"
              ref="barcodeInputRef"
              size="large"
              clearable
            >
              <template #prepend>
                <el-icon><Search /></el-icon>
              </template>
              <template #append>
                <el-button type="primary" @click="addProduct">添加</el-button>
              </template>
            </el-input>
          </div>
          
          <!-- 搜索建议 -->
          <div v-if="searchSuggestions.length > 0" class="search-suggestions">
            <div class="suggestions-header">
              <span>💡 搜索建议 ({{ searchSuggestions.length }}个结果)</span>
            </div>
            <div class="suggestions-list">
              <div 
                v-for="product in searchSuggestions" 
                :key="product.productId"
                class="suggestion-item"
                @click="selectSuggestion(product)"
              >
                <div class="suggestion-info">
                  <div class="suggestion-name">{{ product.productName }}</div>
                  <div class="suggestion-details">
                    <span class="suggestion-barcode">{{ product.barcode }}</span>
                    <span class="suggestion-price">¥{{ product.price.toFixed(2) }}</span>
                  </div>
                </div>
                <div class="suggestion-stock">
                  <el-tag :type="getStockStatus(product.stockQuantity)">
                    库存: {{ product.stockQuantity }}
                  </el-tag>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 所有商品信息展示 -->
        <div class="products-info-section">
          <div class="products-header">
            <h3>📦 商品信息</h3>
            <div class="products-actions">
              <el-input
                v-model="productSearchKeyword"
                placeholder="搜索商品..."
                @input="filterProducts"
                size="small"
                clearable
                style="width: 200px;"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
              <el-button @click="refreshProducts" size="small">
                <el-icon><Refresh /></el-icon>
              </el-button>
            </div>
          </div>
          
          <div class="products-grid">
            <div 
              v-for="product in filteredProducts" 
              :key="product.productId"
              class="product-card"
              @click="addProductById(product.productId)"
              :class="{ 'low-stock': product.stockQuantity <= 10 }"
            >
              <div class="product-header">
                <div class="product-name">{{ product.productName }}</div>
                <el-tag 
                  :type="getStockStatus(product.stockQuantity)" 
                  size="small"
                >
                  {{ product.stockQuantity }}
                </el-tag>
              </div>
              <div class="product-details">
                <div class="product-barcode">{{ product.barcode }}</div>
                <div class="product-price">¥{{ product.price.toFixed(2) }}</div>
              </div>
              <div class="product-actions">
                <el-button size="small" type="primary" @click.stop="addProductById(product.productId)">
                  <el-icon><Plus /></el-icon>
                  加入购物车
                </el-button>
              </div>
            </div>
          </div>
          
          <!-- 商品分页 -->
          <div class="products-pagination">
            <el-pagination
              v-model:current-page="productPagination.current"
              v-model:page-size="productPagination.size"
              :total="productPagination.total"
              :page-sizes="[12, 24, 48]"
              layout="sizes, prev, pager, next, total"
              @size-change="loadProducts"
              @current-change="loadProducts"
              small
            />
          </div>
        </div>
      </div>

      <!-- 右侧购物车和结算区域 -->
      <div class="right-panel">
        <!-- 购物车 -->
        <div class="cart-section">
          <div class="cart-header">
            <h3>🛍️ 购物车</h3>
            <div class="cart-summary">
              <span class="cart-count">{{ cartItems.length }}件商品</span>
              <el-button 
                v-if="cartItems.length > 0" 
                size="small" 
                type="danger" 
                @click="clearCart"
                plain
              >
                清空
              </el-button>
            </div>
          </div>
          
          <div class="cart-items">
            <div v-if="cartItems.length === 0" class="empty-cart">
              <div class="empty-icon">🛒</div>
              <p>购物车为空</p>
              <p class="empty-tip">扫描条码或点击商品添加</p>
            </div>
            <div v-else class="cart-list">
              <div v-for="(item, index) in cartItems" :key="`${item.productId}-${index}`" class="cart-item">
                <div class="item-info">
                  <div class="item-name" :title="item.productName">{{ item.productName }}</div>
                  <div class="item-meta">
                    <span class="item-barcode">{{ item.barcode }}</span>
                    <span class="item-unit-price">¥{{ item.price.toFixed(2) }}/件</span>
                  </div>
                </div>
                <div class="item-controls">
                  <div class="quantity-controls">
                    <el-button 
                      size="small" 
                      @click="decreaseQuantity(index)"
                      :disabled="item.quantity <= 1"
                      circle
                    >
                      <el-icon><Minus /></el-icon>
                    </el-button>
                    <span class="quantity-display">{{ item.quantity }}</span>
                    <el-button 
                      size="small" 
                      @click="increaseQuantity(index)"
                      :disabled="item.quantity >= item.stockQuantity"
                      circle
                    >
                      <el-icon><Plus /></el-icon>
                    </el-button>
                  </div>
                  <div class="item-total">¥{{ item.subtotal.toFixed(2) }}</div>
                  <el-button 
                    size="small" 
                    type="danger" 
                    @click="removeItem(index)"
                    circle
                  >
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 结算区域 -->
        <div class="checkout-section">
          <h3>💰 结算信息</h3>
          
          <!-- 金额显示 -->
          <div class="amount-display">
            <div class="amount-row">
              <span class="amount-label">商品总额：</span>
              <span class="amount-value">¥{{ totalAmount.toFixed(2) }}</span>
            </div>
            <div class="amount-row">
              <span class="amount-label">优惠金额：</span>
              <el-input-number
                v-model="discountAmount"
                :min="0"
                :max="totalAmount"
                :step="0.01"
                :precision="2"
                size="small"
                @change="calculateFinalAmount"
                style="width: 120px;"
              />
            </div>
            <div class="amount-row final-row">
              <span class="amount-label">应收金额：</span>
              <span class="amount-value final-amount">¥{{ finalAmount.toFixed(2) }}</span>
            </div>
          </div>

          <!-- 会员信息 -->
          <div class="member-section">
            <h4>👤 会员信息</h4>
            <div class="member-input">
              <el-input
                v-model="memberPhone"
                placeholder="输入手机号查找会员"
                @blur="searchMember"
                @keyup.enter="searchMember"
                clearable
                size="small"
              >
                <template #prepend>📱</template>
                <template #append>
                  <el-button @click="searchMember" size="small">查找</el-button>
                </template>
              </el-input>
            </div>
            <div v-if="selectedMember" class="member-card">
              <div class="member-info">
                <div class="member-name">{{ selectedMember.memberName }}</div>
                <div class="member-details">
                  <el-tag :type="getMemberLevelColor(selectedMember.memberLevel)" size="small">
                    {{ getMemberLevelText(selectedMember.memberLevel) }}
                  </el-tag>
                  <span class="member-points">积分: {{ selectedMember.points }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 支付方式 -->
          <div class="payment-section">
            <h4>💳 支付方式</h4>
            <el-radio-group v-model="paymentMethod" class="payment-options">
              <el-radio-button label="cash">💵 现金</el-radio-button>
              <el-radio-button label="card">💳 银行卡</el-radio-button>
              <el-radio-button label="alipay">📱 支付宝</el-radio-button>
              <el-radio-button label="wechat">💚 微信</el-radio-button>
            </el-radio-group>
          </div>

          <!-- 找零信息（现金支付时显示） -->
          <div v-if="paymentMethod === 'cash'" class="change-section">
            <div class="received-input">
              <span class="amount-label">实收金额：</span>
              <el-input-number
                v-model="receivedAmount"
                :min="finalAmount"
                :step="0.01"
                :precision="2"
                @change="calculateChange"
                size="small"
                style="width: 120px;"
              />
            </div>
            <div class="change-display">
              <span class="amount-label">找零金额：</span>
              <span class="change-amount" :class="{ 'highlight': changeAmount > 0 }">
                ¥{{ changeAmount.toFixed(2) }}
              </span>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="action-buttons">
            <el-button 
              size="large" 
              @click="clearCart"
              :disabled="cartItems.length === 0"
            >
              🗑️ 清空
            </el-button>
            <el-button
              type="primary"
              size="large"
              @click="processPayment"
              :disabled="cartItems.length === 0"
              class="pay-button"
            >
              💰 结算 ¥{{ finalAmount.toFixed(2) }}
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 支付成功对话框 -->
    <el-dialog v-model="showPaymentSuccess" title="支付成功" width="500px" :show-close="false">
      <div class="payment-success">
        <div class="success-icon">✅</div>
        <h3>支付成功！</h3>
        <div class="payment-details">
          <p><span>订单号：</span><span>{{ currentOrder.orderNumber }}</span></p>
          <p><span>支付金额：</span><span>¥{{ currentOrder.finalAmount }}</span></p>
          <p><span>支付方式：</span><span>{{ getPaymentMethodText(currentOrder.paymentMethod) }}</span></p>
          <p v-if="currentOrder.paymentMethod === 'cash' && changeAmount > 0">
            <span>找零：</span><span class="change-highlight">¥{{ changeAmount.toFixed(2) }}</span>
          </p>
        </div>
      </div>
      <template #footer>
        <el-button @click="printReceipt">🖨️ 打印小票</el-button>
        <el-button type="primary" @click="nextOrder">➡️ 下一单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive, onMounted, nextTick, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Delete, Plus, Minus, Refresh } from '@element-plus/icons-vue'
import { productApi } from '../api/product'

// 响应式数据
const barcodeInput = ref('')
const barcodeInputRef = ref()
const productSearchKeyword = ref('')
const searchSuggestions = ref([])

// 购物车商品列表
interface CartItem {
  productId: number
  productName: string
  barcode: string
  price: number
  stockQuantity: number
  quantity: number
  subtotal: number
}

const cartItems = ref<CartItem[]>([])
const discountAmount = ref(0)
const memberPhone = ref('')
interface Member {
  memberId: number
  memberName: string
  phoneNumber: string
  memberLevel: string
  points: number
}
const selectedMember = ref<Member | null>(null)
const paymentMethod = ref('cash')
const receivedAmount = ref(0)
const showPaymentSuccess = ref(false)
const currentOrder = ref<{ orderNumber?: string; finalAmount?: number; paymentMethod?: string }>({})

// 新增商品相关数据
const allProducts = ref([])
const filteredProducts = ref([])
const productPagination = reactive({
  current: 1,
  size: 12,
  total: 0
})

// 计算属性
const totalAmount = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + item.subtotal, 0)
})

const finalAmount = computed(() => {
  return Math.max(0, totalAmount.value - discountAmount.value)
})

const changeAmount = computed(() => {
  return Math.max(0, receivedAmount.value - finalAmount.value)
})

// 方法
const addProduct = () => {
  if (!barcodeInput.value.trim()) return

  const product = allProducts.value.find(p => p.barcode === barcodeInput.value.trim())
  
  if (!product) {
    ElMessage.error('未找到该商品，请检查条码是否正确')
    return
  }

  // 检查购物车中是否已有该商品
  const existingItem = cartItems.value.find(item => item.barcode === product.barcode)
  
  if (existingItem) {
    if (existingItem.quantity < product.stockQuantity) {
      existingItem.quantity++
      existingItem.subtotal = existingItem.price * existingItem.quantity
    } else {
      ElMessage.warning('商品库存不足')
    }
  } else {
    cartItems.value.push({
      ...product,
      quantity: 1,
      subtotal: product.price
    })
  }

  barcodeInput.value = ''
  calculateFinalAmount()
  
  // 重新聚焦到输入框
  nextTick(() => {
    barcodeInputRef.value?.focus()
  })
}

interface QuickProduct {
  productId: number
  productName: string
  price: number
  barcode: string
}

const addQuickProduct = (product: QuickProduct) => {
  const cartItem = {
    ...product,
    quantity: 1,
    subtotal: product.price,
    stockQuantity: 999 // 快捷商品通常库存充足
  }
  
  cartItems.value.push(cartItem)
  calculateFinalAmount()
}

const updateItemTotal = (index: number) => {
  const item = cartItems.value[index]
  item.subtotal = item.price * item.quantity
  calculateFinalAmount()
}

const removeItem = (index) => {
  cartItems.value.splice(index, 1)
  calculateFinalAmount()
}

const calculateFinalAmount = () => {
  receivedAmount.value = finalAmount.value
}

const calculateChange = () => {
  // 找零计算已在计算属性中处理
}

const searchMember = () => {
  if (!memberPhone.value) {
    selectedMember.value = null
    return
  }

  // 模拟会员查询
  const mockMembers = [
    {
      memberId: 1001,
      memberName: '张三',
      phoneNumber: '13812345678',
      memberLevel: 'gold',
      points: 2580
    }
  ]

  const member = mockMembers.find(m => m.phoneNumber === memberPhone.value)
  
  if (member) {
    selectedMember.value = member
    ElMessage.success(`会员 ${member.memberName} 信息加载成功`)
  } else {
    selectedMember.value = null
    ElMessage.warning('未找到该会员信息')
  }
}

const processPayment = async () => {
  if (cartItems.value.length === 0) {
    ElMessage.error('购物车为空，无法结算')
    return
  }

  if (paymentMethod.value === 'cash' && receivedAmount.value < finalAmount.value) {
    ElMessage.error('实收金额不足')
    return
  }

  try {
    // 构建结算数据
    const checkoutData = {
      items: cartItems.value.map(item => ({
        productId: item.productId,
        productName: item.productName,
        barcode: item.barcode,
        price: Number(item.price),
        quantity: Number(item.quantity),
        subtotal: Number(item.subtotal)
      })),
      totalAmount: Number(totalAmount.value),
      discountAmount: Number(discountAmount.value),
      finalAmount: Number(finalAmount.value),
      paymentMethod: paymentMethod.value,
      memberId: selectedMember.value?.memberId || null,
      cashierId: 1 // 假设当前收银员ID为1
    }

    console.log('结算数据:', checkoutData)

    // 模拟API调用
    const response = await mockCheckout(checkoutData)
    
    if (response.success) {
      currentOrder.value = {
        orderNumber: response.orderNumber,
        finalAmount: finalAmount.value,
        paymentMethod: paymentMethod.value
      }
      
      showPaymentSuccess.value = true
      ElMessage.success('支付成功！')
    } else {
      ElMessage.error('支付失败：' + response.message)
    }
  } catch (error) {
    console.error('支付错误:', error)
    ElMessage.error('支付失败，请重试')
  }
}

const mockCheckout = (data) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({
        success: true,
        orderNumber: 'ORD' + Date.now(),
        message: '支付成功'
      })
    }, 1000)
  })
}

const clearCart = () => {
  ElMessageBox.confirm('确定要清空购物车吗？', '确认操作', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    cartItems.value = []
    discountAmount.value = 0
    selectedMember.value = null
    memberPhone.value = ''
    receivedAmount.value = 0
    ElMessage.success('购物车已清空')
  }).catch(() => {
    // 取消操作
  })
}

const printReceipt = () => {
  ElMessage.info('小票打印功能开发中...')
}

const nextOrder = () => {
  showPaymentSuccess.value = false
  clearCart()
  
  // 重新聚焦到条码输入框
  nextTick(() => {
    barcodeInputRef.value?.focus()
  })
}

const handleSearchInput = async () => {
  if (barcodeInput.value.length >= 2) {
    // 搜索商品建议
    searchSuggestions.value = allProducts.value.filter(product => 
      product.productName.toLowerCase().includes(barcodeInput.value.toLowerCase()) ||
      product.barcode.includes(barcodeInput.value)
    ).slice(0, 5)
  } else {
    searchSuggestions.value = []
  }
}

const selectSuggestion = (product) => {
  barcodeInput.value = product.barcode
  searchSuggestions.value = []
  addProduct()
}

const loadProducts = async () => {
  try {
    console.log('🔍 加载商品数据...')
    const params = {
      current: productPagination.current,
      size: productPagination.size,
      productName: productSearchKeyword.value || undefined
    }
    
    const response = await productApi.getPage(params)
    
    if (response && response.success) {
      allProducts.value = response.data?.records || []
      productPagination.total = response.data?.total || 0
      filterProducts()
      console.log(`✅ 加载了 ${allProducts.value.length} 个商品`)
    } else {
      throw new Error('API返回失败')
    }
  } catch (error) {
    console.error('❌ 加载商品失败:', error)
    // 使用模拟数据
    allProducts.value = [
      {
        productId: 1,
        productName: '可口可乐500ml',
        barcode: '6901028000001',
        price: 3.50,
        stockQuantity: 85
      },
      {
        productId: 2,
        productName: '农夫山泉550ml', 
        barcode: '6902148000002',
        price: 2.50,
        stockQuantity: 120
      },
      {
        productId: 3,
        productName: '康师傅方便面',
        barcode: '6901326000003', 
        price: 4.50,
        stockQuantity: 5
      },
      {
        productId: 4,
        productName: '牙刷',
        barcode: '6901028000004',
        price: 8.90,
        stockQuantity: 0
      }
    ]
    productPagination.total = allProducts.value.length
    filterProducts()
  }
}

const filterProducts = () => {
  if (!productSearchKeyword.value) {
    filteredProducts.value = allProducts.value
  } else {
    filteredProducts.value = allProducts.value.filter(product =>
      product.productName.toLowerCase().includes(productSearchKeyword.value.toLowerCase()) ||
      product.barcode.includes(productSearchKeyword.value)
    )
  }
}

const refreshProducts = () => {
  productSearchKeyword.value = ''
  productPagination.current = 1
  loadProducts()
}

const addProductById = (productId) => {
  const product = allProducts.value.find(p => p.productId === productId)
  if (product) {
    if (product.stockQuantity <= 0) {
      ElMessage.error(`${product.productName} 库存不足`)
      return
    }
    
    const existingItem = cartItems.value.find(item => item.productId === product.productId)
    
    if (existingItem) {
      if (existingItem.quantity < product.stockQuantity) {
        increaseQuantity(cartItems.value.indexOf(existingItem))
      } else {
        ElMessage.warning(`${product.productName} 库存不足`)
      }
    } else {
      cartItems.value.push({
        ...product,
        quantity: 1,
        subtotal: product.price
      })
      ElMessage.success(`${product.productName} 已加入购物车`)
    }
    
    calculateFinalAmount()
  }
}

const increaseQuantity = (index) => {
  const item = cartItems.value[index]
  if (item.quantity < item.stockQuantity) {
    item.quantity++
    item.subtotal = item.price * item.quantity
    calculateFinalAmount()
  }
}

const decreaseQuantity = (index) => {
  const item = cartItems.value[index]
  if (item.quantity > 1) {
    item.quantity--
    item.subtotal = item.price * item.quantity
    calculateFinalAmount()
  }
}

const getStockStatus = (stock) => {
  if (stock <= 0) return 'danger'
  if (stock <= 10) return 'warning'
  return 'success'
}

const getMemberLevelColor = (level: string) => {
  const types: Record<string, string> = {
    'bronze': '',
    'silver': 'info', 
    'gold': 'warning',
    'diamond': 'success'
  }
  return types[level] || ''
}

const getMemberLevelText = (level: string) => {
  const texts: Record<string, string> = {
    'bronze': '普通会员',
    'silver': '银卡会员',
    'gold': '金卡会员', 
    'diamond': '钻石会员'
  }
  return texts[level] || level
}

const getPaymentMethodText = (method: string) => {
  const texts: Record<string, string> = {
    'cash': '现金',
    'card': '银行卡',
    'alipay': '支付宝',
    'wechat': '微信支付'
  }
  return texts[method] || method
}

// 生命周期
onMounted(() => {
  loadProducts()
  nextTick(() => {
    barcodeInputRef.value?.focus()
  })
})

// 监听搜索关键词变化
watch(productSearchKeyword, () => {
  filterProducts()
})
</script>

<style scoped>
.cashier {
  padding: 15px;
  height: calc(100vh - 30px);
  overflow: hidden;
}

.cashier-layout {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 15px;
  height: 100%;
}

.left-panel, .right-panel {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.scan-section {
  margin-bottom: 20px;
}

.scan-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.scan-header h3 {
  margin: 0;
  color: #2c3e50;
}

.scan-stats {
  display: flex;
  gap: 15px;
  font-size: 0.9rem;
}

.scan-count {
  color: #666;
}

.total-preview {
  color: #e67e22;
  font-weight: bold;
}

.search-suggestions {
  margin-top: 10px;
  border: 1px solid #e6e6e6;
  border-radius: 8px;
  background: white;
  max-height: 200px;
  overflow-y: auto;
}

.suggestions-header {
  padding: 8px 12px;
  background: #f8f9fa;
  border-bottom: 1px solid #e6e6e6;
  font-size: 0.9rem;
  color: #666;
}

.suggestion-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 12px;
  cursor: pointer;
  transition: background 0.2s;
}

.suggestion-item:hover {
  background: #f0f8ff;
}

.suggestion-info {
  flex: 1;
}

.suggestion-name {
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 2px;
}

.suggestion-details {
  display: flex;
  gap: 10px;
  font-size: 0.8rem;
  color: #666;
}

.suggestion-price {
  color: #e67e22;
  font-weight: bold;
}

.products-info-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.products-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.products-header h3 {
  margin: 0;
  color: #2c3e50;
}

.products-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 10px;
  flex: 1;
  overflow-y: auto;
  padding: 5px 0;
}

.product-card {
  border: 1px solid #e6e6e6;
  border-radius: 8px;
  padding: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: white;
}

.product-card:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
  transform: translateY(-2px);
}

.product-card.low-stock {
  border-color: #f39c12;
  background: #fff8e1;
}

.product-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.product-name {
  font-weight: bold;
  color: #2c3e50;
  font-size: 0.9rem;
  flex: 1;
  margin-right: 8px;
}

.product-details {
  margin-bottom: 10px;
}

.product-barcode {
  font-size: 0.8rem;
  color: #666;
  margin-bottom: 4px;
}

.product-price {
  font-size: 1rem;
  color: #e67e22;
  font-weight: bold;
}

.product-actions {
  text-align: center;
}

.products-pagination {
  margin-top: 15px;
  text-align: center;
}

.cart-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  margin-bottom: 20px;
  overflow: hidden;
}

.cart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.cart-header h3 {
  margin: 0;
  color: #2c3e50;
}

.cart-summary {
  display: flex;
  align-items: center;
  gap: 10px;
}

.cart-count {
  font-size: 0.9rem;
  color: #666;
}

.cart-items {
  flex: 1;
  overflow: hidden;
  border: 1px solid #e6e6e6;
  border-radius: 8px;
}

.empty-cart {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: #999;
}

.empty-icon {
  font-size: 3rem;
  margin-bottom: 10px;
}

.empty-tip {
  font-size: 0.9rem;
  margin-top: 5px;
}

.cart-list {
  height: 100%;
  overflow-y: auto;
  padding: 10px;
}

.cart-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  border-bottom: 1px solid #f0f0f0;
  transition: background 0.2s;
}

.cart-item:hover {
  background: #f8f9fa;
}

.cart-item:last-child {
  border-bottom: none;
}

.item-info {
  flex: 1;
  min-width: 0;
}

.item-name {
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-meta {
  display: flex;
  gap: 10px;
  font-size: 0.8rem;
  color: #666;
}

.item-controls {
  display: flex;
  align-items: center;
  gap: 10px;
}

.quantity-controls {
  display: flex;
  align-items: center;
  gap: 8px;
}

.quantity-display {
  min-width: 30px;
  text-align: center;
  font-weight: bold;
}

.item-total {
  min-width: 80px;
  text-align: right;
  font-weight: bold;
  color: #27ae60;
}

.checkout-section h3 {
  margin: 0 0 20px 0;
  color: #2c3e50;
}

.amount-display {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.amount-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.amount-row:last-child {
  margin-bottom: 0;
}

.final-row {
  padding-top: 10px;
  border-top: 2px solid #e67e22;
  font-size: 1.1rem;
}

.amount-label {
  font-weight: 500;
  color: #2c3e50;
}

.amount-value {
  font-weight: bold;
  color: #e67e22;
}

.final-amount {
  font-size: 1.3rem;
  color: #27ae60;
}

.member-section, .payment-section {
  margin-bottom: 20px;
}

.member-section h4, .payment-section h4 {
  margin: 0 0 10px 0;
  color: #2c3e50;
  font-size: 1rem;
}

.member-card {
  background: #e8f5e8;
  padding: 10px;
  border-radius: 6px;
  margin-top: 10px;
}

.member-name {
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 5px;
}

.member-details {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.9rem;
}

.member-points {
  color: #666;
}

.payment-options {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.change-section {
  background: #fff3cd;
  padding: 12px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.received-input, .change-display {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.change-display {
  margin-bottom: 0;
}

.change-amount {
  font-weight: bold;
  color: #e67e22;
}

.change-amount.highlight {
  color: #27ae60;
  font-size: 1.1rem;
}

.action-buttons {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 10px;
}

.pay-button {
  font-size: 1.1rem;
  height: 50px;
  font-weight: bold;
}

.payment-success {
  text-align: center;
  padding: 20px;
}

.success-icon {
  font-size: 4rem;
  margin-bottom: 20px;
}

.payment-success h3 {
  color: #27ae60;
  margin-bottom: 25px;
  font-size: 1.5rem;
}

.payment-details {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  text-align: left;
}

.payment-details p {
  display: flex;
  justify-content: space-between;
  margin: 0 0 10px 0;
  padding: 5px 0;
}

.change-highlight {
  color: #27ae60;
  font-weight: bold;
  font-size: 1.1rem;
}

@media (max-width: 1200px) {
  .cashier-layout {
    grid-template-columns: 1fr;
    gap: 10px;
  }
  
  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  }
  
  .cart-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .item-controls {
    width: 100%;
    justify-content: space-between;
  }
}

@media (max-width: 768px) {
  .cashier {
    padding: 10px;
  }
  
  .scan-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .products-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .products-grid {
    grid-template-columns: 1fr;
  }
  
  .action-buttons {
    grid-template-columns: 1fr;
  }
}
</style>