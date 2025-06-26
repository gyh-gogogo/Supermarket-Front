<template>
  <div class="ai-assistant" :class="{ 'expanded': isExpanded }">
    <!-- AI助手触发按钮 -->
    <div v-if="!isExpanded" class="ai-trigger" @click="toggleAssistant">
      <div class="ai-trigger-icon">🤖</div>
      <div class="ai-trigger-text">AI助手</div>
      <div v-if="hasNewMessage" class="ai-notification"></div>
    </div>

    <!-- AI对话面板 -->
    <div v-if="isExpanded" class="ai-panel">
      <!-- 头部 -->
      <div class="ai-header">
        <div class="ai-title">
          <span class="ai-avatar">🤖</span>
          <div class="ai-info">
            <div class="ai-name">超市AI助手</div>
            <div class="ai-status" :class="{ 'online': isConnected, 'offline': !isConnected }">
              {{ isConnected ? '在线' : '离线' }}
            </div>
          </div>
        </div>
        <div class="ai-controls">
          <el-button @click="clearHistory" size="small" type="text" title="清空对话">
            <el-icon><Delete /></el-icon>
          </el-button>
          <el-button @click="toggleAssistant" size="small" type="text" title="最小化">
            <el-icon><Minus /></el-icon>
          </el-button>
        </div>
      </div>

      <!-- 对话区域 -->
      <div class="ai-chat-container">
        <div class="ai-messages" ref="messagesContainer">
          <!-- 欢迎消息 -->
          <div v-if="messages.length === 0" class="welcome-message">
            <div class="welcome-avatar">🤖</div>
            <div class="welcome-content">
              <h4>您好！我是超市管理AI助手</h4>
              <p>我可以帮您分析销售数据、提供管理建议、解答运营问题。</p>
              <div class="preset-questions">
                
                
              </div>
            </div>
          </div>

          <!-- 聊天消息 -->
          <div v-for="message in messages" :key="message.id" class="message-item" :class="message.role">
            <div class="message-avatar">
              {{ message.role === 'user' ? '👤' : '🤖' }}
            </div>
            <div class="message-content">
              <div class="message-text" v-html="formatMessage(message.content)"></div>
              <div class="message-time">{{ formatTime(message.timestamp) }}</div>
            </div>
          </div>

          <!-- 打字指示器 -->
          <div v-if="isTyping" class="message-item assistant">
            <div class="message-avatar">🤖</div>
            <div class="message-content">
              <div class="typing-indicator">
                <span></span><span></span><span></span>
              </div>
            </div>
          </div>
        </div>

        <!-- 输入区域 -->
        <div class="ai-input-area">
          <div class="input-toolbar">
            
          </div>
          <div class="input-container">
            <el-input
              v-model="inputMessage"
              type="textarea"
              :rows="2"
              placeholder="输入您的问题，我来为您解答..."
              @keydown.enter.exact.prevent="sendMessage"
              @keydown.enter.shift.exact="newLine"
              :disabled="isTyping"
              ref="messageInput"
            />
            <el-button 
              type="primary" 
              @click="sendMessage"
              :loading="isTyping"
              :disabled="!inputMessage.trim()"
              class="send-button"
            >
              <el-icon v-if="!isTyping"><Position /></el-icon>
              <span v-if="isTyping">发送中...</span>
              <span v-else>发送</span>
            </el-button>
          </div>
          <div class="input-hint">
            按 Enter 发送，Shift + Enter 换行
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, nextTick, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Delete, Minus, Position } from '@element-plus/icons-vue'

// Props
interface Props {
  dashboardData?: any
}

const props = withDefaults(defineProps<Props>(), {
  dashboardData: () => ({})
})

interface AIMessage {
  id: string
  role: 'user' | 'assistant'
  content: string
  timestamp: Date
}

// 响应式数据
const isExpanded = ref(false)
const isConnected = ref(true)
const isTyping = ref(false)
const hasNewMessage = ref(false)
const inputMessage = ref('')
const messages = ref<AIMessage[]>([])
const messagesContainer = ref<HTMLElement>()
const messageInput = ref()

// 预设问题
const presetQuestions = ref([
  '分析今日销售情况',
  '库存管理建议',
  '收银效率优化',
  '会员营销策略'
])

// 当前流式连接
let currentEventSource: EventSource | null = null

// 方法
const toggleAssistant = () => {
  isExpanded.value = !isExpanded.value
  if (isExpanded.value) {
    hasNewMessage.value = false
    nextTick(() => {
      scrollToBottom()
      messageInput.value?.focus()
    })
  }
}

