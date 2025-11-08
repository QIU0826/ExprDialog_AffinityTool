<template>
  <div class="container target-form-container">
    <div class="row justify-content-center">
      <div class="col-md-6">
        <h2 class="text-center mb-4">设置攻略对象信息</h2>
        <form @submit.prevent="submitForm">
          <div class="mb-3">
            <label for="nickname" class="form-label">昵称</label>
            <input 
              type="text" 
              class="form-control" 
              id="nickname" 
              v-model="form.nickname" 
              required
              placeholder="请输入对方的昵称"
            >
          </div>
          
          <div class="mb-3">
            <label class="form-label">性别</label>
            <div class="form-check">
              <input 
                type="radio" 
                class="form-check-input" 
                id="gender-male" 
                value="男" 
                v-model="form.gender"
                required
              >
              <label class="form-check-label" for="gender-male">男</label>
            </div>
            <div class="form-check">
              <input 
                type="radio" 
                class="form-check-input" 
                id="gender-female" 
                value="女" 
                v-model="form.gender"
              >
              <label class="form-check-label" for="gender-female">女</label>
            </div>
            <div class="form-check">
              <input 
                type="radio" 
                class="form-check-input" 
                id="gender-other" 
                value="其他" 
                v-model="form.gender"
              >
              <label class="form-check-label" for="gender-other">其他</label>
            </div>
          </div>
          
          <div class="mb-3">
            <label for="personality" class="form-label">性格</label>
            <select 
              class="form-control" 
              id="personality" 
              v-model="form.personality" 
              required
            >
              <option value="">请选择性格类型</option>
              <option value="内向">内向</option>
              <option value="外向">外向</option>
              <option value="直爽">直爽</option>
              <option value="敏感">敏感</option>
            </select>
          </div>
          
          <div class="mb-3">
            <label class="form-label">爱好</label>
            <div class="爱好-checkboxes">
              <div class="form-check" v-for="hobby in hobbyOptions" :key="hobby">
                <input 
                  type="checkbox" 
                  class="form-check-input" 
                  :id="`hobby-${hobby}`" 
                  :value="hobby" 
                  v-model="form.hobbies"
                >
                <label class="form-check-label" :for="`hobby-${hobby}`">{{ hobby }}</label>
              </div>
            </div>
            <div class="mt-2">
              <input 
                type="text" 
                class="form-control" 
                v-model="newHobby" 
                placeholder="添加其他爱好"
                @keyup.enter="addCustomHobby"
              >
              <button 
                type="button" 
                class="btn btn-sm btn-outline-secondary mt-1" 
                @click="addCustomHobby"
              >
                添加
              </button>
            </div>
          </div>
          
          <div class="mb-3">
            <label for="notes" class="form-label">其他备注</label>
            <textarea 
              class="form-control" 
              id="notes" 
              v-model="form.notes" 
              rows="3"
              placeholder="添加一些其他有用的信息..."
            ></textarea>
          </div>
          
          <button type="submit" class="btn btn-primary w-100">保存信息</button>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()

// 表单数据
const form = ref({
  nickname: '',
  gender: '',
  personality: '',
  hobbies: [],
  notes: ''
})

// 预设爱好选项
const hobbyOptions = ref([
  '音乐', '电影', '读书', '旅行', '运动', '游戏', 
  '摄影', '烹饪', '绘画', '舞蹈', '编程', '美食',
  '健身', '瑜伽', '动漫', '追剧', '手工', '宠物'
])

// 自定义爱好输入
const newHobby = ref('')

// 添加自定义爱好
const addCustomHobby = () => {
  if (newHobby.value.trim() && !hobbyOptions.value.includes(newHobby.value.trim())) {
    hobbyOptions.value.push(newHobby.value.trim())
    form.value.hobbies.push(newHobby.value.trim())
    newHobby.value = ''
  }
}

// 提交表单
const submitForm = async () => {
  try {
    // 调用后端API保存数据
    const response = await axios.post('/target-info', form.value)
    
    // 保存到本地存储，方便后续使用
    localStorage.setItem('targetInfo', JSON.stringify(form.value))
    
    alert('信息保存成功！')
    
    // 跳转到对话页面
    router.push('/dialog')
  } catch (error) {
    console.error('保存失败:', error)
    alert('保存失败，请稍后重试')
  }
}
</script>

<style scoped>
.target-form-container {
  padding: 40px 0;
}

.爱好-checkboxes {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 10px;
  margin-bottom: 10px;
}
</style>