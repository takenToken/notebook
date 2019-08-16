docker能做什么？
    1.可以解决虚拟机解决的问题，同时可以解决虚拟机占用资源过高而无法解决的问题
    2.隔离应用依赖
    3.创建应用镜像并进行复制
    4.创建容易分发的即启即用的应用
    5.允许实例简单、快速的扩展
    6.测试应用并随后销毁它们

想法: 创建程序可移植的轻量级容器
概念: 镜像、容器、链接、数据卷

    镜像：类似虚拟机的快照
    容器：类似快照中创建容器，应用运行到容器里
    链接：两个容器链接在一起，容器会产生一个随机的ip，内部的本地局域网
    数据卷：让你可以不受容器的生命周期影响而进行数据的持久化，docker允许你定义应用部分和数据部分
docker最大思维变化：容器应该是短暂的一次性的

union文件系统，是由union装载来达到一个分层的累积变化，文件系统可以被装载在其他文件系统之上，
其结果就是一个分层的积累变化。每个装载的文件系统表示前一个文件系统之后的变化集合，就像是一个diff。



---------------------------docker 地址----------------------------------------
1.配置访问https地址信任，因为docker容器不允许你直接pull https协议下应用， 你必须信任你配置的地址，允许你访问。
insecure registries:
    registry.eyd.com:5000

2.配置dokcer镜像地址 registry mirrors
    自己阿里账号docker加速地址: https://pee6w651.mirror.aliyuncs.com

3.mvn docker:build 插件默认监听了localhost:2375，  因为docker默认是未开启远程监听，所以不能远程访问,
mvn插件是必须要需要配置远程访问docker的。

在mac下面，增加如下配置，就能开启远程监听
    export DOCKER_HOST=unix:///var/run/docker.sock

提示：
    1.imageName必须符合正则[a-z0-9-_.]，否则将会构建失败
    2.插件默认使用localhost:2375去连接Docker，如果你的Docker端口不是2375，需要配置环境变量DOCKER_HOST=tcp://<host>:2375


--我们的项目使用的是java:8作为基础镜像，从官方网站拉去下来的jdk8的镜像，并不叫java:8，需要针对镜像打一个tag，镜像名为java,tag名字叫8
命令: docker tag fiadliel/java8-jre java:8

--运行命令
docker run -d --name eyd-register-center -p 50012:50012 eyd-register-center:3.0.1-SNAPSHOT


4.mac 系统下，没有联网配置网络的情况一下，使用的lo0网络， 默认是127.0.0.1 代表本身, 容器内部本身地址默认127.0.0.1 代表本身
需要新配置一个ip，用于容器内部与外部的区分

sudo ifconfig lo0 alias 10.200.10.1/24
sudo ifconfig lo0 delete 10.200.10.1 删除网络回路

----------------------ubuntu----------------------------------------------
http://114.215.138.254:2375
这里2375是docker监听的tcp端口， 默认并不打开。需要这样设置(以ubuntu为例)

1. 修改docker配置文件
sudo vim /etc/default/docker
添加以下内容:
DOCKER_OPTS="-H tcp://0.0.0.0:2375 -H unix:///var/run/docker.sock"

2. 重启docker
sudo restart docker
或者
sudo service docker restart

3. 查看docker端口
sudo netstat -ltnp | grep docker
tcp6       0      0 :::2375                 :::*                    LISTEN      6112/docker
警告: 开启docker监听的tcp端口非常不安全,而且你暴露了主机的IP地址，请设置防火墙!

------------------------------------------------------------------------------
查看mac下端口被占用命令: lsof -i tcp:80

-----------------------------docker通过配置jmx监控java应用-----------------------

ENV JAVA_OPTS="\
-Dcom.sun.management.jmxremote.rmi.port=9090 \
-Dcom.sun.management.jmxremote=true \
-Dcom.sun.management.jmxremote.port=9090 \
-Dcom.sun.management.jmxremote.ssl=false \
-Dcom.sun.management.jmxremote.authenticate=false \
-Dcom.sun.management.jmxremote.local.only=false \
-Djava.rmi.server.hostname=192.168.99.100"
EXPOSE 8080
EXPOSE 9090
ENTRYPOINT java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar /app.jar

其中ava.rmi.server.hostname为docker的地址
------------------------------------------------------------------------------
sona-token2 : e0c94d7926e1c7961ede1d42688052b5e5908477
