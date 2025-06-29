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
              <span class="amount-value discount-value">-¥{{ discountAmount.toFixed(2) }}</span>
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
                maxlength="11"
              >
                <template #prepend>📱</template>
                <template #append>
                  <el-button @click="searchMember" size="small" type="primary">查找</el-button>
                </template>
              </el-input>
            </div>
            
            <!-- 会员信息卡片 -->
            <div v-if="selectedMember" class="member-card">
              <div class="member-header">
                <div class="member-name">{{ selectedMember.memberName }}</div>
                <el-button @click="clearMemberInfo" size="small" type="text" class="clear-member">
                  <el-icon><Close /></el-icon>
                </el-button>
              </div>
              <div class="member-details">
                <div class="member-level">
                  <el-tag :type="getMemberLevelColor(selectedMember.memberLevel)" size="small">
                    {{ selectedMember.memberLevelName }}
                  </el-tag>
                  <span class="member-phone">{{ selectedMember.phone }}</span>
                </div>
                <div class="member-benefits">
                  <span class="member-points">积分: {{ selectedMember.points }}</span>
                  <span v-if="selectedMember.discountRate > 0" class="discount-info">
                    享受{{ (selectedMember.discountRate * 100) }}%折扣
                  </span>
                </div>
              </div>
              
              <!-- 会员折扣详情 -->
              <div v-if="memberDiscountInfo && discountAmount > 0" class="member-discount">
                <div class="discount-header">💰 会员优惠详情</div>
                <div class="discount-details">
                  <div class="discount-row">
                    <span>商品总额：</span>
                    <span>¥{{ totalAmount.toFixed(2) }}</span>
                  </div>
                  <div class="discount-row highlight">
                    <span>会员优惠：</span>
                    <span>-¥{{ discountAmount.toFixed(2) }}</span>
                  </div>
                  <div class="discount-row">
                    <span>节省金额：</span>
                    <span class="saved-amount">¥{{ discountAmount.toFixed(2) }}</span>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- 无会员提示 -->
            <div v-else class="no-member-tip">
              <span>💡 输入会员手机号享受折扣优惠</span>
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
        </div>
      </div>
      <template #footer>
        <el-button type="primary" @click="nextOrder">➡️ 下一单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive, onMounted, nextTick, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Delete, Plus, Minus, Refresh, Close } from '@element-plus/icons-vue'
import { productApi } from '../api/product'
import { memberApi } from '../api/member'
import { saleApi } from '../api/sale'

// 响应式数据
const barcodeInput = ref('')
const barcodeInputRef = ref()
const productSearchKeyword = ref('')
const searchSuggestions = ref([])

// 商品数据
const allProducts = ref([])
const filteredProducts = ref([])
const productPagination = reactive({
  current: 1,
  size: 12,
  total: 0
})

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

// 支付相关 - 移除找零相关变量
const paymentMethod = ref('cash')
const showPaymentSuccess = ref(false)
const currentOrder = ref({
  orderNumber: '',
  finalAmount: 0,
  paymentMethod: ''
})

// 会员相关数据
interface Member {
  memberId: number
  memberName: string
  phone: string
  memberLevel: string
  points: number
  discountRate?: number
  memberLevelName?: string
}

const memberPhone = ref('')
const selectedMember = ref<Member | null>(null)
const memberDiscountInfo = ref<any>(null)

// 计算属性
const totalAmount = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + item.subtotal, 0)
})

const finalAmount = computed(() => {
  return Math.max(0, totalAmount.value - discountAmount.value)
})

// 加载商品数据
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

// 方法
const addProduct = () => {
  if (!barcodeInput.value.trim()) return

  const product = allProducts.value.find(p => p.barcode === barcodeInput.value.trim())
  
  if (!product) {
    ElMessage.error('未找到该商品，请检查条码是否正确')
    return
  }

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
  
  nextTick(() => {
    barcodeInputRef.value?.focus()
  })
}

const removeItem = (index) => {
  cartItems.value.splice(index, 1)
  calculateFinalAmount()
}

