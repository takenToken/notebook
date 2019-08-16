## java.util.collections集合框架
* hash哈希算法: 哈希算法可以将任意长度的二进制值引用为较短的二进制值，把这个小的二进制值称为哈希值
* hashCode与equals区别
    - equals遵循规则：
    - 对称性：如果x.equals(y)返回是“true”，那么y.equals(x)也应该返回是“true”。
    - 反射性：x.equals(x)必须返回是“true”。
    - 类推性：如果x.equals(y)返回是“true”，而且y.equals(z)返回是“true”，那么z.equals(x)也应该返回是“true”。
    - 一致性：如果x.equals(y)返回是“true”，只要x和y内容一直不变，不管你重复x.equals(y)多少次，返回都是“true”。

* hashCode遵循如下规则：
    -  在一个应用程序执行期间，如果一个对象的equals方法做比较所用到的信息没有被修改的话，则对该对象调用hashCode方法多次，
        它必须始终如一地返回同一个整数。
    -  如果两个对象根据equals(Object o)方法是相等的，则调用这两个对象中任一对象的hashCode方法必须产生相同的整数结果。
    -  如果两个对象根据equals(Object o)方法是不相等的，则调用这两个对象中任一个对象的hashCode方法，不要求产生不同的整数结果。但如果能不同，则可能提高散列表的性能
* hashCode是java native方法，是用来计算对象的位置。

```java



hashCode在HashMap扮演的角色为寻域(寻找某个对象在集合中区域位置),hashCode可以将集合分成若干个区域，
每个对象都可以计算出他们的hash码，可以将hash码分组，每个分组对应着某个存储区域，
根据一个对象的hash码就可以确定该对象所存储区域，这样就大大减少查询匹配元素的数量，提高了查询效率.

//https://www.zhihu.com/question/20733617
//扰动函数,设计保证了对象的hashCode的32位值只要有一位发生改变，整个hash()返回值就会改变，高位的变化会反应到低位里
//为了混合原始哈希码的高位和低位，以此来加大低位的随机性，从而减少碰撞
static final int hash(Object key) {
    int h;
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
}
//取余获取key的在数组的下标位置
static int indexFor(int hash,int length){
    return hash & (length - 1)
}

```

* hashmap涉及知识要点
    * hashing的概念
    * HashMap中解决碰撞的方法。
        * 如果两个对象的HashCode一致，则通过增加数组或者TreeNode来存储HashCode一致的对象
    * equals()和hashCode()的应用，以及它们在HashMap中的重要性
        * HashCode不同则一定是不同对象，hashcode相同可能是同一对象
        * 两个对象地址不同则是不同对象，地址相同是同一个对象
    * 不可变对象的好处
    * HashMap多线程的条件竞争
    * 重新调整HashMap的大小

* **在1.7的JDK上HashMap实际上是一个“链表散列”的数据结构，即数组和链表的结合体**
* **在1.8的JDK上HashMap实际上是一个“链表加TreeNode”的数据结构,在发生很多碰撞的时候,TreeNode要比数组效率高**


```java

1.hashCode是为了寻找某对象在集合的区域位置,减少迭代查找。

static final int hash(Object key) {
    int h;
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
}

2.HashMap触发扩容条件: HashMap.Size >= Capacity(初始化大小,默认16) * LoadFactor(扩容因子,默认0.75)
HashMap的Resize经过两个步骤：
    扩容：创建一个新的Entry空数组，长度是原来的两倍
    ReHash：遍历原来的Entry数组，把所有的Entry重新Hash到新数组。因为长度扩大之后Hash的规则会发生变动，因此需要重新Hash。

//HashMap中解决碰撞的方法
3.HashMap的key的存储是采用Node数组存储,通过hashCode与数组的运算获取key的下标位置，如果有多个key拥有相同的下标位置，
则该位置下会产生单链表，通过循环单链表来获取value值。
如果单链的长度大于等于8，则采用树结构存储,以此来加快查询的效率

4.HashMap的get方法，tab[hashKey & (tab.length-1)]取余。

//重新调整HashMap的大小
5.HashMap通过 【当前大小】>= 【初始化大小】 * 【0.75增长因子】判断是否扩容，扩容以2的倍数进行扩容
例如： 初始化 16个， 增长因子是0.75
扩容大小为： 16 * 2 = 32

//不可变对象的好处
6.不可变对象,它的hashCode不会变，hashCode值会缓存，针对采用Hash存储的数据结构，减少了hashCode计算，从而提高效率

//HashMap多线程的条件竞争
7.多线程竞争出现环形链表，所以程序将会进入死循环。

```


* hashmap、concurrenthashmap底层实现和区别

```
//参考资料：http://www.importnew.com/28263.html
jdk 1.7的ConcurrentHashMap采用Segment数组 + 链表 + 数组，其主要通过ReentrantLock锁segment进行并发处理。
jdk 1.8的ConcurrentHashMap采用CAS + 链表 + 红黑树实现(+Node数组)

```

