<template>
  <div class="container dialog-container">
    <div class="row">
      <!-- 左侧：摄像头和表情识别 -->
      <div class="col-md-4">
        <div class="card mb-4">
          <div class="card-header">
            <h4>摄像头预览</h4>
          </div>
          <div class="card-body">
            <div class="camera-container">
              <video ref="video" autoplay playsinline></video>
              <canvas ref="canvas" class="face-detection"></canvas>
            </div>
            <button 
              class="btn btn-outline-primary w-100 mt-3"
              @click="startCamera"
            >
              {{ isCameraOn ? '关闭摄像头' : '开启摄像头' }}
            </button>
          </div>
        </div>
        
        <div class="card mb-4">
          <div class="card-header">
            <h4>表情分析</h4>
          </div>
          <div class="card-body">
            <div class="emotion-result" v-if="currentEmotion">
              <h5>当前表情: {{ currentEmotion }}</h5>
              <div class="emotion-confidence">
                置信度: {{ emotionConfidence }}%
              </div>
            </div>
            <div v-else class="no-emotion">
              <p>未检测到表情</p>
            </div>
          </div>
        </div>
        
        <div class="card">
          <div class="card-header">
            <h4>好感度</h4>
          </div>
          <div class="card-body">
            <div class="affinity-score">
              <div class="score-value">{{ affinityScore }}</div>
              <div class="score-label">当前好感度</div>
            </div>
            <div class="affinity-bar">
              <div 
                class="affinity-progress" 
                :style="{ width: affinityScore + '%' }"
                :class="getAffinityClass()"
              ></div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 右侧：对话界面 -->
      <div class="col-md-8">
        <div class="card">
          <div class="card-header">
            <h4>智能对话</h4>
            <div class="target-info" v-if="targetInfo">
              攻略对象: {{ targetInfo.nickname }} ({{ targetInfo.personality }})
            </div>
          </div>
          <div class="card-body dialog-messages">
            <div v-if="messages.length === 0" class="no-messages">
              开始对话吧！
            </div>
            <div 
              v-for="(message, index) in messages" 
              :key="index"
              class="message-wrapper"
              :class="{ 'my-message': message.isUser }"
            >
              <div class="message-bubble">
                {{ message.content }}
              </div>
              <div class="message-time">{{ message.time }}</div>
            </div>
          </div>
          <div class="card-footer">
            <div class="reply-suggestions" v-if="replySuggestions.length > 0">
              <div class="suggestion-label">推荐回复：</div>
              <div class="suggestion-buttons">
                <button 
                  v-for="(suggestion, index) in replySuggestions" 
                  :key="index"
                  class="btn btn-outline-primary btn-sm m-1"
                  @click="sendSuggestion(suggestion)"
                >
                  {{ suggestion }}
                </button>
              </div>
            </div>
            <div class="input-group mt-3">
              <input 
                type="text" 
                class="form-control" 
                v-model="inputMessage"
                placeholder="输入消息..."
                @keyup.enter="sendMessage"
              >
              <button class="btn btn-primary" @click="sendMessage">
                发送
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import axios from 'axios'

// 摄像头相关
const video = ref(null)
const canvas = ref(null)
const isCameraOn = ref(false)
let stream = null
let detectionInterval = null

// 表情和好感度
const currentEmotion = ref('')
const emotionConfidence = ref(0)
const affinityScore = ref(50)

// 对话相关
const messages = ref([])
const inputMessage = ref('')
const replySuggestions = ref([])

// 攻略对象信息
const targetInfo = ref(null)

// 初始化
onMounted(() => {
  // 尝试加载本地存储的攻略对象信息
  const savedTarget = localStorage.getItem('targetInfo')
  if (savedTarget) {
    targetInfo.value = JSON.parse(savedTarget)
  }
  
  // 初始化画布
  initCanvas()
})

// 清理资源
onUnmounted(() => {
  stopCamera()
})

// 初始化画布
const initCanvas = () => {
  if (canvas.value) {
    const ctx = canvas.value.getContext('2d')
    canvas.value.width = 320
    canvas.value.height = 240
  }
}

// 开启/关闭摄像头
const startCamera = async () => {
  if (isCameraOn.value) {
    stopCamera()
    return
  }
  
  try {
    stream = await navigator.mediaDevices.getUserMedia({ 
      video: { 
        width: 320, 
        height: 240 
      } 
    })
    video.value.srcObject = stream
    isCameraOn.value = true
    
    // 开始表情检测
    startEmotionDetection()
  } catch (error) {
    console.error('摄像头访问失败:', error)
    alert('无法访问摄像头，请确保已授予权限')
  }
}

// 停止摄像头
const stopCamera = () => {
  if (stream) {
    stream.getTracks().forEach(track => track.stop())
    stream = null
  }
  if (detectionInterval) {
    clearInterval(detectionInterval)
    detectionInterval = null
  }
  isCameraOn.value = false
  currentEmotion.value = ''
}

