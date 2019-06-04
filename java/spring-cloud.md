## spring-cloud的客户端负载均衡、Feign熔断、熔断监控

## 熔断断路器：Hystrix客户端
* 断路器工作方式向家里的电路保险丝一样，达到某个阈值则会自动断开，防止烧坏家电.
* Hystrix中的默认值为5秒内的20次故障，会自动断开并调用回调函数Fallback.
![image](https://note.youdao.com/yws/public/resource/cd8746ef85c2bd1dac3ca11ec681a27d/xmlnote/3CA212A5BF6D4A70AC9B4A8E6A421577/10660)

### 配置hystrix步骤
* 引入hystrix的jar包

```java
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-cloud-starter-hystrix</artifactId>
</dependency>
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

* 加入启动注解@EnableHystrix

```java
/**
 * @author tan.bin
 */

@PetrelSpringBootApplication
@EnableEurekaClient
@EnableScheduling
@EnableFeignClients(basePackages = {"com.belle.petrel.common.*",
        "com.belle.petrel.email",
        "com.belle.petrel.shortmsg.client"})
@MapperScan("com.belle.petrel.*.mapper")
@EnableHystrix
public class EmailApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmailApplication.class, args);
    }

}
```

* 编写接口代码并带有回调函数

```java
/**
 * 发送短信
 */
@FeignClient(name = Providers.PETREL_SHORTMSG_API, fallback = MsgApi.MsgApiFallback.class)
public interface MsgApi {

    @RequestMapping(value = "/petrel-shortmsg-api/shortmsg/api/msg/send.json", method = {RequestMethod.GET})
    Map<String, Object> send(@RequestParam(value = "to") String[] to,
                             @RequestParam(value = "content") String content);

    @RequestMapping(value = "/petrel-shortmsg-api/shortmsg/api/msg/sendMap.json", method = {RequestMethod.POST})
    Map<String, Object> sendMap(@RequestBody Map<String, String> params);

    @RequestMapping(value = "/petrel-shortmsg-api/shortmsg/api/msg/selectSendData.json", method = {RequestMethod.POST})
    Map<String, Object> selectSendData(@RequestBody Map<String, String> params);

    @RequestMapping(value = "/petrel-shortmsg-api/shortmsg/api/msg/selectRecData.json", method = {RequestMethod.POST})
    Map<String, Object> selectRecData(@RequestBody Map<String, String> params);


    /**
     * 回退
     */
    @Component
    static class MsgApiFallback implements MsgApi{
        @Override
        public Map<String, Object> send(String[] to, String content) {
            Map<String, Object> resultMap = ResultMapUtils.getErrorResultMap("1001", "短信接口异常,发送失败!");
            return resultMap;
        }

        @Override
        public Map<String, Object> sendMap(Map<String, String> params) {
            Map<String, Object> resultMap = ResultMapUtils.getErrorResultMap("1002", "短信接口异常,发送失败!");
            return resultMap;
        }

        @Override
        public Map<String, Object> selectSendData(Map<String, String> params) {
            Map<String, Object> resultMap = ResultMapUtils.getErrorResultMap("1003", "短信接口异常,查询发送记录失败!");
            return resultMap;
        }

        @Override
        public Map<String, Object> selectRecData(Map<String, String> params) {
            Map<String, Object> resultMap = ResultMapUtils.getErrorResultMap("1004", "短信接口异常,查询接收记录失败!");
            return resultMap;
        }
    }
}

```

### Hystrix熔断异常
* 熔断异常说明

![image](https://note.youdao.com/yws/public/resource/cd8746ef85c2bd1dac3ca11ec681a27d/xmlnote/CAA255311F5846C19F3A7BA8100D6AB4/11151)


## feign配置

```yml
什么时候应该Fallback
2XX，成功，这个不用再讨论。
5XX，也相当明确，直接Fallback，这个也不用讨论。
4XX，可以一律认为是业务逻辑异常（或者更精确的说，可以认为4XX中的某几个是业务异常）。这时候，应该是用if/else来处理这个异常，而不应该动用Hystrix来Fallback。

Feign在默认情况下，对于非2XX，都认为是异常。这个地方是有问题的。特别是对于404这种非常容易抛出的业务异常来说，没两下就circuit break了。

Feign的Issue里已经有人提过这个问题，后面的版本中已经提供了一个参数:decode404。

熔断超时时间
hystrix:
  command:
    default:
      execution:
        isolation:
          thread:
            timeoutInMilliseconds: 20000

