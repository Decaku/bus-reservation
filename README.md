# bus-reservation

[![GitHub](https://img.shields.io/github/followers/Decaku?style=social)](https://github.com/Decaku) [![GitHub](https://img.shields.io/apm/l/vim-mode)](https://zh.wikipedia.org/wiki/MIT%E8%A8%B1%E5%8F%AF%E8%AD%89) [![Language](https://img.shields.io/github/languages/top/Decaku/bus-reservation)]()

## 项目介绍

本项目是基于Dubbo框架实现的分布式班车预约系统。整体可以拆分为用户服务，车次服务，订单服务和支付服务四个微服务来实现服务解耦。

项目基于SpringBoot框架开发，使用Maven打包，集成了Redis，Zookeeper和RocketMQ等消息中间件，使用MySQL持久化数据。



## 项目结构

项目代码存放在school-bus目录下。

school-bus下的目录分别对应服务：

* guns-user 用户服务
* guns-bus 车次服务
* guns-order 订单服务
* guns-pay 支付服务
* guns-api 和 guns-core 存放一些工具类和dto数据模型
* guns-gateway   controller层，封装http API,通过调用其他四个微服务实现
* 其他 .log文件存放日志   .sql文件是建表语句



## 快速开始

首先保证机器上下载安装好redis，zookeeper，rocketmq等中间件并能正确启动提供服务。然后依次启动四个微服务和网关服务即可。本项目中采用的jdk版本是1.8。项目相关的配置文件放在每个model的properties.yml文件下，将端口和IP修改为你本机/服务器上的相关信息。



## 亮点

* 订单超时自动关闭，采用的是redis监听过期的key的方案，其实也可以通过rocketmq的死信队列来实
  现。还有定时任务，延时队列很多方案。没有绝对的好方案
* 使用RocketMQ事务消息保证数据强一致性
* 使用开源的Sentinel进行服务限流，保证服务高可用性质





## 设计问题

* 根据订单号删除订单，现在是硬删除，最好是定义一个状态字段然后打标记软删除
* Guns-pay model里支付密码和金额明文传递，不安全
* JWT SSO单点验证时token的过期时间是交给前端做，不合理，最好是生成token时带上过期时间，服务端解析token判断是否过期



## 开发和部署问题

* register路由在加上requestbody注解后，使用postman请求报415错误，去除后正常。
* dubbo服务提供者模块启动后能够连接注册中心，但是不能把服务注册进去，经过排查发现Impl类里的service注解用的不是dubbo包下的...  java同名的函数和注解太多了，注意不要用错..
* 终端连接zookeeper正常，spring boot服务启动连接zk报错。 各种尝试后发现是高版本jdk(15) 不兼容，降低jdk版本后正常。
* 终端连接zk失败，在zk的配置文件里加一行admin.serverPort=8001。https://blog.csdn.net/lihaitao910215/article/details/105176064
* 连接公司的wifi时，远程调用不成功，目前没找到问题，怀疑是ip的原因。





