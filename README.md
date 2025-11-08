# 表情对话与好感度管理智能交互工具

## 项目简介

这是一个结合了实时对话、表情感知和好感度管理的智能交互工具。通过摄像头捕捉用户表情，分析对话内容，并结合攻略对象的基础信息（爱好/性格/性别）和本人信息，像galgame一样推荐最优回复，帮助用户提高社交互动效果。

## 技术栈

- **前端**：Vue 3.3.x、Vue Router 4.2.x、Pinia 2.1.x、Axios 1.5.x、Vite 4.4.x
- **后端**：Java 17、Spring Boot 3.2.5、Spring Data JPA、Spring Data Redis
- **数据库**：MySQL 8.0
- **缓存**：Redis 6.2.6+
- **AI服务**：百度AI表情识别、OpenAI ChatGPT API
- **图像处理**：OpenCV 4.8.0

## 项目结构

### 前端结构
```
frontend/
├── public/
├── src/
│   ├── assets/
│   ├── components/
│   ├── views/
│   │   ├── HomeView.vue          # 首页
│   │   ├── TargetInfoForm.vue    # 攻略对象信息录入表单
│   │   └── DialogView.vue        # 对话与表情识别页面
│   ├── router/
│   │   └── index.js              # 路由配置
│   ├── App.vue                   # 根组件
│   └── main.js                   # 入口文件
├── index.html
├── package.json
├── vite.config.js
└── README.md
```

### 后端结构
```
src/main/java/com/exprdialog/
├── controller/
│   ├── TargetInfoController.java     # 攻略对象信息控制器
│   ├── EmotionDetectionController.java # 表情识别控制器
│   ├── DialogController.java         # 对话控制器
│   └── AffinityController.java       # 好感度控制器
├── model/
│   ├── TargetInfo.java               # 攻略对象信息模型
│   ├── AffinityScore.java            # 好感度数据模型
│   └── ConversationHistory.java      # 对话历史模型
├── repository/
│   ├── TargetInfoRepository.java     # 攻略对象信息仓库
│   ├── AffinityScoreRepository.java  # 好感度数据仓库
│   └── ConversationHistoryRepository.java # 对话历史仓库
├── service/
│   ├── BaiDuAIService.java           # 百度AI表情识别服务
│   ├── OpenCVService.java            # OpenCV面部检测服务
58: │   ├── DeepSeekService.java          # DeepSeek智能回复服务
│   └── AffinityService.java          # 好感度计算服务
└── ExprDialogApplication.java        # 应用程序入口
```

## 核心功能

### 1. 攻略对象信息管理
- 录入和管理攻略对象的基本信息，包括昵称、性别、性格、爱好等
- 支持信息的增删改查操作

### 2. 实时表情识别
- 使用WebRTC调用摄像头获取实时视频流
- 通过OpenCV进行人脸检测和裁剪
- 调用百度AI表情识别API分析表情
- 实时显示表情识别结果

### 3. 智能对话推荐
- 结合对话历史、表情结果和攻略对象信息
- 调用deepseek生成个性化回复建议
- 根据不同性格和情绪提供多种回复选项

### 4. 好感度管理系统
- 基于表情、消息内容和回复速度动态计算好感度
- 提供好感度历史记录和趋势分析
- 当好感度下降时提供预警和恢复建议
- 好感度数据实时缓存到Redis，历史数据保存到MySQL

## 快速开始

### 前置条件

1. 安装JDK 17
2. 安装Node.js 16.14+ 和 npm 8.5+
3. 安装MySQL 8.0和Redis 6.2.6+
4. 安装OpenCV 4.8.0并配置环境变量
5. 申请百度AI开放平台账号并获取API Key和Secret Key
6. 申请deepseek账号并获取API Key

### 后端配置

1. 克隆项目
```bash
git clone https://github.com/yourusername/expr-dialog-affinity-tool.git
cd expr-dialog-affinity-tool
```

2. 修改数据库配置
在`application.yml`中修改MySQL和Redis的连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/expr_dialog?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC
    username: 
    password: 
```

3. 配置API Key
在`application.yml`中配置百度AI和deepseek的API Key：
```yaml
baidu:
  ai:
    api-key:
    secret-key: 

ai:
  api-key: your_openai_api_key
```

4. 配置OpenCV路径
在`application.yml`中配置OpenCV人脸检测模型路径：
```yaml
opencv:
  haar-cascade-path: D:\opencv-4.8.0\data\haarcascades\haarcascade_frontalface_default.xml
```

5. 启动后端服务
```bash
mvn spring-boot:run
```

### 前端配置

1. 进入前端目录
```bash
cd frontend
```

2. 安装依赖
```bash
npm install
```

3. 启动前端服务
```bash
npm run dev
```

4. 访问应用
在浏览器中打开 http://localhost:8081

## 使用说明

1. **首页**：介绍应用功能，提供导航到其他功能模块

2. **攻略对象信息录入**：
   - 填写攻略对象的昵称、选择性别
   - 选择性格类型（内向/外向/直爽/敏感）
   - 选择爱好标签
   - 点击保存按钮提交信息

3. **对话页面**：
   - 系统会自动调用摄像头（请授权）
   - 实时显示面部检测框和表情识别结果
   - 在输入框中输入消息，系统会根据当前表情和对话历史推荐回复选项
   - 选择推荐回复或输入自定义回复
   - 实时查看好感度变化和趋势

## 注意事项

1. 请确保已正确配置百度AI和deepseek的API Key，否则表情识别和回复推荐功能将无法使用

2. 首次使用时需要授予浏览器访问摄像头的权限

3. 好感度计算基于多种因素，包括表情、消息内容和回复速度，建议在真实对话场景中使用以获得更准确的结果

4. 在本地开发环境中，前端默认运行在8081端口，后端默认运行在8080端口（context-path为/api）

## 许可证

本项目采用MIT许可证。详见LICENSE文件。

## 联系方式

如有任何问题或建议，请通过以下方式联系：

- 邮箱：2889386774@qq.com
