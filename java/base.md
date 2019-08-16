## 基础知识

### 关键字transient

* transient：设置不序列化的字段，例如：针对敏感信息用户密码、身份证信息等字符不变传输。
* transient：修饰变量，而不能修饰方法和类
* transient：修饰的变量不再能被序列化，一个静态变量不管是否被transient修饰，均不能被序列化
* 对象的序列化可以通过实现两种接口来实现，若实现的是Serializable接口，则所有的序列化将会自动进行，
若实现的是Externalizable接口，则没有任何东西可以自动序列化，需要在writeExternal方法中进行手工指定所要序列化的变量，这与是否被transient修饰无关。


```java
class User implements Serializable {
    private static final long serialVersionUID = 8294180014912103005L;

    private transient String passwd;

}
```

### 静态内部类
* 当外部类需要使用内部类，而内部类无需外部类资源，并且内部类可以单独创建的时候会考虑采用静态内部类的设计.
* 静态和非静态区别：
    * 非静态OutClass.InnerClass obj = outClassInstance.new InnerClass(); //注意是外部类实例.new，内部类
    * AAA.StaticInner in = new AAA.StaticInner();//注意是外部类本身，静态内部类


```java
/* 下面程序演示如何在java中创建静态内部类和非静态内部类 */
class OuterClass{
   private static String msg = "GeeksForGeeks";

   // 静态内部类
   public static class NestedStaticClass{

       // 静态内部类只能访问外部类的静态成员
       public void printMessage() {

         // 试着将msg改成非静态的，这将导致编译错误
         System.out.println("Message from nested static class: " + msg);
       }
    }

    // 非静态内部类
    public class InnerClass{

       // 不管是静态方法还是非静态方法都可以在非静态内部类中访问
       public void display(){
          System.out.println("Message from non-static nested class: "+ msg);
       }
    }
}
```


### 关键字private、friendly、protected、public

```java
private 私有权限。只有自己能用。
friendly 包权限。同一个包下的可用。
protected 继承权限。（是包权限的扩展，子女类也可使用）。
public 谁都可以用。
```

### 基础运算符

```java
//运算符

& 取余：只要有一个是0就算成0,相同为1
^ 异或：二进制值相同的为0,不同的为1

<< 左移运算符 《除》
>> 右移运算符 《乘》

```

### 数组
* 数组是java的特殊类,由JVM直接实现
* 声明数组：就是告诉计算机数组的类型是什么。有两种形式：int[] array、int array[]
* 告诉计算机需要给该数组分配多少连续的空间，记住是连续的。array = new int[10];
* 赋值就是在已经分配的空间里面放入数据。array[0] = 1 、array[1] = 2……其实分配空间和赋值是一起进行的，也就是完成数组的初始化


### 并发知识
* java锁
    - 重量级锁(悲观锁)synchronized:
        - 定义：内置锁是可重入锁，系统维护一个计数器和所有线程，当计数器为0则说明可竞争进入方法.
        - 关键字：synchronized
    - 自旋锁(悲观锁)
        - 类型：普通自旋锁和自适应自旋锁
        - 普通自旋锁和自适应自旋锁区别：普通自旋旋转次数是人为预先设定，自适应则是系统自身根据执行情况调整旋转次数
        - 线程挂起代价比较高，需要保存当前线程执行状态，执行时候加载线程状态
        - 自旋锁认为竞争时间短暂的挂起线程代价太高了,则通过for/while方式空转，不让线程进行挂起操作，如果自旋次数达到一定阈值，
        则转换成重量级锁synchronized。
    - 轻量级锁(乐观锁): CAS+版本号(比较交互)， 加版本号解决ABA的问题
    - 偏向锁：“锁总是同一个线程持有，很少发生竞争”，也就是说锁总是被第一个占用他的线程拥有，这个线程就是锁的偏向线程，
        那么只需要在锁第一次被拥有的时候，记录下偏向线程ID。这样偏向线程就一直持有着锁，直到竞争发生才释放锁。以后每次同步，检查锁的偏向线程ID与当前线程ID是否一致，如果一致直接进入同步，退出同步也，无需每次加锁解锁都去CAS更新对象头，如果不一致意味着发生了竞争，锁已经不是总是偏向于同一个线程了，这时候需要锁膨胀为轻量级锁，才能保证线程间公平竞争锁。

