@echo off
setlocal enabledelayedexpansion

REM 检查Java环境
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误: 未找到Java环境，请确保已安装JDK并配置了PATH环境变量
    pause
    exit /b 1
)

echo 正在启动ExprDialog应用程序...

REM 设置类路径
set CLASSPATH=target\classes

REM 添加所有依赖JAR文件到类路径
for %%i in (target\lib\*.jar) do (
    set CLASSPATH=!CLASSPATH!;%%i
)

REM 添加Maven仓库中的主要依赖（如果target/lib不存在）
if not exist target\lib\*.jar (
    set MAVEN_REPO=%USERPROFILE%\.m2\repository
    set CLASSPATH=!CLASSPATH!;!MAVEN_REPO!\org\springframework\boot\spring-boot-starter-data-jpa\3.2.5\spring-boot-starter-data-jpa-3.2.5.jar
    set CLASSPATH=!CLASSPATH!;!MAVEN_REPO!\org\springframework\boot\spring-boot-starter-web\3.2.5\spring-boot-starter-web-3.2.5.jar
    set CLASSPATH=!CLASSPATH!;!MAVEN_REPO!\org\springframework\boot\spring-boot-starter\3.2.5\spring-boot-starter-3.2.5.jar
    set CLASSPATH=!CLASSPATH!;!MAVEN_REPO!\org\springframework\boot\spring-boot-autoconfigure\3.2.5\spring-boot-autoconfigure-3.2.5.jar
    set CLASSPATH=!CLASSPATH!;!MAVEN_REPO!\org\springframework\spring-context\6.1.6\spring-context-6.1.6.jar
    set CLASSPATH=!CLASSPATH!;!MAVEN_REPO!\org\springframework\spring-beans\6.1.6\spring-beans-6.1.6.jar
    set CLASSPATH=!CLASSPATH!;!MAVEN_REPO!\org\springframework\spring-web\6.1.6\spring-web-6.1.6.jar
    set CLASSPATH=!CLASSPATH!;!MAVEN_REPO!\org\springframework\spring-webmvc\6.1.6\spring-webmvc-6.1.6.jar
)

REM 直接运行主类
java -cp "!CLASSPATH!" com.exprdialog.ExprDialogApplication

if %errorlevel% neq 0 (
    echo 应用程序启动失败，请检查错误信息
    pause
    exit /b 1
)

pause