const sendMessage = async () => {
  if (!inputMessage.value.trim() || isTyping.value) return

  const userMessage: AIMessage = {
    id: Date.now().toString(),
    role: 'user',
    content: inputMessage.value.trim(),
    timestamp: new Date()
  }

  messages.value.push(userMessage)
  const question = inputMessage.value.trim()
  inputMessage.value = ''
  
  isTyping.value = true
  scrollToBottom()

  // 创建AI响应消息
  const aiMessage: AIMessage = {
    id: (Date.now() + 1).toString(),
    role: 'assistant',
    content: '',
    timestamp: new Date()
  }
  
  messages.value.push(aiMessage)
  scrollToBottom()

  try {
    console.log('🤖 开始AI流式对话:', question)
    
    // 使用您的后端接口格式
    const url = `http://localhost:8080/api/ai/stream?question=${encodeURIComponent(question)}`
    currentEventSource = new EventSource(url)
    
    currentEventSource.onmessage = (event) => {
      if (event.data && event.data.trim()) {
        aiMessage.content += event.data
        scrollToBottom()
      }
    }
    
    currentEventSource.addEventListener('ai-message', (event: any) => {
      if (event.data && event.data.trim()) {
        aiMessage.content += event.data
        scrollToBottom()
      }
    })
    
    currentEventSource.onerror = (error) => {
      console.error('❌ AI连接错误:', error)
      currentEventSource?.close()
      
      if (!aiMessage.content) {
        aiMessage.content = '抱歉，AI服务暂时不可用，请稍后重试。'
      }
      
      isTyping.value = false
      isConnected.value = false
      
      if (!isExpanded.value) {
        hasNewMessage.value = true
      }
      
  
    }
    
    // 检测完成 - 3秒无新消息认为完成
    let lastActivity = Date.now()
    const checkComplete = setInterval(() => {
      if (Date.now() - lastActivity > 3000) {
        clearInterval(checkComplete)
        currentEventSource?.close()
        isTyping.value = false
        isConnected.value = true
        
        if (!isExpanded.value) {
          hasNewMessage.value = true
        }
        
        console.log('✅ AI对话完成')
      }
    }, 1000)
    
    // 更新活动时间
    const originalOnMessage = currentEventSource.onmessage
    currentEventSource.onmessage = (event) => {
      lastActivity = Date.now()
      originalOnMessage?.call(currentEventSource, event)
    }
    
  } catch (error: any) {
    console.error('❌ AI对话失败:', error)
    
    if (aiMessage.content === '') {
      aiMessage.content = '发送失败，请检查网络连接。'
    }
    
    isTyping.value = false
    isConnected.value = false
    ElMessage.error('发送失败')
  }
}

const sendPresetQuestion = (question: string) => {
  inputMessage.value = question
  sendMessage()
}

const insertSystemInfo = () => {
  const systemInfo = formatSystemInfo(props.dashboardData)
  inputMessage.value = systemInfo
}

const showPresets = () => {
  ElMessage.info('请点击下方常用问题快速提问')
}

const clearHistory = () => {
  messages.value = []
  if (currentEventSource) {
    currentEventSource.close()
    currentEventSource = null
  }
  isTyping.value = false
  ElMessage.success('对话历史已清空')
}

const newLine = () => {
  inputMessage.value += '\n'
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

const formatMessage = (content: string): string => {
  return content
    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
    .replace(/\*(.*?)\*/g, '<em>$1</em>')
    .replace(/\n/g, '<br>')
    .replace(/`(.*?)`/g, '<code>$1</code>')
}

const formatTime = (timestamp: Date): string => {
  return timestamp.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

const formatSystemInfo = (dashboardData: any): string => {
  return `
当前超市系统状态：
- 今日销售额：¥${dashboardData.todaySales || 0}
- 今日订单数：${dashboardData.todayOrders || 0}
- 商品总数：${dashboardData.totalProducts || 0}
- 会员总数：${dashboardData.totalMembers || 0}
- 低库存商品：${dashboardData.lowStockCount || 0}个

请基于以上数据为超市管理提供专业建议。
  `.trim()
}

// 生命周期
onMounted(() => {
  console.log('🤖 AI助手组件已加载')
})

onUnmounted(() => {
  if (currentEventSource) {
    currentEventSource.close()
  }
})
</script>

<style scoped>
.ai-assistant {
  position: fixed;
  bottom: 20px;
  right: 20px;
  z-index: 9999;
  transition: all 0.3s ease;
}

.ai-trigger {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 12px 20px;
  border-radius: 25px;
  cursor: pointer;
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
  position: relative;
}

.ai-trigger:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 35px rgba(102, 126, 234, 0.6);
}

.ai-trigger-icon {
  font-size: 1.2rem;
}

.ai-trigger-text {
  font-weight: 600;
  font-size: 0.9rem;
}

.ai-notification {
  position: absolute;
  top: -5px;
  right: -5px;
  width: 12px;
  height: 12px;
  background: #ff4757;
  border-radius: 50%;
  border: 2px solid white;
  animation: pulse 2s infinite;
}

.ai-panel {
  background: white;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
  width: 400px;
  height: 600px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border: 1px solid rgba(0, 0, 0, 0.1);
  color: #2c3e50; /* 面板默认文字颜色 */
}

.ai-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 16px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.ai-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.ai-avatar {
  font-size: 1.5rem;
}

.ai-name {
  font-weight: 600;
  font-size: 1rem;
}

.ai-status {
  font-size: 0.8rem;
  opacity: 0.9;
}

.ai-status.online::before {
  content: '●';
  color: #4ade80;
  margin-right: 4px;
}

.ai-status.offline::before {
  content: '●';
  color: #f87171;
  margin-right: 4px;
}

.ai-controls {
  display: flex;
  gap: 8px;
}

.ai-chat-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.ai-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background: #f8f9fa;
  color: #2c3e50; /* 消息区域默认文字颜色 */
}