* 指令重排,在初始化变量的时候不一定是按照顺序初始化的，可能jvm会进行重排来达到系统更优
* 不可变对象一定是线程安全
    - 创建对象后的状态就不能修改
    - 对象的所有作用域都是final类型
    - 对象是正确创建的(在对象的创建期间，this引用没有逸出)
* volatile 内存可见性, 某个线程修改改对象值，其它线程立马得到修改的值
* 线程封闭ThreadLocal 线程内对象, 避免多线程直接竞争



### 关键字volatile
* volatile：保证线程之间修改的可见性

```java
volatile修饰的变量，保证修改的值立即被更新到主存
//例子
public volatile String userName;

作用：
    1、保证此变量对所有的线程的可见性
    2、禁止指令重排序优化。有volatile修饰的变量，赋值后多执行了一个“load addl $0x0, (%esp)”操作，
    这个操作相当于一个内存屏障（指令重排序时不能把后面的指令重排序到内存屏障之前的位置），只有一个CPU访问内存时，
    并不需要内存屏障；（什么是指令重排序：是指CPU采用了允许将多条指令不按程序规定的顺序分开发送给各相应电路单元处理）

使用条件：
    对变量的写操作不依赖于当前值。
    该变量没有包含在具有其他变量的不变式中
场景：
    //https://blog.csdn.net/vking_wang/article/details/9982709
    1、状态模式: 用于指示一个事件的启动和停止,通常只有一种状态转换
    2、一次性安全发布（one-time safe publication）
    3、独立观察（independent observation）
    4、"volatile bean" 模式
    5、开销较低的“读－写锁”策略

volatile boolean shutdownRequested;

public void shutdown() {
    shutdownRequested = true;
}

public void doWork() {
    while (!shutdownRequested) {
        // do stuff
    }
}

```

### synchronized内置锁
* 对象锁，控制并发，运用到方法、变量、代码块
* 可重入性: 对单个线程执行时重新进入同一个子程序仍然是安全的，不存在锁排队。
* 从设计上讲，当一个线程请求一个由其他线程持有的对象锁时，该线程会阻塞。当线程请求自己持有的对象锁时，如果该线程是重入锁，请求就会成功，否则阻塞
* 可重入的条件：
    * 不在函数内使用静态或全局数据
    * 不返回静态或全局数据，所有数据都由函数的调用者提供
    * 使用本地数据（工作内存），或者通过制作全局数据的本地拷贝来保护全局数据
    * 不调用不可重入函数
* 可重入锁的实现

```
每个锁关联一个线程持有者和一个计数器。当计数器为0时表示该锁没有被任何线程持有，
那么任何线程都都可能获得该锁而调用相应方法。当一个线程请求成功后，JVM会记下持有锁的线程，并将计数器计为1。
此时其他线程请求该锁，则必须等待。而该持有锁的线程如果再次请求这个锁，就可以再次拿到这个锁，同时计数器会递增。
当线程退出一个synchronized方法/块时，计数器会递减，如果计数器为0则释放该锁
```

### Java中CAS详解