const processPayment = async () => {
  if (cartItems.value.length === 0) {
    ElMessage.error('购物车为空，无法结算')
    return
  }

  try {
    console.log('🛒 开始处理支付...')
    
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
      cashierId: 1
    }

    console.log('📤 发送结算数据:', checkoutData)

    const response = await saleApi.checkout(checkoutData)
    console.log('📥 收到结算响应:', response)
    
    if (response && response.success) {
      currentOrder.value = {
        orderNumber: response.data.saleNumber,
        finalAmount: finalAmount.value,
        paymentMethod: paymentMethod.value
      }
      
      showPaymentSuccess.value = true
      ElMessage.success('支付成功！订单已保存')
      
      console.log('🎉 支付完成，订单号:', response.data.saleNumber)
    } else {
      throw new Error(response?.message || '结算失败')
    }
  } catch (error: any) {
    console.error('❌ 支付失败:', error)
    
    if (error.response?.status === 500) {
      ElMessage.error('服务器错误，支付失败，请重试')
    } else if (error.response?.status === 404) {
      ElMessage.error('结算接口不存在，请检查后端服务')
    } else {
      console.log('⚠️ 后端API失败，使用模拟结算')
      const mockResponse = await mockCheckout(checkoutData)
      
      if (mockResponse.success) {
        currentOrder.value = {
          orderNumber: mockResponse.orderNumber,
          finalAmount: finalAmount.value,
          paymentMethod: paymentMethod.value
        }
        
        showPaymentSuccess.value = true
        ElMessage.warning('支付成功！(模拟模式，数据未保存到数据库)')
      }
    }
  }
}

// 模拟结算接口
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
    clearMemberInfo()
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
  clearMemberInfo()
  
  nextTick(() => {
    barcodeInputRef.value?.focus()
  })
}

const handleSearchInput = async () => {
  if (barcodeInput.value.length >= 2) {
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

// 会员查找功能
const searchMember = async () => {
  if (!memberPhone.value || memberPhone.value.trim().length < 11) {
    selectedMember.value = null
    memberDiscountInfo.value = null
    ElMessage.warning('请输入正确的11位手机号')
    return
  }

  try {
    console.log('📱 开始查找会员:', memberPhone.value)
    
    const response = await memberApi.getByPhone(memberPhone.value.trim())
    console.log('📱 会员查询响应:', response)
    
    if (response && response.success && response.data) {
      const member = response.data
      selectedMember.value = {
        memberId: member.memberId,
        memberName: member.memberName,
        phone: member.phone,
        memberLevel: member.memberLevel,
        points: member.points,
        discountRate: getDiscountRateByLevel(member.memberLevel),
        memberLevelName: getMemberLevelText(member.memberLevel)
      }
      
      ElMessage.success(`会员 ${member.memberName} 查找成功！`)
      
      await calculateMemberDiscount()
      
    } else {
      selectedMember.value = null
      memberDiscountInfo.value = null
      ElMessage.warning('未找到该手机号对应的会员，请检查手机号是否正确')
    }
  } catch (error: any) {
    console.error('❌ 会员查询失败:', error)
    selectedMember.value = null
    memberDiscountInfo.value = null
    
    if (error.response) {
      const status = error.response.status
      if (status === 404) {
        ElMessage.error('未找到该手机号对应的会员')
      } else if (status === 500) {
        ElMessage.error('服务器错误，请稍后重试')
      } else {
        ElMessage.error(`查询失败 (${status}): 请检查网络连接`)
      }
    } else {
      ElMessage.error('网络连接失败，请检查后端服务是否启动')
    }
  }
}

// 根据会员等级获取折扣率
const getDiscountRateByLevel = (level: string): number => {
  const discountRates: Record<string, number> = {
    'diamond': 0.15,
    'gold': 0.10,
    'silver': 0.05,
    'bronze': 0.0
  }
  return discountRates[level] || 0.0
}

// 计算会员折扣
const calculateMemberDiscount = async () => {
  if (!selectedMember.value || totalAmount.value <= 0) {
    discountAmount.value = 0
    return
  }

  try {
    console.log('💰 开始计算会员折扣...')
    
    const response = await memberApi.calculateDiscount(
      selectedMember.value.memberId,
      totalAmount.value
    )
    
    if (response && response.success && response.data) {
      discountAmount.value = response.data.discountAmount || 0
      
      ElMessage.success(
        `${selectedMember.value.memberLevelName}享受${(response.data.discountPercentage || 0)}%折扣，优惠¥${discountAmount.value.toFixed(2)}`
      )
      
      console.log('✅ 会员折扣计算完成，优惠金额:', discountAmount.value)
    } else {
      throw new Error('后端折扣计算失败')
    }
  } catch (error: any) {
    console.error('❌ 后端折扣计算失败，使用前端计算:', error)
    
    const discountRate = selectedMember.value.discountRate || 0
    const calculatedDiscount = totalAmount.value * discountRate
    
    discountAmount.value = calculatedDiscount
    
    if (discountRate > 0) {
      ElMessage.success(
        `${selectedMember.value.memberLevelName}享受${(discountRate * 100)}%折扣，优惠¥${discountAmount.value.toFixed(2)}`
      )
    }
  }
}

const calculateFinalAmount = () => {
  if (selectedMember.value) {
    calculateMemberDiscount()
  }
}

// 清空会员信息时重置优惠
const clearMemberInfo = () => {
  selectedMember.value = null
  memberDiscountInfo.value = null
  memberPhone.value = ''
  discountAmount.value = 0
}

// 工具方法
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
  console.log('🎉 收银台页面已加载')
  loadProducts()
  nextTick(() => {
    barcodeInputRef.value?.focus()
  })
})

