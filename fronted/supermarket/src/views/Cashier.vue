<!-- filepath: c:\Users\gaoyuhang\Desktop\Supermarket Front\fronted\supermarket\src\views\Cashier.vue -->
<template>
  <div class="cashier">
    <div class="cashier-layout">
      <!-- 左侧商品扫描区域 -->
      <div class="left-panel">
        <div class="scan-section">
          <h3>🛒 商品扫描</h3>
          <div class="barcode-input">
            <el-input
              v-model="barcodeInput"
              placeholder="请扫描或输入商品条码"
              @keyup.enter="addProduct"
              ref="barcodeInputRef"
              size="large"
            >
              <template #prepend>
                <el-icon><Search /></el-icon>
              </template>
              <template #append>
                <el-button type="primary" @click="addProduct">添加</el-button>
              </template>
            </el-input>
          </div>
        </div>

        <!-- 购物车商品列表 -->
        <div class="cart-section">
          <h3>📋 购物车 ({{ cartItems.length }}件)</h3>
          <div class="cart-items">
            <div v-if="cartItems.length === 0" class="empty-cart">
              <p>购物车为空，请扫描商品条码</p>
            </div>
            <div v-else>
              <div v-for="(item, index) in cartItems" :key="index" class="cart-item">
                <div class="item-info">
                  <div class="item-name">{{ item.productName }}</div>
                  <div class="item-barcode">{{ item.barcode }}</div>
                  <div class="item-price">¥{{ item.price.toFixed(2) }}</div>
                </div>
                <div class="item-quantity">
                  <el-input-number
                    v-model="item.quantity"
                    :min="1"
                    :max="item.stockQuantity"
                    size="small"
                    @change="updateItemTotal(index)"
                  />
                </div>
                <div class="item-total">
                  ¥{{ item.subtotal.toFixed(2) }}
                </div>
                <div class="item-actions">
                  <el-button size="small" type="danger" @click="removeItem(index)">
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧结算区域 -->
      <div class="right-panel">
        <div class="checkout-section">
          <h3>💰 结算信息</h3>
          
          <!-- 金额显示 -->
          <div class="amount-display">
            <div class="total-amount">
              <label>商品总额：</label>
              <span class="amount">¥{{ totalAmount.toFixed(2) }}</span>
            </div>
            <div class="discount-section">
              <label>优惠金额：</label>
              <el-input-number
                v-model="discountAmount"
                :min="0"
                :max="totalAmount"
                :step="0.01"
                :precision="2"
                size="small"
                @change="calculateFinalAmount"
              />
            </div>
            <div class="final-amount">
              <label>应收金额：</label>
              <span class="amount final">¥{{ finalAmount.toFixed(2) }}</span>
            </div>
          </div>

          <!-- 会员信息 -->
          <div class="member-section">
            <h4>👤 会员信息</h4>
            <div class="member-input">
              <el-input
                v-model="memberPhone"
                placeholder="请输入会员手机号"
                @blur="searchMember"
                clearable
              >
                <template #prepend>📱</template>
              </el-input>
            </div>
            <div v-if="selectedMember" class="member-info">
              <p><strong>{{ selectedMember.memberName }}</strong></p>
              <p>等级: <el-tag :type="getMemberLevelColor(selectedMember.memberLevel)">
                {{ getMemberLevelText(selectedMember.memberLevel) }}
              </el-tag></p>
              <p>积分: {{ selectedMember.points }}</p>
            </div>
          </div>

          <!-- 支付方式 -->
          <div class="payment-section">
            <h4>💳 支付方式</h4>
            <el-radio-group v-model="paymentMethod" class="payment-options">
              <el-radio label="cash">现金</el-radio>
              <el-radio label="card">银行卡</el-radio>
              <el-radio label="alipay">支付宝</el-radio>
              <el-radio label="wechat">微信</el-radio>
            </el-radio-group>
          </div>

          <!-- 找零信息（现金支付时显示） -->
          <div v-if="paymentMethod === 'cash'" class="change-section">
            <div class="received-amount">
              <label>实收金额：</label>
              <el-input-number
                v-model="receivedAmount"
                :min="finalAmount"
                :step="0.01"
                :precision="2"
                @change="calculateChange"
              />
            </div>
            <div class="change-amount">
              <label>找零金额：</label>
              <span class="amount">¥{{ changeAmount.toFixed(2) }}</span>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="action-buttons">
            <el-button size="large" @click="clearCart">清空购物车</el-button>
            <el-button
              type="primary"
              size="large"
              @click="processPayment"
              :disabled="cartItems.length === 0"
              class="pay-button"
            >
              💰 结算 (¥{{ finalAmount.toFixed(2) }})
            </el-button>
          </div>
        </div>

        <!-- 快捷商品 -->
        <div class="quick-products">
          <h4>⚡ 快捷商品</h4>
          <div class="quick-items">
            <div
              v-for="product in quickProducts"
              :key="product.productId"
              class="quick-item"
              @click="addQuickProduct(product)"
            >
              <div class="quick-name">{{ product.productName }}</div>
              <div class="quick-price">¥{{ product.price.toFixed(2) }}</div>
            </div>
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
          <p>订单号：{{ currentOrder.orderNumber }}</p>
          <p>支付金额：¥{{ currentOrder.finalAmount }}</p>
          <p>支付方式：{{ getPaymentMethodText(currentOrder.paymentMethod) }}</p>
          <p v-if="currentOrder.paymentMethod === 'cash' && changeAmount > 0">
            找零：¥{{ changeAmount.toFixed(2) }}
          </p>
        </div>
      </div>
      <template #footer>
        <el-button @click="printReceipt">打印小票</el-button>
        <el-button type="primary" @click="nextOrder">下一单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Delete } from '@element-plus/icons-vue'

