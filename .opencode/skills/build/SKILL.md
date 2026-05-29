---
name: build
description: 编译当前项目
license: MIT
compatibility: opencode
---

## Build Backend JAR

编译后端 JAR 包：

```powershell
$env:JAVA_HOME="D:\tool\Java\jdk-17"; cd chat2db-server; mvn clean package -DskipTests
```

生成的 JAR 文件：
- `chat2db-server/chat2db-server-start/target/chat2db-server-start.jar`

运行方式：
```powershell
java -jar chat2db-server/chat2db-server-start/target/chat2db-server-start.jar
```

## Build Backend (Compile Only)

仅编译不打包：

```powershell
$env:JAVA_HOME="D:\tool\Java\jdk-17"; cd chat2db-server; mvn clean compile -DskipTests
```

## Build Frontend

编译前端：

```powershell
nvm use 20; cd chat2db-client; yarn install; yarn run build:web
```

开发模式运行前端：
```powershell
cd chat2db-client; yarn install; yarn run start:web
```

```powershell
# Requirements
# - Java 17 (设置 JAVA_HOME 环境变量)
# - Node.js 16+
# - Maven 3.6.1+
# - Yarn 4.x
```