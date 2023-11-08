# LsApi
开放API调用平台
### 介绍
基于Spring Boot + MySQL + Dubbo 的API接口调用平台，用户可以注册登录、开通接口调用权限，可以使用接口，并且每次调用会进行统计。<br/>
管理员可以发布接口、下线接口、接入接口，以及可视化接口的调用情况、数据。

### 主要功能
1. 用户的注册、登录、登出。
2. 接口的增删改查以及上线和下线。
3. 用户通过sdk调用接口，后台统计用户调用次数，并且可以在前端图表化展示调用信息。

### 技术栈
- Java Spring Boot框架
- MySQL 数据库
- MyBatis-Plus及 MyBatis ×自动生成
- API签名认证(Http 调用)
- Spring Boot Starter (SDK开发)
- Dubbo分布式(RPC、Nacos)
- Spring Cloud Gateway 微服务网关
- Swagger + Knife4i接口文档生成
- Hutool、Apache Common Utils、Gson等工具库

### 启动
api-backend：7529端口，后端接口管理（用户注册登录、发布新的接口、上传下线接口）<br/>
api-gateway：8090端口，网关<br/>
api-interface：8123端口，提供各种接口服务<br/>
api-client-sdk：客户端SDK，无端口，发送请求到8090端口，由网关进行转发到后端的api-interface<br/>