// 监听
watch(productSearchKeyword, () => {
  filterProducts()
})

watch(totalAmount, () => {
  if (selectedMember.value) {
    calculateMemberDiscount()
  }
})
</script>

<style scoped>
.cashier {
  padding: 15px;
  height: calc(100vh - 30px);
  overflow: hidden;
  background: #f8f9fa;
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
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border: 1px solid #e6e6e6;
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
  color: #1a1a1a;
  font-weight: 600;
  font-size: 1.2rem;
}

.scan-stats {
  display: flex;
  gap: 15px;
  font-size: 0.95rem;
}

.scan-count {
  color: #2c3e50;
  font-weight: 500;
  background: #f8f9fa;
  padding: 4px 8px;
  border-radius: 4px;
}

.total-preview {
  color: #d35400;
  font-weight: bold;
  background: #fef7e0;
  padding: 4px 8px;
  border-radius: 4px;
}

.search-suggestions {
  margin-top: 10px;
  border: 2px solid #409eff;
  border-radius: 8px;
  background: white;
  max-height: 200px;
  overflow-y: auto;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.suggestions-header {
  padding: 10px 15px;
  background: #409eff;
  color: white;
  font-size: 0.9rem;
  font-weight: 600;
}

.suggestion-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 15px;
  cursor: pointer;
  transition: background 0.2s;
  border-bottom: 1px solid #f0f0f0;
}

.suggestion-item:hover {
  background: #e3f2fd;
}

.suggestion-item:last-child {
  border-bottom: none;
}

.suggestion-info {
  flex: 1;
}

.suggestion-name {
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 4px;
  font-size: 0.95rem;
}

.suggestion-details {
  display: flex;
  gap: 15px;
  font-size: 0.85rem;
}

.suggestion-barcode {
  color: #666;
}

.suggestion-price {
  color: #d35400;
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
  color: #1a1a1a;
  font-weight: 600;
  font-size: 1.2rem;
}

.products-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 12px;
  flex: 1;
  overflow-y: auto;
  padding: 5px 0;
}

.product-card {
  border: 2px solid #e9ecef;
  border-radius: 8px;
  padding: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: white;
}

.product-card:hover {
  border-color: #409eff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
  transform: translateY(-2px);
}

.product-card.low-stock {
  border-color: #e67e22;
  background: #fef9e7;
}

.product-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
}

.product-name {
  font-weight: 600;
  color: #1a1a1a;
  font-size: 0.95rem;
  flex: 1;
  margin-right: 10px;
  line-height: 1.3;
}

.product-details {
  margin-bottom: 12px;
}

.product-barcode {
  font-size: 0.85rem;
  color: #666;
  margin-bottom: 6px;
  font-family: 'Courier New', monospace;
}

.product-price {
  font-size: 1.1rem;
  color: #d35400;
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
  color: #1a1a1a;
  font-weight: 600;
  font-size: 1.2rem;
}

.cart-summary {
  display: flex;
  align-items: center;
  gap: 12px;
}

.cart-count {
  font-size: 0.9rem;
  color: #2c3e50;
  font-weight: 500;
  background: #f8f9fa;
  padding: 4px 8px;
  border-radius: 4px;
}

.cart-items {
  flex: 1;
  overflow: hidden;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  background: white;
}

.empty-cart {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: #666;
}

.empty-icon {
  font-size: 3rem;
  margin-bottom: 15px;
  opacity: 0.6;
}

.empty-cart p {
  font-size: 1rem;
  color: #666;
  margin: 5px 0;
}

.empty-tip {
  font-size: 0.9rem;
  color: #999;
}

.cart-list {
  height: 100%;
  overflow-y: auto;
  padding: 12px;
}

.cart-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #f0f0f0;
  transition: background 0.2s;
  background: white;
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
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 0.95rem;
}