// 响应式数据
const barcodeInput = ref('')
const barcodeInputRef = ref()
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

// 模拟商品数据
const productDatabase = ref([
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
    stockQuantity: 156
  }
])

const quickProducts = ref([
  { productId: 1, productName: '购物袋', price: 0.50, barcode: 'BAG001' },
  { productId: 2, productName: '矿泉水', price: 1.50, barcode: 'WATER001' },
  { productId: 3, productName: '口香糖', price: 2.00, barcode: 'GUM001' }
])

// 方法
const addProduct = () => {
  if (!barcodeInput.value.trim()) return

  const product = productDatabase.value.find(p => p.barcode === barcodeInput.value.trim())
  
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

const getMemberLevelColor = (level) => {
  const colors = {
    bronze: '',
    silver: 'info',
    gold: 'warning',
    diamond: 'success'
  }
  return colors[level] || ''
}

const getMemberLevelText = (level) => {
  const texts = {
    bronze: '普通会员',
    silver: '银卡会员',
    gold: '金卡会员',
    diamond: '钻石会员'
  }
  return texts[level] || '普通会员'
}

const getPaymentMethodText = (method) => {
  const texts = {
    cash: '现金',
    card: '银行卡',
    alipay: '支付宝',
    wechat: '微信'
  }
  return texts[method] || '现金'
}

// 生命周期
onMounted(() => {
  // 自动聚焦到条码输入框
  nextTick(() => {
    barcodeInputRef.value?.focus()
  })
})
</script>

<style scoped>
.cashier {
  padding: 20px;
  height: calc(100vh - 80px);
}

.cashier-layout {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
  height: 100%;
}

.left-panel, .right-panel {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
}

.scan-section {
  margin-bottom: 20px;
}

.scan-section h3 {
  margin: 0 0 15px 0;
  color: #2c3e50;
}

.barcode-input {
  width: 100%;
}

.cart-section {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.cart-section h3 {
  margin: 0 0 15px 0;
  color: #2c3e50;
}

.cart-items {
  flex: 1;
  overflow-y: auto;
  border: 1px solid #e6e6e6;
  border-radius: 8px;
  padding: 10px;
}

.empty-cart {
  text-align: center;
  padding: 40px 20px;
  color: #999;
}

.cart-item {
  display: grid;
  grid-template-columns: 2fr 120px 100px 50px;
  gap: 15px;
  align-items: center;
  padding: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.cart-item:last-child {
  border-bottom: none;
}

.item-info {
  min-width: 0;
}

.item-name {
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 4px;
}

.item-barcode {
  font-size: 0.8rem;
  color: #999;
  margin-bottom: 2px;
}

.item-price {
  font-size: 0.9rem;
  color: #e67e22;
  font-weight: bold;
}

.item-total {
  text-align: right;
  font-weight: bold;
  color: #27ae60;
}

.checkout-section {
  flex: 1;
}

.checkout-section h3 {
  margin: 0 0 20px 0;
  color: #2c3e50;
}

.amount-display {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.total-amount, .discount-section, .final-amount {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.final-amount {
  margin-bottom: 0;
  padding-top: 10px;
  border-top: 2px solid #e67e22;
}

.amount {
  font-size: 1.2rem;
  font-weight: bold;
  color: #e67e22;
}

.amount.final {
  font-size: 1.5rem;
  color: #27ae60;
}

.member-section, .payment-section {
  margin-bottom: 20px;
}

.member-section h4, .payment-section h4 {
  margin: 0 0 10px 0;
  color: #2c3e50;
}

.member-input {
  margin-bottom: 10px;
}

.member-info {
  background: #e8f5e8;
  padding: 10px;
  border-radius: 6px;
  font-size: 0.9rem;
}

.member-info p {
  margin: 0 0 5px 0;
}

.payment-options {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.change-section {
  background: #fff3cd;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.received-amount, .change-amount {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.change-amount {
  margin-bottom: 0;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

.pay-button {
  flex: 1;
  font-size: 1.1rem;
  height: 50px;
}

.quick-products {
  margin-top: 20px;
}

.quick-products h4 {
  margin: 0 0 15px 0;
  color: #2c3e50;
}

.quick-items {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(100px, 1fr));
  gap: 10px;
}

.quick-item {
  background: #f8f9fa;
  padding: 12px;
  border-radius: 6px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.quick-item:hover {
  background: #e9ecef;
  border-color: #3498db;
}

.quick-name {
  font-size: 0.8rem;
  margin-bottom: 4px;
  color: #2c3e50;
}

.quick-price {
  font-size: 0.9rem;
  font-weight: bold;
  color: #e67e22;
}

.payment-success {
  text-align: center;
  padding: 20px;
}

.success-icon {
  font-size: 3rem;
  margin-bottom: 15px;
}

.payment-success h3 {
  color: #27ae60;
  margin-bottom: 20px;
}

.payment-details {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  text-align: left;
}

.payment-details p {
  margin: 0 0 8px 0;
  display: flex;
  justify-content: space-between;
}

@media (max-width: 1024px) {
  .cashier-layout {
    grid-template-columns: 1fr;
    gap: 15px;
  }
  
  .cart-item {
    grid-template-columns: 1fr;
    gap: 10px;
  }
  
  .item-total {
    text-align: left;
  }
}
</style>