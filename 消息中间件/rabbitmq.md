## RabbitMQ重要概念
* 路由器、交换器、队列、通道、绑定
* 消息送推送端-->路由/交换器-->队列-->消费者
* Exchange类型：Direct 、 Fanout 、Topic、Header

## RabbitMQ集群
* [参考资料](https://www.jianshu.com/p/97fbf9c82872?utm_campaign=maleskine&utm_content=note&utm_medium=seo_notes&utm_source=recommendation)
* 普通集群和HA集群，HA集群是建立在普通集群之上，进行队列镜像保证更高可用性.
* 概念
    - 内存节点：只保存状态到内存，例外情况是：持久的 queue 的内容将被保存到磁盘。
    - 磁盘节点：保存状态到内存和磁盘。
    - 集群中可以存在多个磁盘节点，磁盘节点越多整个集群可用性越好，但是集群整体性能不会线性增加，需要权衡考虑
    - 如果集群中只有内存节点，那么不能停止它们，否则所有状态和消息都会丢失。
* 普通集群： HAProxy + 一个磁盘节点RabbitMQ + 两个内存节点RabbitMQ
* HA集群：HAProxy + 两个磁盘(一主一从)节点RabbitMQ + 两个(一主一从)内存节点RabbitMQ
    - HA集群读写数据都在主节点上,从节点只是主节点的镜像，实现更高可用性

### 性能参数
* 开启Confirm的Ack机制，会大概降低60%的QPS
* 开启磁盘持久化会降低QPS
* 增加vhost数量，能提高QPS
* RabbitMQ的confirm是于生产者与MQ之间的确认机制，确保消息确实投递到了MQ。在许多对可靠性要求比较高的应用场景下都需要使用该机制确保消息不丢。测试显示，该功能开启与否对性能影响很大，在单台服务器上开8个queue，消息持久化，1kb大小的消息，不开confirm时QPS 28k，开启confirm后QPS降到21k

### 消息堆积处理方式
* 分析消息堆积原因
* 扩展临时消息队列，增加临时消费者能力，快速处理掉消息，削减峰值