```java
锁机制存在以下问题：
（1）在多线程竞争下，加锁、释放锁会导致比较多的上下文切换和调度延时，引起性能问题。
（2）一个线程持有锁会导致其它所有需要此锁的线程挂起。
（3）如果一个优先级高的线程等待一个优先级低的线程释放锁会导致优先级倒置，引起性能风险。

volatile是不错的机制，但是volatile不能保证原子性。因此对于同步最终还是要回到锁机制上来。

独占锁是一种悲观锁，synchronized就是一种独占锁，会导致其它所有需要锁的线程挂起，等待持有锁的线程释放锁。
而另一个更加有效的锁就是乐观锁。所谓乐观锁就是，每次不加锁而是假设没有冲突而去完成某项操作，如果因为冲突失败就重试，直到成功为止。
乐观锁用到的机制就是CAS，Compare and Swap。

CAS: 比较并交换
CAS 操作包含三个操作数 —— 内存位置（V）、预期原值（A）和新值(B),如果内存位置的值与预期原值相匹配，那么处理器会自动将该位置值更新为新值 。
否则，处理器不做任何操作

CAS目的：
    利用CPU的CAS指令，同时借助JNI来完成Java的非阻塞算法。其它原子操作都是利用类似的特性完成的。而整个J.U.C都是建立在CAS之上的，
    因此对于synchronized阻塞算法，J.U.C在性能上有了很大的提升

CAS存在的问题：
1.  ABA问题。因为CAS需要在操作值的时候检查下值有没有发生变化，如果没有发生变化则更新，但是如果一个值原来是A，变成了B，又变成了A，
那么使用CAS进行检查时会发现它的值没有发生变化，但是实际上却变化了。ABA问题的解决思路就是使用版本号。在变量前面追加上版本号，
每次变量更新的时候把版本号加一，那么A－B－A 就会变成1A-2B－3A

2. 循环时间长开销大。自旋CAS如果长时间不成功，会给CPU带来非常大的执行开销。如果JVM能支持处理器提供的pause指令那么效率会有一定的提升，
pause指令有两个作用，第一它可以延迟流水线执行指令（de-pipeline）,使CPU不会消耗过多的执行资源，延迟的时间取决于具体实现的版本，
在一些处理器上延迟时间是零。第二它可以避免在退出循环的时候因内存顺序冲突（memory order violation）
而引起CPU流水线被清空（CPU pipeline flush），从而提高CPU的执行效率


3. 只能保证一个共享变量的原子操作。当对一个共享变量执行操作时，我们可以使用循环CAS的方式来保证原子操作，但是对多个共享变量操作时，
循环CAS就无法保证操作的原子性，这个时候就可以用锁，或者有一个取巧的办法，就是把多个共享变量合并成一个共享变量来操作。
比如有两个共享变量i＝2,j=a，合并一下ij=2a，然后用CAS来操作ij。从Java1.5开始JDK提供了AtomicReference类来保证引用对象之间的原子性，
你可以把多个变量放在一个对象里来进行CAS操作

实现模式：
    首先，声明共享变量为volatile；
    然后，使用CAS的原子条件更新来实现线程之间的同步；
    同时，配合以volatile的读/写和CAS所具有的volatile读和写的内存语义来实现线程之间的通信。

```

## java.util.concurrent并发框架
### 多线程
* **无状态对象一定是线程安全的**
* **要保持状态的一致性，就需要在单个原子操作中更新所有相关的状态变量**
* 内置锁<synchronized关键字>同步代码块
* ==内置锁是重入性：同一个线程允许多次调用已由它自己持有锁的方法==
* 每个共享的和可变的变量都应该只由一个锁来保护，从而使维护人员知道是哪一个锁
* 加锁的含义不仅仅局限于互斥行为,还包括内存可见性，为了确保所有线程都能看到共享的变量的最新值，所有执行读操作或者写操作的线程都必须在同一个锁上同步
* 不要在构造过程中使用this引用逸出
* 不可变对象一定是线程安全的

### ReentrantLock锁
* 可重入锁ReentrantLock锁：通过<volatile可见性>、<CPU的CAS比较交换指令>代码方式实现锁机制，与synchronized关键字不同
    * ReentrantLock分"公平模型"和"非公平模型"两种
    * 公平模型：当锁处于无线程占有的状态，在其他线程抢占该锁的时候，都需要先进入队列中等待
    * 非公平锁：当锁处于无线程占有的状态，此时其他线程和在队列中等待的线程都可以抢占该锁。
