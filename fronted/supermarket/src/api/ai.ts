export interface AIMessage {
  id: string
  role: 'user' | 'assistant'
  content: string
  timestamp: Date
  streaming?: boolean
}

export class AIService {
  private baseUrl = 'http://localhost:8080/api/ai'

  // SSE流式对话
  async streamChat(question: string, onMessage: (content: string) => void, onComplete: () => void, onError: (error: Error) => void) {
    try {
      const url = `${this.baseUrl}/stream?question=${encodeURIComponent(question)}`
      const eventSource = new EventSource(url)
      
      eventSource.onmessage = (event) => {
        if (event.data && event.data.trim()) {
          onMessage(event.data)
        }
      }
      
      eventSource.onerror = (error) => {
        console.error('❌ SSE连接错误:', error)
        eventSource.close()
        onError(new Error('AI服务连接失败'))
      }
      
      eventSource.addEventListener('ai-message', (event: any) => {
        if (event.data && event.data.trim()) {
          onMessage(event.data)
        }
      })
      
      // 检测连接完成
      let lastActivity = Date.now()
      const checkComplete = setInterval(() => {
        if (Date.now() - lastActivity > 3000) { // 3秒无新消息则认为完成
          clearInterval(checkComplete)
          eventSource.close()
          onComplete()
        }
      }, 1000)
      
      // 更新活动时间
      eventSource.onmessage = (event) => {
        lastActivity = Date.now()
        if (event.data && event.data.trim()) {
          onMessage(event.data)
        }
      }
      
      return eventSource
    } catch (error: any) {
      console.error('❌ 启动SSE流失败:', error)
      onError(error)
      return null
    }
  }

  // 获取预设问题
  getPresetQuestions(): string[] {
    return [
      '帮我分析今日销售情况',
      '库存管理有什么建议？',
      '如何提高收银效率？',
      '会员营销策略建议',
      '商品定价策略分析',
      '如何减少商品损耗？'
    ]
  }

  // 格式化系统信息用于AI分析
  formatSystemInfo(dashboardData: any): string {
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
}

export const aiService = new AIService()
