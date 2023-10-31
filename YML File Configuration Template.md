# YML File Configuration Template

此处提供yml配置模板，免去了一些敏感信息，保留了运行项目必需配置。

## 1. training-user-service

```yml
server:
  port: 8181
spring:
  # 服务命名
  application:
    name: training-user-service
  # 服务注册发现
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
  # MySQL数据库
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://域名:端口/training?useSSL=false
    username: 用户名
    password: 密码
  # Redis数据库
  data:
    redis:
      database: 10
      host: 域名
      port: 端口
      password: 密码
      timeout: 5000
      jedis:
        pool:
          max-idle: 6
          max-active: 32
          max-wait: 100
          min-idle: 4
  transaction:
    default-timeout: 30 # 默认事务超时时间为30s
  security:
    user:
      password:
        encoder: bcrypt
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

## 2. training-department-service

```yml
server:
  port: 8182
spring:
  # 服务命名
  application:
    name: training-department-service
  # 服务注册发现
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
  # MySQL数据库
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://域名:端口/training?useSSL=false
    username: 用户名
    password: 密码
  # Redis数据库
  data:
    redis:
      database: 10
      host: 域名
      port: 端口
      password: 密码
      timeout: 5000
      jedis:
        pool:
          max-idle: 6
          max-active: 32
          max-wait: 100
          min-idle: 4
  transaction:
    default-timeout: 30 # 默认事务超时时间为30s
  security:
    user:
      password:
        encoder: bcrypt
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

## 3. training-resource

```yml
server:
  port: 8183
spring:
  # 服务命名
  application:
    name: training-resource
  # 服务注册发现
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
  # MySQL数据库
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://域名:端口/training?useSSL=false
    username: 用户名
    password: 密码
  # Redis数据库
  data:
    redis:
      database: 10
      host: 域名
      port: 端口
      password: 密码
      timeout: 5000
      jedis:
        pool:
          max-idle: 6
          max-active: 32
          max-wait: 100
          min-idle: 4
  transaction:
    default-timeout: 30 # 默认事务超时时间为30s
  security:
    user:
      password:
        encoder: bcrypt
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl

upload:
  normal-path: 绝对路径\normal
```

## 4. training-plan-service

```yml
server:
  port: 8184
spring:
  # 服务命名
  application:
    name: training-plan-service
  # 服务注册发现
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
  # MySQL数据库
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://域名:端口/training?useSSL=false
    username: 用户名
    password: 密码
  # Redis数据库
  data:
    redis:
      database: 10
      host: 域名
      port: 端口
      password: 密码
      timeout: 5000
      jedis:
        pool:
          max-idle: 6
          max-active: 32
          max-wait: 100
          min-idle: 4
  transaction:
    default-timeout: 30 # 默认事务超时时间为30s
  security:
    user:
      password:
        encoder: bcrypt
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

## 5. training-learn-service

```yml
server:
  port: 8185
spring:
  # 服务命名
  application:
    name: training-learn-service
  # 服务注册发现
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
  # MySQL数据库
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://域名:端口/training?useSSL=false
    username: 用户名
    password: 密码
  # Redis数据库
  data:
    redis:
      database: 10
      host: 域名
      port: 端口
      password: 密码
      timeout: 5000
      jedis:
        pool:
          max-idle: 6
          max-active: 32
          max-wait: 100
          min-idle: 4
  transaction:
    default-timeout: 30 # 默认事务超时时间为30s
  security:
    user:
      password:
        encoder: bcrypt
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

## 6. training-progress-service

```yml
server:
  port: 8186
spring:
  # 服务命名
  application:
    name: training-progress-service
  # 服务注册发现
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
  # MySQL数据库
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://域名:端口/training?useSSL=false
    username: 用户名
    password: 密码
  # Redis数据库
  data:
    redis:
      database: 10
      host: 域名
      port: 端口
      password: 密码
      timeout: 5000
      jedis:
        pool:
          max-idle: 6
          max-active: 32
          max-wait: 100
          min-idle: 4
  transaction:
    default-timeout: 30 # 默认事务超时时间为30s
  security:
    user:
      password:
        encoder: bcrypt
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

