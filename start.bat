@echo off

rem 表情对话与好感度管理智能交互工具 - 启动脚本

echo ================================================
echo 表情对话与好感度管理智能交互工具 后端服务启动脚本
echo ================================================

rem 检查Java环境
echo 正在检查Java环境...
java -version
if %errorlevel% neq 0 (
    echo 错误: 未找到Java环境，请确保已安装JDK 17或更高版本
    pause
    exit /b 1
)

echo.
echo 注意事项:
set /p dummy=1. 请确保已安装MySQL数据库并创建数据库(expr_dialog) [按Enter继续] 
set /p dummy=2. 请确保已安装Redis服务并启动 [按Enter继续]
set /p dummy=3. 请确保已修改application.yml中的数据库配置和API Key [按Enter继续]

rem 启动应用
echo.
echo 正在启动后端服务...
echo 服务地址: http://localhost:8080/api
echo 按Ctrl+C停止服务

java -jar target/expr-dialog-affinity-tool-1.0.0.jar

if %errorlevel% neq 0 (
    echo 错误: 服务启动失败
    pause
    exit /b 1
)

pause