<template>
  <div class="container home-container">
    <div class="row justify-content-center">
      <div class="col-md-8 text-center">
        <h1 class="display-4 mb-4">表情对话智能助手</h1>
        <p class="lead mb-6">实时对话 + 表情感知 + 好感度管理，让你的社交更智能</p>
        <div class="card mb-6">
          <div class="card-body">
            <h5 class="card-title">功能介绍</h5>
            <ul class="list-unstyled text-left">
              <li class="mb-2">✅ <strong>表情感知</strong>：通过摄像头实时捕捉并分析表情</li>
              <li class="mb-2">✅ <strong>智能对话推荐</strong>：基于对话历史和表情分析，推荐最优回复</li>
              <li class="mb-2">✅ <strong>好感度管理</strong>：实时计算和显示好感度变化</li>
              <li class="mb-2">✅ <strong>个性化攻略</strong>：根据对象性格、爱好提供定制化对话策略</li>
            </ul>
          </div>
        </div>
        <div class="d-flex justify-content-center gap-4">
          <router-link to="/target-info" class="btn btn-primary btn-lg">
            开始使用
          </router-link>
          <router-link to="/dialog" class="btn btn-outline-primary btn-lg">
            直接对话
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// 首页逻辑
</script>

<style scoped>
.home-container {
  padding: 40px 0;
}

.card {
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}
</style>