#设置从不超时
hystrix:
  command:
    default:
      execution:
        timeout:
          enabled: false

 # 使用Hystrix Metrics Stream必备
 management:
   endpoints:
     web:
       exposure:
         include: hystrix.stream

feign:
  hystrix:
    enabled: true
  httpclient:
    enabled: true
  #请求request和响应response GZIP压缩支持
  compression:
    request:
      enabled: true
      mime-types: text/xml,application/xml,application/json
      min-request-size: 2048
    response:
      enabled: true
#设置feign api超时时间 10秒为链接超时， 120秒为读取超时
ribbon:
  ConnectTimeout: 10000
  ReadTimeout: 120000
```

## 熔断仪表盘中心

```java
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-hystrix-dashboard</artifactId>
</dependency>

<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```
* 监控访问地址【hystrix】：http://localhost:10110/petrel-hystrix-api/hystrix/
* 被监控(客户端)的地址【hystrix.stream】：http://localhost:10061/petrel-email-api/hystrix.stream

### 情况说明
* 访问被监控地址无数据显示一直ping.... ,则是因为没有访问带熔断的Feign接口。
* 熔断监控是实时数据收集.
* hystrix-dashboard查看单个熔断数据，其实际意义不大,可以使用[turbine]做整合工作，收集各应用的状态。


## Ribbon客户端负载均衡器
### 重要接口
* Load

```java
/**
 * Represents a client side load balancer
 * @author Spencer Gibb
 */
public interface LoadBalancerClient extends ServiceInstanceChooser {

	<T> T execute(String serviceId, LoadBalancerRequest<T> request) throws IOException;


	<T> T execute(String serviceId, ServiceInstance serviceInstance, LoadBalancerRequest<T> request) throws IOException;


	URI reconstructURI(ServiceInstance instance, URI original);
}
```

### 主要组件
* ServerList
用于获取地址列表。它既可以是静态的(提供一组固定的地址)，也可以是动态的(从注册中心中定期查询地址列表)。

* ServerListFilter
仅当使用动态ServerList时使用，用于在原始的服务列表中使用一定策略过虑掉一部分地址。

* IRule
选择一个最终的服务地址作为LB结果。选择策略有轮询、根据响应时间加权、断路器(当Hystrix可用时)等。

* Ribbon在工作时首选会通过ServerList来获取所有可用的服务列表，然后通过ServerListFilter过虑掉一部分地址，最后在剩下的地址中通过IRule选择出一台服务器作为最终结果。


### 负载策略描述
* AbstractLoadBalancerRule：负载均衡策略的抽象类，在该抽象类中定义了负载均衡器ILoadBalancer对象，该对象能够在具体实现选择服务策略时，获取到一些负载均衡器中维护的信息来作为分配依据，并以此设计一些算法来实现针对特定场景的高效策略。

* RandomRule：随机策略，从服务实例清单中随机选择一个服务实例。获得可用实例列表upList和所有实例列表allList，并通过rand.nextInt(serverCount)函数来获取一个随机数，并将该随机数作为upList的索引值来返回具体实例。

* RoundRobinRule：轮询策略，按照线性轮询的方式依次选择每个服务实例。通过AtomicInteger nextServerCyclicCounter对象实现，每次进行实例选择时通过调用incrementAndGetModulo函数实现递增。

* RetryRule：重试策略，具备重试机制的实例选择。内部定义了RoundRobinRule，并实现了对RoundRobinRule进行反复尝试的策略，若期间能够选择到具体的服务实例就返回，若选择不到就根据设置的尝试结束时间为阈值，当超过该阈值后就返回null。

* WeightedResponseTimeRule：权重策略，根据实例的运行情况来计算权重，并根据权重来挑选实例，以达到更优的分配效果。通过定时任务为每个服务进行权重计算，平均响应时间小的权重区间（总平均响应时间-实例平均响应时间）就大，实力选择根据权重范围随机选择，落在哪个区间则选择哪个实例。

* BestAvailableRule：最佳策略，通过遍历负载均衡器中维护的所有服务实例，会过滤掉故障的实例，并找出并发请求数最小的一个，选出最空闲的实例。

* AvailabilityFilteringRule：可用过滤策略：先过滤出故障的或并发请求大于阈值一部分服务实例，然后再以线性轮询的方式从过滤后的实例清单中选出一个。

* ZoneAvoidanceRule：区域感知策略：使用主过滤条件（区域负载器，选择最优区域）对所有实例过滤并返回过滤后的实例清单，依次使用次过滤条件列表中的过滤条件对主过滤条件的结果进行过滤，判断最小过滤数（默认1）和最小过滤百分比（默认0），满足条件则使用RoundRobinRule选择实例。



## turbine监控
* 引入jar包

```java
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-turbine</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-netflix-turbine</artifactId>
</dependency>
```

* turbine的application.yml配置

```yml
turbine:
  aggregator:
    clusterConfig: default   # 指定聚合哪些集群，多个使用","分割，默认为default。可使用http://.../turbine.stream?cluster={clusterConfig之一}访问
  ### 配置Eureka中的serviceId列表，表明监控哪些服务
  appConfig: petrel-email-api,petrel-shortmsg-api
  clusterNameExpression: new String("default")
  # 1. clusterNameExpression指定集群名称，默认表达式appName；此时：turbine.aggregator.clusterConfig需要配置想要监控的应用名称
  # 2. 当clusterNameExpression: default时，turbine.aggregator.clusterConfig可以不写，因为默认就是default
  # 3. 当clusterNameExpression: metadata['cluster']时，假设想要监控的应用配置了eureka.instance.metadata-map.cluster: ABC，则需要配置，同时turbine.aggregator.clusterConfig: ABC
