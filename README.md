# Agent Backend Demo

一个基于Spring Boot的后端演示项目，展示了现代Java Web应用程序的最佳实践。

## 📋 项目概述

这是一个使用Spring Boot 3.5.0构建的后端API演示项目，集成了JPA数据访问、PostgreSQL数据库、Flyway数据库迁移等现代化技术栈。

## 🛠 技术栈

- **Java**: 21
- **Spring Boot**: 3.5.0
- **数据库**: PostgreSQL (生产环境) / H2 (测试环境)
- **ORM**: Spring Data JPA
- **数据库迁移**: Flyway
- **构建工具**: Gradle 8.13
- **容器化**: Docker Compose
- **开发工具**: Lombok

## 📋 环境要求

在开始之前，请确保您的开发环境满足以下要求：

- **Java**: JDK 21 或更高版本
- **Docker**: 用于运行PostgreSQL数据库
- **Git**: 用于克隆项目

## 🚀 快速开始

### 1. 克隆项目

```bash
git clone <repository-url>
cd agent-backend-demo
```

### 2. 启动数据库

使用Docker Compose启动PostgreSQL数据库：

```bash
docker-compose up -d
```

这将启动一个PostgreSQL容器，配置如下：
- 数据库名: `agent-backend`
- 用户名: `postgres`
- 密码: `postgres`
- 端口: `5432`

### 3. 构建项目

```bash
./gradlew build
```

### 4. 运行应用程序

```bash
./gradlew bootRun
```

应用程序将在 `http://localhost:8080` 启动。

## 🔧 开发指南

### 项目结构

```
src/
├── main/
│   ├── java/
│   │   └── org/tw/agent_backend_demo/
│   │       └── AgentBackendDemoApplication.java  # 主应用程序类
│   └── resources/
│       ├── application.properties                # 应用配置
│       ├── db/migration/                        # Flyway数据库迁移脚本
│       ├── static/                              # 静态资源
│       └── templates/                           # 模板文件
└── test/
    └── java/                                    # 测试代码
```

### 数据库配置

项目支持两种数据库配置：

1. **开发环境**: PostgreSQL (通过Docker Compose)
2. **测试环境**: H2内存数据库

### 添加数据库迁移

在 `src/main/resources/db/migration/` 目录下创建Flyway迁移脚本：

```
V1__Create_initial_tables.sql
V2__Add_user_table.sql
```

### 运行测试

```bash
./gradlew test
```

## 🐳 Docker部署

### 构建Docker镜像

```bash
./gradlew bootBuildImage
```

### 使用Docker Compose部署

```bash
docker-compose up
```

## 📝 API文档

应用程序启动后，您可以通过以下方式访问API：

- 应用程序: `http://localhost:8080`
- 健康检查: `http://localhost:8080/actuator/health` (如果启用了Actuator)

## 🔍 常见问题

### 数据库连接问题

如果遇到数据库连接问题，请检查：

1. Docker容器是否正在运行：`docker ps`
2. PostgreSQL端口是否被占用：`lsof -i :5432`
3. 数据库配置是否正确

### 构建失败

如果构建失败，请尝试：

1. 清理构建缓存：`./gradlew clean`
2. 重新构建：`./gradlew build --refresh-dependencies`

## 🤝 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开Pull Request

## 📄 许可证

本项目采用 [MIT许可证](LICENSE)。

## 📞 联系方式

如有问题或建议，请通过以下方式联系：

- 项目Issues: [GitHub Issues](../../issues)
- 邮箱: [your-email@example.com]

---

**注意**: 这是一个演示项目，仅用于学习和开发参考。在生产环境中使用前，请确保进行适当的安全配置和性能优化。 