* 从性能角度ReentrantLock性能要比synchronized要好，ReentrantLock也提供高级特性。
* 参考文章《[Java中的ReentrantLock和synchronized两种锁定机制的对比](https://blog.csdn.net/fw0124/article/details/6672522)》

```java
class X {
    private final ReentrantLock lock = new ReentrantLock();
    // ...

    public void m() {
      lock.lock();  // block until condition holds
      try {
        // ... method body
      } finally {
        lock.unlock()
      }
    }
  }
```

### 关键类
* ScheduledExecutorService 多线程并行处理定时任务时，Timer 运行多个 TimeTask 时，只要其中之一没有捕获 抛出的异常，其它任务便会自动终止运行，使用 ScheduledExecutorService 则没有这个问题
* DateUtils 工具类 LocalDateTime 代替 Calendar， DateTimeFormatter 代替 Simpledateformatter
* ThreadPoolExecutor 避免自定义创建线程，多用线程池方式，避免重复创建大量线程导致“上下文切换”资源浪费
* CountDownLatch 异步转同步操作




## 其它
* string 、stringbuffer 、springbuilder区别
    * 运行速度快慢为：StringBuilder > StringBuffer > String
    * String为字符串常量，而StringBuilder和StringBuffer均为字符串变量，即String对象一旦创建之后该对象是不可更改的，但后两者的对象是变量，是可以更改的.
    * 在线程安全上，StringBuilder是线程不安全的，而StringBuffer是线程安全的

```
String：适用于少量的字符串操作的情况
StringBuilder：适用于单线程下在字符缓冲区进行大量操作的情况
StringBuffer：适用多线程下在字符缓冲区进行大量操作的情况
```


## 基础算法
* 二叉树
* LRU算法(Least recently used,最近最少使用)

### 红黑树与AVL树
* 红黑树的插入删除效率高与AVL树。
* AVL树是高度平衡的,查询效率比红黑树高。
* 红黑树的查询性能略微逊色于AVL树，因为他比avl树会稍微不平衡最多一层，也就是说红黑树的查询性能只比相同内容的avl树最多多一次比较，但是，红黑树在插入和删除上完爆avl树，avl树每次插入删除会进行大量的平衡度计算，而红黑树为了维持红黑性质所做的红黑变换和旋转的开销，相较于avl树为了维持平衡的开销要小得多。
* AVL更平衡，结构上更加直观，时间效能针对读取而言更高；维护稍慢，空间开销较大。
* 红黑树，读取略逊于AVL，维护强于AVL，空间开销与AVL类似，内容极多时略优于AVL，维护优于AVL。
* 基本上主要的几种平衡树看来，红黑树有着良好的稳定性和完整的功能，性能表现也很不错，综合实力强，在诸如STL的场景中需要稳定表现。

### 类加载过程
* Bootstrap类加载器先java本身的类，再外部ExtStrap类加载器加载指定目录下的类，最后应用Application加载器加载应用内的java类。
* 双亲委派机制

### 线程池
#### 概述
* ThreadPoolExecutor作为java.util.concurrent包对外提供基础实现，以内部线程池的形式对外提供管理任务执行，线程调度，线程池管理等等服务；
* Executors方法提供的线程服务，都是通过参数设置来实现不同的线程池机制。
* 先来了解其线程池管理的机制，有助于正确使用，避免错误使用导致严重故障。同时可以根据自己的需求实现自己的线程池
* 核心参数：
    * corePoolSize 核心线程池大小
    * maximumPoolSize 最大线程池大小
    * keepAliveTime 线程池中超过corePoolSize数目的空闲线程最大存活时间；可以allowCoreThreadTimeOut(true)使得核心线程有效时间
    * TimeUnit keepAliveTime时间单位
    * workQueue 阻塞任务队列
    * threadFactory 	新建线程工厂
    * RejectedExecutionHandler 当提交任务数超过maxmumPoolSize+workQueue之和时，任务会交给RejectedExecutionHandler来处理

```
重点讲解：
其中比较容易让人误解的是：corePoolSize，maximumPoolSize，workQueue之间关系。

1.当线程池小于corePoolSize时，新提交任务将创建一个新线程执行任务，即使此时线程池中存在空闲线程。
2.当线程池达到corePoolSize时，新提交任务将被放入workQueue中，等待线程池中任务调度执行
3.当workQueue已满，且maximumPoolSize>corePoolSize时，新提交任务会创建新线程执行任务
4.当提交任务数超过maximumPoolSize时，新提交任务由RejectedExecutionHandler处理
5.当线程池中超过corePoolSize线程，空闲时间达到keepAliveTime时，关闭空闲线程
6.当设置allowCoreThreadTimeOut(true)时，线程池中corePoolSize线程空闲时间达到keepAliveTime也将关闭
```

### 连接池
* 略

### IO、NIO
* Linux有五种IO模型： 同步阻塞I/O模型、非阻塞I/O模型、多路复用I/O模型、异步IO模型、信号驱动I/O模型
* InputStream/OutStream 阻塞模式


## RxJava