* Collection---->List----->(Vector \ ArryList \ LinkedList)
![image](https://note.youdao.com/yws/public/resource/cd8746ef85c2bd1dac3ca11ec681a27d/xmlnote/44D25B4E3E17438590FF051B3C974F64/8475)
![image](https://note.youdao.com/yws/public/resource/cd8746ef85c2bd1dac3ca11ec681a27d/xmlnote/EF31868B797444C5A6EDBBEEDF773181/8477)

```
Vector是ArrayList的线程安全版,内部大量运用了【synchronized】关键字

//ArrayList实现了RandomAccess接口，在使用for(int i = 0; i<100;i++){}这种循环遍历效率高。
实现RandomAccess接口的的List可以通过简单的for循环来访问数据比使用iterator访问来的高效快速

ArrayList内部是采用数组存储,支持动态扩容,默认初始化10个
如果存入的数据大于默认10个，则按照[oldCapacity + 2 * oldCapacity]大小扩容处理
如果需要扩容容积大于默认的扩容容积，则按照实际容积来扩容

//Arrays用的很多,添加移除元素都涉及到位置的移动
public static void (Object src,int srcPos,Object dest,int destPos,int length)
src:源数组；	srcPos:源数组要复制的起始位置；
dest:目的数组；	destPos:目的数组放置的起始位置；
length:复制的长度。


//LinkList
LinkedList 是一个继承于AbstractSequentialList的双向链表。它也可以被当作堆栈、队列或双端队列进行操作。
LinkedList 实现 List 接口，能进行队列操作。
LinkedList 实现 Deque 接口，即能将LinkedList当作双端队列使用。
ArrayList底层是由数组支持，而LinkedList 是由双向链表实现的，其中的每个对象包含数据的同时还包含指向链表中前一个与后一个元素的引用。

LinkedList中插入元素很快，而ArrayList中插入元素很慢
LinkedList中随机访问很慢，而ArrayList中随机访问很快

原因：
    LinkedList采用双端队列存储数据，在插入数据的时候直接在末尾插入，速度很快
    ArrayList采用数组存储数据，插入数据大于默认数组大小，则会进行扩容处理，扩容涉及到数据复制，复制数据会有性能损耗,导致插入速度慢于LinkedList
    ArrayList随机访问很快是因为实现了RandomAccess接口，JVM内部进行优化处理，所以for遍历效率高
    LinkedList随机访问慢是因为没有实现RandomAccess接口，内部采用双端队列需要循环遍历去查找



LinkedHashMap 继承HashMap拥有它的很多特性，在HashMap基础上增加双向链Entry<K,V>的before,after，控制插入或者访问顺序。
//https://blog.csdn.net/justloveyou_/article/details/71713781
LinkedHashMap采用的hash算法和HashMap相同，但是它重新定义了Entry。LinkedHashMap中的Entry增加了两个指针 before 和 after，它们分别用于维护双向链接列表。特别需要注意的是，next用于维护HashMap各个桶中Entry的连接顺序，before、after用于维护Entry插入的先后顺序的

private transient Entry<K,V> header;  // 双向链表的表头元素
private final boolean accessOrder;  //true表示按照访问顺序迭代，false时表示按照插入顺序
```

### WeekHashMap

```
引用类型主要分为4种：1、强引用；2、软引用；3、弱引用；4、虚引用
强引用就是永远不会回收掉被引用的对象，比如说我们代码中new出来的对象。
软引用表示有用但是非必需的，如果系统内存资源紧张，可能就会被回收；
弱引用表示非必需的对象，只能存活到下一次垃圾回收发生之前；
虚引用是最弱的，这个引用无法操作对象。
在java中有与之对应的对象：SoftReference(软引用）, WeakReference（弱引用）,PhantomReference（虚引用）。
在我们今天要研究的WeakHashMap中用WeakReference来实现
```

### Copy-On-Write
    - Copy-On-Write简称COW，是一种用于程序设计中的优化策略。其基本思路是，从一开始大家都在共享同一个内容，当某个人想要修改这个内容的时候，才会真正把内容Copy出去形成一个新的内容然后再改，这是一种延时懒惰策略。从JDK1.5开始Java并发包里提供了两个使用CopyOnWrite机制实现的并发容器,它们是CopyOnWriteArrayList和CopyOnWriteArraySet。CopyOnWrite容器非常有用，可以在非常多的并发场景中使用到。
* CopyOnWrite容器即写时复制的容器
    - 通俗的理解是当我们往一个容器添加元素的时候，不直接往当前容器添加，而是先将当前容器进行Copy，复制出一个新的容器，然后新的容器里添加元素，添加完元素之后，再将原容器的引用指向新的容器。这样做的好处是我们可以对CopyOnWrite容器进行并发的读，而不需要加锁，因为当前容器不会添加任何元素。所以CopyOnWrite容器也是一种读写分离的思想，读和写不同的容器
* CopyOnWrite的应用场景
    - CopyOnWrite并发容器用于**读多写少的并发场景**。比如白名单，黑名单，商品类目的访问和更新场景，假如我们有一个搜索网站，用户在这个网站的搜索框中，输入关键字搜索内容，但是某些关键字不允许被搜索。这些不能被搜索的关键字会被放在一个黑名单当中，黑名单每天晚上更新一次。当用户搜索时，会检查当前关键字在不在黑名单当中，如果在，则提示不能搜索