.welcome-message {
  text-align: center;
  padding: 20px;
}

.welcome-avatar {
  font-size: 3rem;
  margin-bottom: 16px;
}

.welcome-content h4 {
  color: #2c3e50; /* 欢迎标题深色 */
  margin: 0 0 8px 0;
  font-size: 1.1rem;
}

.welcome-content p {
  color: #666; /* 欢迎描述灰色 */
  margin: 0 0 20px 0;
  line-height: 1.5;
}

.preset-questions {
  text-align: left;
}

.preset-title {
  font-weight: 600;
  color: #2c3e50; /* 预设问题标题深色 */
  margin-bottom: 12px;
  font-size: 0.9rem;
}

.preset-buttons {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.preset-buttons .el-button {
  justify-content: flex-start;
  text-align: left;
}

.message-item {
  display: flex;
  margin-bottom: 16px;
  gap: 12px;
}

.message-item.user {
  flex-direction: row-reverse;
}

.message-item.user .message-content {
  background: #667eea;
  color: white;
  border-radius: 18px 18px 4px 18px;
}

.message-item.assistant .message-content {
  background: white;
  border: 1px solid #e1e5e9;
  border-radius: 18px 18px 18px 4px;
  color: #2c3e50; /* 修复：添加深色文字 */
}

.message-avatar {
  font-size: 1.5rem;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #f0f2f5;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.message-content {
  max-width: 280px;
  padding: 12px 16px;
  word-wrap: break-word;
}

.message-text {
  line-height: 1.5;
  font-size: 0.95rem;
  color: inherit; /* 继承父元素颜色 */
}

.message-text strong {
  color: #1a1a1a; /* 加粗文字更深色 */
}

.message-text em {
  color: #666; /* 斜体文字中等色 */
}

.message-text code {
  background: #f5f5f5;
  color: #d73a49; /* 代码文字红色 */
  padding: 2px 4px;
  border-radius: 3px;
  font-family: 'Courier New', monospace;
}

.message-time {
  font-size: 0.75rem;
  opacity: 0.7;
  margin-top: 6px;
  color: #999; /* 时间文字灰色 */
}

.typing-indicator {
  padding: 12px 16px;
  display: flex;
  gap: 4px;
  align-items: center;
  color: #666; /* 打字指示器颜色 */
}

.input-toolbar {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
  padding-bottom: 8px;
  border-bottom: 1px solid #f0f2f5;
}

.input-container {
  display: flex;
  gap: 8px;
  align-items: flex-end;
}

.input-container .el-textarea {
  flex: 1;
}

.send-button {
  height: 40px;
  border-radius: 20px;
}

.input-hint {
  font-size: 0.75rem;
  color: #999; /* 输入提示灰色 */
  margin-top: 6px;
  text-align: center;
}

/* 滚动条样式 */
.ai-messages::-webkit-scrollbar {
  width: 6px;
}

.ai-messages::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.ai-messages::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.ai-messages::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 动画 */
@keyframes pulse {
  0% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.2); opacity: 0.7; }
  100% { transform: scale(1); opacity: 1; }
}

@keyframes typing {
  0%, 80%, 100% { transform: scale(0.8); opacity: 0.5; }
  40% { transform: scale(1); opacity: 1; }
}

/* 响应式 */
@media (max-width: 768px) {
  .ai-panel {
    width: calc(100vw - 40px);
    height: calc(100vh - 40px);
    position: fixed;
    top: 20px;
    left: 20px;
    right: 20px;
    bottom: 20px;
  }
  
  .ai-trigger {
    padding: 10px 16px;
  }
  
  .ai-trigger-text {
    display: none;
  }
}
</style>