// 开始表情检测
const startEmotionDetection = () => {
  // 每5秒进行一次表情识别
  detectionInterval = setInterval(async () => {
    if (video.value && canvas.value) {
      // 捕获视频帧
      const ctx = canvas.value.getContext('2d')
      ctx.drawImage(video.value, 0, 0, 320, 240)
      
      // 转换为Base64
      const imageData = canvas.value.toDataURL('image/jpeg', 0.8)
      const base64Image = imageData.split(',')[1]
      
      try {
        // 调用后端表情识别API
        const response = await axios.post('/emotion-detect', {
          image: base64Image
        })
        
        if (response.data && response.data.emotion) {
          currentEmotion.value = response.data.emotion
          emotionConfidence.value = Math.round(response.data.confidence * 100)
          
          // 更新好感度（这里简化处理，实际应该根据更多因素计算）
          updateAffinityScore(response.data.emotion)
        }
      } catch (error) {
        console.error('表情识别失败:', error)
      }
    }
  }, 5000)
}

// 更新好感度
const updateAffinityScore = (emotion) => {
  switch (emotion) {
    case '开心':
      affinityScore.value = Math.min(100, affinityScore.value + 2)
      break
    case '皱眉':
      affinityScore.value = Math.max(0, affinityScore.value - 3)
      break
    case '无视':
      // 无视状态，不改变好感度
      break
    default:
      // 其他表情微调
      affinityScore.value = Math.max(0, Math.min(100, affinityScore.value + 0.5))
  }
  
  // 保存到后端
  saveAffinityScore()
}

// 保存好感度
const saveAffinityScore = async () => {
  try {
    await axios.post('/affinity/update', {
      targetId: targetInfo.value?.id || 1,
      score: affinityScore.value,
      emotion: currentEmotion.value
    })
  } catch (error) {
    console.error('保存好感度失败:', error)
  }
}

// 获取好感度样式类
const getAffinityClass = () => {
  if (affinityScore.value >= 80) return 'affinity-high'
  if (affinityScore.value >= 50) return 'affinity-medium'
  return 'affinity-low'
}

// 发送消息
const sendMessage = async () => {
  if (!inputMessage.value.trim()) return
  
  // 添加到消息列表
  const messageText = inputMessage.value.trim()
  messages.value.push({
    content: messageText,
    isUser: true,
    time: new Date().toLocaleTimeString()
  })
  
  // 清空输入框
  inputMessage.value = ''
  
  try {
    // 调用后端获取推荐回复
    const response = await axios.post('/dialog/recommend', {
      message: messageText,
      targetInfo: targetInfo.value,
      emotion: currentEmotion.value,
      conversationHistory: messages.value
    })
    
    // 添加对方回复（这里模拟对方回复）
    setTimeout(() => {
      messages.value.push({
        content: '对方回复了...',
        isUser: false,
        time: new Date().toLocaleTimeString()
      })
      
      // 更新推荐回复
      if (response.data && response.data.suggestions) {
        replySuggestions.value = response.data.suggestions
      }
    }, 1000)
  } catch (error) {
    console.error('获取推荐回复失败:', error)
  }
}

// 发送推荐回复
const sendSuggestion = (suggestion) => {
  inputMessage.value = suggestion
  sendMessage()
}
</script>

<style scoped>
.dialog-container {
  padding: 20px 0;
}

.camera-container {
  position: relative;
  width: 320px;
  height: 240px;
  margin: 0 auto;
}

video, canvas {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  border-radius: 8px;
}

.face-detection {
  z-index: 10;
  pointer-events: none;
}

.emotion-result {
  text-align: center;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.emotion-confidence {
  color: #666;
  margin-top: 5px;
}

.no-emotion {
  text-align: center;
  color: #999;
  padding: 15px;
}

.affinity-score {
  text-align: center;
  margin-bottom: 15px;
}

.score-value {
  font-size: 36px;
  font-weight: bold;
  color: #007bff;
}

.score-label {
  color: #666;
  margin-top: 5px;
}

.affinity-bar {
  height: 20px;
  background-color: #e9ecef;
  border-radius: 10px;
  overflow: hidden;
}

.affinity-progress {
  height: 100%;
  transition: width 0.3s ease;
}

.affinity-high {
  background-color: #28a745;
}

.affinity-medium {
  background-color: #ffc107;
}

.affinity-low {
  background-color: #dc3545;
}

.dialog-messages {
  height: 400px;
  overflow-y: auto;
  padding: 15px;
}

.no-messages {
  text-align: center;
  color: #999;
  padding: 40px;
}

.message-wrapper {
  margin-bottom: 15px;
  display: flex;
  flex-direction: column;
}

.my-message {
  align-items: flex-end;
}

.message-bubble {
  max-width: 70%;
  padding: 10px 15px;
  border-radius: 18px;
  word-wrap: break-word;
}

.message-wrapper:not(.my-message) .message-bubble {
  background-color: #f8f9fa;
  border-bottom-left-radius: 4px;
}

.my-message .message-bubble {
  background-color: #007bff;
  color: white;
  border-bottom-right-radius: 4px;
}

.message-time {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

.reply-suggestions {
  margin-bottom: 10px;
}

.suggestion-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 5px;
}

.suggestion-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

.target-info {
  font-size: 14px;
  color: #666;
  margin-top: 5px;
}
</style>