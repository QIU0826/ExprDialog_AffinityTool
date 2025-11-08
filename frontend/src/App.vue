<template>
  <div class="app-container">
    <header class="app-header">
      <nav class="navbar navbar-expand-lg navbar-light bg-light">
        <div class="container">
          <router-link to="/" class="navbar-brand">表情对话智能助手</router-link>
          <div class="collapse navbar-collapse">
            <ul class="navbar-nav mr-auto">
              <li class="nav-item">
                <router-link to="/target-info" class="nav-link">设置攻略对象</router-link>
              </li>
              <li class="nav-item">
                <router-link to="/dialog" class="nav-link">开始对话</router-link>
              </li>
            </ul>
          </div>
        </div>
      </nav>
    </header>
    <main class="app-main">
      <router-view />
    </main>
    <footer class="app-footer">
      <div class="container">
        <p class="text-center text-muted">© 2024 表情对话智能助手 - 实时对话+表情感知+好感度管理</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
// App组件逻辑
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-header {
  background-color: #f8f9fa;
  border-bottom: 1px solid #dee2e6;
}

.app-main {
  flex: 1;
  padding: 20px 0;
}

.app-footer {
  background-color: #f8f9fa;
  padding: 20px 0;
  margin-top: 20px;
}
</style>