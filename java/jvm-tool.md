## JVM命令
jmap是一个可以输出所有内存中对象的工具，甚至可以将VM 中的heap，以二进制输出成文本。
命令：jmap -dump:format=b,file=heap.bin <pid>
file：保存路径及文件名
pid：进程编号
* jmap -histo:live  pid| less :堆中活动的对象以及大小
* jmap -heap pid : 查看堆的使用状况信息
* 实时查看gc情况

```bash
-- 5代表进程号
-- 1000代表间隔时间
-- 10代表打印次数
jstat -gcutil 5 1000 10

S0C：第一个幸存区的大小
S1C：第二个幸存区的大小
S0U：第一个幸存区的使用大小
S1U：第二个幸存区的使用大小
EC：伊甸园区的大小
EU：伊甸园区的使用大小
OC：老年代大小
OU：老年代使用大小
MC：方法区大小
MU：方法区使用大小
CCSC:压缩类空间大小
CCSU:压缩类空间使用大小
YGC：年轻代垃圾回收次数
YGCT：年轻代垃圾回收消耗时间
FGC：老年代垃圾回收次数
FGCT：老年代垃圾回收消耗时间
GCT：垃圾回收消耗总时间

  S0     S1     E      O      M     CCS    YGC     YGCT    FGC    FGCT     GCT
  0.00  50.00  75.95  52.58  96.04  93.56   1249   31.148     4    1.983   33.131
 62.50   0.00   1.38  52.58  96.04  93.56   1250   31.199     4    1.983   33.181
 62.50   0.00   2.04  52.58  96.04  93.56   1250   31.199     4    1.983   33.181
```

* 手动执行java的垃圾回收

```bash
jcmd <pid> GC.run
```

* CLOSE_WAIT

```bash
https://www.cnblogs.com/sunxucool/p/3449068.html
常用的三个状态是：ESTABLISHED 表示正在通信，TIME_WAIT 表示主动关闭，CLOSE_WAIT 表示被动关闭。
netstat -n | awk '/^tcp/ {++S[$NF]} END {for(a in S) print a, S[a]}'
//
netstat -atp|grep 8191 |wc -l
//可用
netstat -lan |grep 10003
```

* 线程状态

```bash
在dump 文件里，写法可能不太一样：

死锁，Deadlock（重点关注）
执行中，Runnable
等待资源，Waiting on condition（重点关注）
等待获取监视器，Waiting on monitor entry（重点关注）
对象等待中，Object.wait() 或 TIMED_WAITING
暂停，Suspended
阻塞，Blocked（重点关注）
停止，Parked
```


### 阿里arthas
[阿里arthas参考资料](https://mp.weixin.qq.com/s/eYFNUtDujpYCAk9-6ByV4Q)
* 步骤
    - wget https://alibaba.github.io/arthas/arthas-boot.jar
    - java jar arthas-boot.jar
    - 输入：dashboard
