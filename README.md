# 企业内部培训软件服务端

随着企业业务的发展和竞争加剧，企业员工的培训和学习变得越来越重要。为了提升员工的技能水平、增强员工的竞争力，很有必要搭建属于企业内部培训学习平台。该平台面向一般企业，提供一种通用服务，旨在为企业内部员工提供在线学习、培训课程管理、学习进度跟踪、考核评估等功能，以支持员工的学习和发展。

## 1. 架构设计

![image-20231016163249232.png](https://i.postimg.cc/GpDR7mnD/image-20231016163249232.png)

服务端基于SpringCloud Alibaba微服务架构。企业内部培训软件分为客户端、服务端和数据库三大部分。客户端在使用其功能时，总是请求服务端，这一过程中会经过GateWay网关进行权限认证，然后根据用户请求权限，调用各服务。在本系统中，所有服务皆注册在Nacos注册中心中统一管理，使用SpringBoot 3 新特性WebFlux编程实现服务间的接口调用。数据库在此处分为MySQL数据库和Redis缓存数据库，二者共同实现数据存储部分。

## 1.1 客户端

客户端是指运行在Web浏览器面向用户的终端。在这里，客户端分为员工、讲师和管理员三个版本。

技术栈：

- Vue3 + Element Plus；
- 响应式UI设计，PC和移动端网页皆有适配。

仓库地址：[organwalk/training-client: 企业内部培训软件客户端 ](https://github.com/organwalk/training-client)

## 1.2 服务端

### 1.2.1 用户服务

运行于8181端口。用户服务旨在为权限认证、用户信息调阅、教师员工角色分配等提供接口服务。

技术栈：

- HTTP服务器，RESTful风格API
- Open JDK 17

- Springboot 3 + MyBatisPlus + RedisTemplate
- MySQL 8.0 + Redis 7.0

仓库地址：[用户服务](https://github.com/organwalk/training-server/tree/master/training-user-service)

### 1.2.2 部门管理服务

运行于8182端口。部门管理服务旨在为调阅部门信息及部门管理提供接口服务。

技术栈：

- HTTP服务器，RESTful风格API
- Open JDK 17
- Springboot 3 + MyBatisPlus + RedisTemplate + WebFlux
- MySQL 8.0 + Redis 7.0

仓库地址：[部门管理服务](https://github.com/organwalk/training-server/tree/master/training-department-service)

### 1.2.3 资源服务

运行于8183端口。资源服务提供本系统的一般资源存储，同时提供教材的数据存储。旨在为系统建立一个资源服务器。

- HTTP服务器，RESTful风格API

- Open JDK 17
- Springboot 3 + MyBatisPlus + RedisTemplate + WebFlux + FFmpeg
- MySQL 8.0 + Redis 7.0

仓库地址：[资源管理服务](https://github.com/organwalk/training-server/tree/master/training-resource)

### 1.2.4 培训管理服务

运行于8184端口。培训管理服务旨在为培训课程管理和培训计划管理提供接口服务。

技术栈：

- HTTP服务器，RESTful风格API
- Open JDK 17
- Springboot 3 + MyBatisPlus + RedisTemplate + WebFlux
- MySQL 8.0 + Redis 7.0

仓库地址：[培训管理服务](https://github.com/organwalk/training-server/tree/master/training-plan-service)

### 1.2.5 学习相关服务

运行于8185端口。学习相关服务旨在为在线学习、考核与评估、讨论与互动提供接口服务。

技术栈：

- HTTP服务器，RESTful风格API

- Open JDK 17
- Springboot 3 + MyBatisPlus + RedisTemplate + WebFlux
- MySQL 8.0 + Redis 7.0

仓库地址：[学习相关服务](https://github.com/organwalk/training-server/tree/master/training-learn-service)

### 1.2.6 进度跟踪服务

运行于8186端口。进度跟踪服务旨在为学习进度跟踪提供接口服务。

技术栈：

- HTTP服务器，RESTful风格API

- Open JDK 17
- Springboot 3 + MyBatisPlus + RedisTemplate + WebFlux
- MySQL 8.0 + Redis 7.0

仓库地址：[进度跟踪服务](https://github.com/organwalk/training-server/tree/master/training-progress-service)

### 1.2.7 消息通知服务

消息通知服务旨在为系统建立一个消息通知服务器。

技术栈：

- HTTP服务器，RESTful风格API以及WebSocket服务器提供主动的消息推送

- Open JDK 17
- Springboot 3 + MyBatisPlus + RedisTemplate + WebFlux + WebSocket+RabbitMQ
- MySQL 8.0 + Redis 7.0

### 1.2.8 GateWay网关

在网关处通过调用用户服务完成权限认证。同时提供请求管理。

技术栈:

- HTTP服务器
- Open JDK 17
- Springboot 3 + MyBatisPlus + WebFlux

仓库地址：[GateWay网关](https://github.com/organwalk/training-server/tree/master/training-gateway)

## 1.3 项目运行所需配置

yml配置文件模板参照：[YML File Configuration Template](https://github.com/organwalk/training-server/blob/master/YML%20File%20Configuration%20Template.md)

## 1.4 数据库表设计

数据库表设计参照：[Database Table Design](https://github.com/organwalk/training-server/blob/master/Database%20Table%20Design.md)

## 1.5 开发环境

基于Windows11开发，需要具备：

- OpenJDK 17
- Nacos 2.1.1
- SpringBoot 3.0.11