```


### 熔断参数说明
* 默认情况下10秒内10个错误则触发熔断，熔断恢复默认时间5秒，失败率达50%后熔断。
*


```
https://www.jianshu.com/p/3dfe6855e1c5
https://www.colabug.com/3868613.html
https://blog.csdn.net/hry2015/article/details/78554846
https://www.jianshu.com/p/138f92aa83dc


Hystrix相关的常用配置信息：
超时时间（默认1000ms，单位：ms）
hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds
hystrix.command.HystrixCommandKey.execution.isolation.thread.timeoutInMilliseconds
线程池核心线程数
hystrix.threadpool.default.coreSize（默认为10）
Queue
hystrix.threadpool.default.maxQueueSize（最大排队长度。默认-1，使用SynchronousQueue。其他值则使用 LinkedBlockingQueue。如果要从-1换成其他值则需重启，即该值不能动态调整，若要动态调整，需要使用到下边这个配置）
hystrix.threadpool.default.queueSizeRejectionThreshold（排队线程数量阈值，默认为5，达到时拒绝，如果配置了该选项，队列的大小是该队列）
注意：如果maxQueueSize=-1的话，则该选项不起作用
断路器
hystrix.command.default.circuitBreaker.requestVolumeThreshold（当在配置时间窗口内达到此数量的失败后，进行短路。默认20个）
hystrix.command.default.circuitBreaker.sleepWindowInMilliseconds（短路多久以后开始尝试是否恢复，默认5s）
hystrix.command.default.circuitBreaker.errorThresholdPercentage（出错百分比阈值，当达到此阈值后，开始短路。默认50%）
fallback
hystrix.command.default.fallback.isolation.semaphore.maxConcurrentRequests（调用线程允许请求HystrixCommand.GetFallback()的最大数量，默认10。超出时将会有异常抛出，注意：该项配置对于THREAD隔离模式也起作用）
```


## spring-cloud-zuul网关应用
### 实现不重启应用，动态加载zuul下配置
* 应用要集成spring-cloud-config配置中心
* 增加@refreshScope注解

```
@Bean
@RefreshScope
@ConfigurationProperties("zuul")
public ZuulProperties zuulProperties(){
    return new ZuulProperties();
}
```
* 在git上更改zuul下配置并提交
* 调用刷新接口 http://localhost:10007/petrel/refresh，成功则会返回修改内容

```
["config.client.version","zuul.routes.petrel-email-api.path"]
```

* 如果出现404,是因为默认开启安全认证，所有不能直接调用,请调整如下配置,或者集成spring安全功能

```
management:
  security:
    enabled: false
```

* zuul请求的debug

```
设置
zuul.debug.request=true  #如果设置了这个，默认所有的请求都会debug

zuul.include-debug-header: true
未设置zuul.debug.request=true，可以

用zuul_host:zuul_port/路径?debug=true debug你的指定请求


logging:
  level:
    com.netflix: DEBUG

```

* Hystrix 使用 Archaius 来实现动态配置


## 配置中心:携程Apollo/阿里Nacos