.item-meta {
  display: flex;
  gap: 15px;
  font-size: 0.85rem;
}

.item-barcode {
  color: #666;
  font-family: 'Courier New', monospace;
}

.item-unit-price {
  color: #d35400;
  font-weight: 500;
}

.item-controls {
  display: flex;
  align-items: center;
  gap: 12px;
}

.quantity-controls {
  display: flex;
  align-items: center;
  gap: 10px;
}

.quantity-display {
  min-width: 30px;
  text-align: center;
  font-weight: bold;
  color: #1a1a1a;
  font-size: 1rem;
}

.item-total {
  min-width: 80px;
  text-align: right;
  font-weight: bold;
  color: #27ae60;
  font-size: 1rem;
}

.checkout-section {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 10px;
  border: 2px solid #e9ecef;
}

.checkout-section h3 {
  margin: 0 0 20px 0;
  color: #1a1a1a;
  font-weight: 600;
  font-size: 1.2rem;
}

.amount-display {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  border: 2px solid #e9ecef;
}

.amount-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  font-size: 1rem;
}

.amount-row:last-child {
  margin-bottom: 0;
}

.final-row {
  padding-top: 15px;
  border-top: 3px solid #27ae60;
  font-size: 1.2rem;
}

.amount-label {
  font-weight: 600;
  color: #1a1a1a;
}

.amount-value {
  font-weight: bold;
  color: #d35400;
}

.discount-value {
  color: #27ae60;
}

.final-amount {
  font-size: 1.4rem;
  color: #27ae60;
  font-weight: bold;
}

.member-section, .payment-section {
  margin-bottom: 20px;
}

.member-section h4, .payment-section h4 {
  margin: 0 0 12px 0;
  color: #1a1a1a;
  font-size: 1.1rem;
  font-weight: 600;
}

.member-card {
  background: linear-gradient(135deg, #e8f5e8 0%, #f0f8f0 100%);
  padding: 18px;
  border-radius: 10px;
  margin-top: 12px;
  border: 2px solid #27ae60;
  box-shadow: 0 2px 8px rgba(39, 174, 96, 0.15);
}

.member-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.member-name {
  font-weight: bold;
  color: #1a1a1a;
  font-size: 1.1rem;
}

.clear-member {
  color: #666;
  padding: 0;
}

.member-details {
  margin-bottom: 12px;
}

.member-level {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.member-phone {
  color: #666;
  font-size: 0.9rem;
  font-weight: 500;
}

.member-benefits {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.95rem;
}

.member-points {
  color: #d35400;
  font-weight: 600;
}

.discount-info {
  color: #27ae60;
  font-weight: bold;
  background: rgba(39, 174, 96, 0.15);
  padding: 4px 10px;
  border-radius: 6px;
}

.member-discount {
  margin-top: 15px;
  padding: 12px;
  background: rgba(39, 174, 96, 0.1);
  border-radius: 8px;
  border-left: 4px solid #27ae60;
}

.discount-header {
  font-weight: bold;
  color: #27ae60;
  margin-bottom: 10px;
  font-size: 0.95rem;
}

.discount-details {
  font-size: 0.9rem;
}

.discount-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 6px;
  color: #1a1a1a;
}

.discount-row.highlight {
  color: #27ae60;
  font-weight: bold;
}

.saved-amount {
  color: #d35400;
  font-weight: bold;
}

.no-member-tip {
  margin-top: 12px;
  padding: 12px 15px;
  background: #e3f2fd;
  border-radius: 8px;
  color: #1976d2;
  font-size: 0.9rem;
  text-align: center;
  border: 2px dashed #1976d2;
  font-weight: 500;
}

.payment-options {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.action-buttons {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 12px;
}

.pay-button {
  font-size: 1.2rem;
  height: 55px;
  font-weight: bold;
}

.payment-success {
  text-align: center;
  padding: 25px;
}

.success-icon {
  font-size: 4rem;
  margin-bottom: 20px;
}

.payment-success h3 {
  color: #27ae60;
  margin-bottom: 25px;
  font-size: 1.6rem;
  font-weight: bold;
}

.payment-details {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  text-align: left;
  border: 2px solid #e9ecef;
}

.payment-details p {
  display: flex;
  justify-content: space-between;
  margin: 0 0 12px 0;
  padding: 8px 0;
  font-size: 1rem;
  color: #1a1a1a;
}

.payment-details span:first-child {
  font-weight: 600;
}

.payment-details span:last-child {
  font-weight: bold;
  color: #27ae60;
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