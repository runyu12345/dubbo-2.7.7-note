# Dubbo(2.7.7)源码阅读笔记

## 核心模块简介

### dubbo-common

> 公共模块, 其中包含很多工具类和公共逻辑, 例如: Dubbo SPI实现, 时间轮实现, 动态编译器等.

* compiler: 动态编译相关实现
* config: 配置相关实现
* constants: 常量定义, 尤其是URL参数的Key
* convert: 类型转换器集合
* extension: Dubbo SPI的核心实现
* io: I/O相关的工具类
* logger: 对多款Java日志框架的集成
* threadlocal: ThreadLocal相关工具类
* threadpool: 线程池相关工具类
* timer: 时间轮的核心实现
* utils: 通用的工具类集合

### dubbo-remoting

> Dubbo远程通信模块, 其中的子模块依赖各种开源组件实现远程通信. 在dubbo-remoting-api子模块中, 定义该模块的抽象概念, 在其他子模块中依赖其他开源组件进行实现. 例如: dubbo-remoting-netty4子模块, 依赖Netty4实现远程通信, dubbo-remoting-zookeeper通过Apache Curator实现与ZooKeeper集群交互. 

### dubbo-rpc

> Dubbo中对远程调用协议进行抽象的模块, 其中抽象了各种协议, 依赖于dubbo-remoting模块的远程调用功能. dubbo-rpc-api子模块是核心抽象, 其他子模块是针对具体协议的实现. 例如: dubbo-rpc-dubbo子模块是对dubbo协议的实现, 依赖了dubbo-remoting-netty4等dubbo-remoting子模块.**dubbo-rpc模块的实现中只包含一对一的调用, 不关心集群相关的内容.**

### dubbo-cluster

> Dubbo中负责管理集群的模块, 提供了负载均衡,容错,路由等一些列集群相关的功能. 最终的目的是将多个Provider伪装成一个Provider, 这样Consumer就可以像调用一个Provider那样调用Provider集群了.

### dubbo-registry

> Dubbo中负责与多种开源注册中心进行交互的模块, 提供注册中心的能力. 其中, dubbo-registry-api子模块是顶层抽象, 其他子模块是针对具体开源注册中心组件的具体实现. 例如: dubbo-registry-zookeeper子模块是Dubbo接入Zookeeper的具体实现.

### dubbo-monitor

> Dubbo的监控模块, 主要用于统计服务调用次数, 调用时间以及实现调用链路跟踪的服务.

### dubbo-config

> Dubbo对外暴露的配置都是由该模块进行解析的. 例如: dubbo-config-api子模块,负责处理API方式使用时的相关配置. dubbo-config-spring子模块负责处理与Spring集成使用时的相关配置方式. 有了dubbo-config模块, 用户只需要了解Dubbo配置的规则即可, 无须了解Dubbo内部的细节.

### dubbo-metadata

> Dubbo的元数据模块. 实现套路也是有一个api子模块进行抽象, 然后其他子模块进行具体实现.

### dubbo-configcenter

> Dubbo的动态配置模块, 主要负责外部化配置以及服务治理规则的存储于通知, 提供了多个子模块用来接入多种开源服务发现的组件.

### dubbo-demo

> 提供了三个基础的dubbo实例项配置, 分别是: 使用XML配置的Demo示例, 使用注解配置的Demo示例, 直接使用API的示例. 

## 看源码要解决的问题(1)

* Dubbo是如何与Zookeeper等注册中心进行交互的?

* Provider与Consumer之间是如何交互的? 

* 统一契约是什么?

*  契约是如何做到扩展的?

* 这个契约还会用在Dubbo的哪些地方? 

* 为什么写业务代码的时候, 感受不到任何网络交互?

* Consumer为什么能正确识别?

  