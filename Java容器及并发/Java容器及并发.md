# 容器

### ArrayList

#### Arraylist 与 LinkedList 区别?

- **1. 是否保证线程安全：** `ArrayList` 和 `LinkedList` 都是不同步的，也就是不保证线程安全；

- **2. 底层数据结构：** `Arraylist` 底层使用的是 **`Object` 数组**；`LinkedList` 底层使用的是 **双向链表** 数据结构（JDK1.6之前为循环链表，JDK1.7取消了循环。注意双向链表和双向循环链表的区别，下面有介绍到！）

- **3. 插入和删除是否受元素位置的影响：** 

  ① `ArrayList` 采用数组存储，所以插入和删除元素的时间复杂度受元素位置的影响。 比如：执行`add(E e)`方法的时候， `ArrayList` 会默认在将指定的元素追加到此列表的末尾，这种情况时间复杂度就是O(1)。但是如果要在指定位置 i 插入和删除元素的话（`add(int index, E element)`）时间复杂度就为 O(n-i)。因为在进行上述操作的时候集合中第 i 和第 i 个元素之后的(n-i)个元素都要执行向后位/向前移一位的操作。 

  ② `LinkedList` 采用链表存储，所以对于`add(E e)`方法的插入，删除元素时间复杂度不受元素位置的影响，近似 O(1)(因其内部维护有存储表头表尾地址的变量)，如果是要在指定位置`i`插入和删除元素的话（`add(int index, E element`） 时间复杂度近似为O(n)因为需要先移动到指定位置再插入。

- **4. 是否支持快速随机访问：** `LinkedList` 不支持高效的随机元素访问，而 `ArrayList` 支持。快速随机访问就是通过元素的序号快速获取元素对象(对应于`get(int index)`方法)。

- **5. 内存空间占用：** ArrayList的空 间浪费主要体现在在list列表的结尾会预留一定的容量空间，而LinkedList的空间花费则体现在它的每一个元素都需要消耗比ArrayList更多的空间（因为要存放直接后继和直接前驱以及数据）。

  

  ==对于随机访问get和set，ArrayList优于LinkedList，因为LinkedList要移动指针。 
   对于新增和删除操作add和remove，LinedList比较占优势，因为ArrayList要移动数据。==

  虽然新增和删除操作下，LinkedList由于需要访问到index所以时间复杂度退化到O(n)，但实际操作下来，访问到index的速度还是比 Arraylist 元素向后位/向前移一位的操作 要快的。

#### Arraylist的扩容机制







### HashMap

#### **HashMap 的数据结构**

HashMap的主干是一个Node数组。Node是HashMap的基本组成单元，每一个Node包含一个key-value键值对。

简单来说，**HashMap由数组+链表组成的**，数组是HashMap的主体，链表则是主要为了解决哈希冲突而存在的，当链表长度大于8，则转化为红黑树

```java
static class Node<K,V> implements Map.Entry<K,V> {
   final int hash;
   final K key;
   V value;
   Node<K,V> next;
```

![111](Java容器及并发.assets/111.jpg)

#### JDK1.8后对hash算法和寻址算法

**hash算法优化**

```java
//重新计算哈希值
static final int hash(Object key) {
	int h;
    //key如果是null,新hashcode是0,否则,新的hashcode为原hashcode无符号右移16位后与原hashcode做异或运算
	return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
}
```

**寻址算法优化**

```java
index = hash&(length-1) //key的hash值和数组长度-1的32位二进制数进行与运算
```

先有的寻址算法优化再有的hash算法优化。

==古老的寻址方法是hash对数组长度取模，优化后的寻址算法能达到同样的效果且性能更好。==

而为什么要hashcode高低16位进行异或呢？因为当node数组长度比较小，而hashcode的值比较大的时候，直接hashcode & (node数组长度 - 1）运算，hashcode的高16位是不会参加运算的，因为node数组长度 - 1的高16位都是0。所以hashcode的高低16位进行异或，就达到了高16位和低16位都参与运算的效果，让hash算法更加均匀。

![image-20200418214012611](Java容器及并发.assets/image-20200418214012611.png)

==综合低16位与高16的影响，减少hash碰撞==

#### 扩容机制（为什么扩容是2的n次幂）

hashmap默认长度是16，负载因子是 0.75f，threshold =0.75*16=12，也就是说，插入 12 个键值对就会扩容。

**hashmap维护的node数组的长度永远是2的n次方**，在扩容时，会扩大到原来的两倍，因为使用的是**2的次幂扩展**，那么元素的位置要么保持不变，要么在原位置上偏移2的次幂。这里用到寻址算法优化

![1424165-20190522103950481-844633092](Java容器及并发.assets/1424165-20190522103950481-844633092.png)

上图可以看到，扩大2倍，相当于 n 左移一位，那么 n-1 在高位就多出了一个 1，此时与原散列值进行与运算，就多参与了一位，这个比特位要么是 0，要么是 1:

- 0 的话索引不变
- 1 的话索引就变成"原索引+oldCap"

==扩容时，利用 2 的次幂数值的二进制特点，既省去重新计算 hash 的时间，又把之前冲突的节点散列到了其他位置==

#### hashmap的get/put过程

此题可以组成如下连环炮来问

- 知道hashmap中put元素的过程是什么样么?
- 知道hashmap中get元素的过程是什么样么？
- 你还知道哪些hash算法？
- 说说String中hashcode的实现?(此题很多大厂问过)

***知道hashmap中put元素的过程是什么样么?***

对key的hashCode()做hash运算，计算index; 如果没碰撞直接放到bucket里； 如果碰撞了，以链表的形式存在buckets后； 如果碰撞导致链表过长(大于等于TREEIFY_THRESHOLD)，就把链表转换成红黑树(JDK1.8中的改动)； 如果节点已经存在就替换old value(保证key的唯一性) 如果bucket满了(超过load factor*current capacity)，就要resize。

***知道hashmap中get元素的过程是什么样么?***

对key的hashCode()做hash运算，计算index; 如果在bucket里的第一个节点里直接命中，则直接返回； 如果有冲突，则通过key.equals(k)去查找对应的Node;

- 若为树，则在树中通过key.equals(k)查找，O(logn)；
- 若为链表，则在链表中通过key.equals(k)查找，O(n)。

***你还知道哪些hash算法？***

先说一下hash算法干嘛的，Hash函数是指把一个大范围映射到一个小范围。把大范围映射到一个小范围的目的往往是为了节省空间，使得数据容易保存。 它是一种单向密码体制，即它是一个从明文到密文的不可逆的映射，只有加密过程，没有解密过程。同时，Hash函数可以将任意长度的输入经过变化以后得到固定长度的输出。比较出名的有MurmurHash、MD4、MD5等等

***说说String中hashcode的实现?(此题频率很高)***

```java
public int hashCode() {
    int h = hash;
    if (h == 0 && value.length > 0) {//value为将字符串截成的字符数组  
        char val[] = value;

        for (int i = 0; i < value.length; i++) {
            h = 31 * h + val[i];
        }
        hash = h;
    }
    return h;
}
```

String类中的hashCode计算方法还是比较简单的，就是以31为权，每一位为字符的ASCII值进行运算，用自然溢出来等效取模。

例如String msg = "abcd";  // 此时value[] = {'a','b','c','d'}  因此

for循环会执行4次

第一次：h = 31*0 + a = 97 

第二次：h = 31*97 + b = 3105 

第三次：h = 31*3105 + c = 96354 

第四次：h = 31*96354 + d = 2987074 

由以上代码计算可以算出 msg 的hashcode = 2987074  

哈希计算公式可以计为`s[0]*31^(n-1) + s[1]*31^(n-2) + ... + s[n-1]`

那为什么以31为质数呢?

主要是因为31是一个奇质数，所以`31*i=32*i-i=(i<<5)-i`，这种位移与减法结合的计算相比一般的运算快很多。

#### 为什么hashmap的在链表元素数量超过8时改为红黑树?

此题可以组成如下连环炮来问

- 知道jdk1.8中hashmap改了啥么?
- 为什么在解决hash冲突的时候，不直接用红黑树?而选择先用链表，再转红黑树?
- 我不用红黑树，用二叉查找树可以么?

***知道jdk1.8中hashmap改了啥么?***

- 由**数组+链表**的结构改为**数组+链表+红黑树**。
- 优化了高位运算的hash算法：h^(h>>>16)
- 扩容后，元素要么是在原位置，要么是在原位置再移动2次幂的位置，且链表顺序不变。

最后一条是重点，因为最后一条的变动，hashmap在1.8中，不会在出现死循环问题。

***为什么在解决hash冲突的时候，不直接用红黑树?而选择先用链表，再转红黑树?***

 因为红黑树需要进行左旋，右旋，变色这些操作来保持平衡，而单链表不需要。 当元素小于8个当时候，此时做查询操作，链表结构已经能保证查询性能。当元素大于8个的时候，此时需要红黑树来加快查询速度，但是新增节点的效率变慢了。

因此，如果一开始就用红黑树结构，元素太少，新增效率又比较慢，无疑这是浪费性能的。

***我不用红黑树，用二叉查找树可以么?*** 

可以。但是二叉查找树在特殊情况下会变成一条线性结构（这就跟原来使用链表结构一样了，造成很深的问题），遍历查找会非常慢。

#### HashMap 和 Hashtable 的区别

1. **线程是否安全：** HashMap 是非线程安全的，HashTable 是线程安全的；HashTable 内部的方法基本都经过`synchronized` 修饰。（如果你要保证线程安全的话就使用 ConcurrentHashMap 吧！）；

2. **效率：** 因为线程安全的问题，HashMap 要比 HashTable 效率高一点。另外，HashTable 基本被淘汰，不要在代码中使用它；

3. **对Null key 和Null value的支持：** HashMap 中，null 可以作为键，这样的键只有一个，可以有一个或多个键所对应的值为 null。。但是在 HashTable 中 put 进的键值只要有一个 null，直接抛出 NullPointerException。

4. **初始容量大小和每次扩充容量大小的不同 ：** 

   ①创建时如果不指定容量初始值，Hashtable 默认的初始大小为11，之后每次扩充，容量变为原来的2n+1。HashMap 默认的初始化大小为16。之后每次扩充，容量变为原来的2倍。

   ②创建时如果给定了容量初始值，那么 Hashtable 会直接使用你给定的大小，而 HashMap 会将其扩充为2的幂次方大小（HashMap 中的`tableSizeFor()`方法保证）。也就是说 HashMap 总是使用2的幂作为哈希表的大小。

5. **底层数据结构：** JDK1.8 以后的 HashMap 在解决哈希冲突时有了较大的变化，当链表长度大于阈值（默认为8）（将链表转换成红黑树前会判断，如果当前数组的长度小于 64，那么会选择先进行数组扩容，而不是转换为红黑树）时，将链表转化为红黑树，以减少搜索时间。Hashtable 没有这样的机制。

#### HashMap 多线程操作导致死循环问题

### HashSet

底层由HashMap来实现，HashSet使用成员对象来计算hashcode值，对于两个对象来说hashcode可能相同，所以equals()方法用来判断对象的相等性。

### ConcurrentHashMap 

JDK1.8以前： 采用分段锁 。put和 get **两次Hash**到达指定的HashEntry，第一次hash到达Segment,第二次到达Segment里面的Entry,然后在遍历entry链表。



JDK1.8以后：并发控制使用Synchronized和CAS来操作。put如果没有hash冲突就直接CAS插入，如果存在hash冲突，就加锁来保证线程安全。



#### ConcurrentHashMap 和 Hashtable 的区别

ConcurrentHashMap 和 Hashtable 的区别主要体现在实现线程安全的方式上不同。

- **底层数据结构：** 

  JDK1.7的 ConcurrentHashMap 底层采用 **分段的数组+链表** 实现，JDK1.8 采用的数据结构跟HashMap1.8的结构一样，数组+链表/红黑二叉树。

  Hashtable 和 JDK1.8 之前的 HashMap 的底层数据结构类似都是采用 **数组+链表** 的形式。

- **实现线程安全的方式（重要）：**

  ① **在JDK1.7的时候，ConcurrentHashMap（分段锁）** 对整个桶数组进行了分割分段(Segment)，每一把锁只锁容器其中一部分数据，多线程访问容器里不同数据段的数据，就不会存在锁竞争，提高并发访问率。 **到了 JDK1.8 的时候已经摒弃了Segment的概念，而是直接用 Node 数组+链表+红黑树的数据结构来实现，并发控制使用 synchronized 和 CAS 来操作。（JDK1.6以后 对 synchronized锁做了很多优化）** 整个看起来就像是优化过且线程安全的 HashMap，虽然在JDK1.8中还能看到 Segment 的数据结构，但是已经简化了属性，只是为了兼容旧版本；

  ② **Hashtable(同一把锁)** :使用 synchronized 来保证线程安全，效率非常低下。当一个线程访问同步方法时，其他线程也访问同步方法，可能会进入阻塞或轮询状态，如使用 put 添加元素，另一个线程不能使用 put 添加元素，也不能使用 get，竞争会越来越激烈效率越低。

# 并发

## 基本方法

### **Object.java**

==wait(), notify()和notifyAll()  需在上锁的方法/代码块中使用==

wait()：是让当前线程进入等待状态，同时**会让当前线程释放立刻它所持有的锁**。

notify()：唤醒在此对象锁上等待的单个线程，**等线程结束后再释放锁。**
notifyAll()：唤醒在此对象锁上等待的所有线程，**等线程结束后再释放锁。**

### **Thread.java**

==join(),sleep(),yield()==

join(): 把指定的线程添加到当前线程中，主线程等待join线程结束后再执行。

sleep():让出cpu给其他线程, 在一定时间内不参与cpu调度，**不释放锁**。

yield(): 让出cpu给其他线程, 并处于就绪状态。

## 基本概念

### 线程的生命周期和状态

![线程状态](Java容器及并发.assets/20190629144813607.png)

![在这里插入图片描述](Java容器及并发.assets/20190629165400894.png)





## JMM-Java内存模型

方法中的基本类型本地变量将直接存储在工作内存的栈帧结构中；（虚拟机栈）

引用类型的本地变量：引用存储在工作内存（虚拟机栈），实际存储在主内存；（堆）

成员变量、静态变量、类信息均会被存储在主内存中；（堆）

![img](Java容器及并发.assets/4222138-96ca2a788ec29dc2.webp)

 Java内存模型是围绕着并发编程中**原子性**、**可见性**、**有序性**这三个特征来建立的

### 内存交互

- lock   （锁定）：作用于主内存的变量，把一个变量标识为线程独占状态

- unlock （解锁）：作用于主内存的变量，它把一个处于锁定状态的变量释放出来，释放后的变量才可以被其他线程锁定

- read  （读取）：作用于主内存变量，它把一个变量的值从主内存传输到线程的工作内存中，以便随后的load动作使用

- load   （载入）：作用于工作内存的变量，它把read操作从主存中变量放入工作内存中

- use   （使用）：作用于工作内存中的变量，它把工作内存中的变量传输给执行引擎，每当虚拟机遇到一个需要使用到变量的值，就会使用到这个指令

- assign （赋值）：作用于工作内存中的变量，它把一个从执行引擎中接受到的值放入工作内存的变量副本中

- store  （存储）：作用于主内存中的变量，它把一个从工作内存中一个变量的值传送到主内存中，以便后续的write使用

- write 　（写入）：作用于主内存中的变量，它把store操作从工作内存中得到的变量的值放入主内存的变量中

  

### 内存模型三大特性

**原子性：**例如上面八项操作，在操作系统里面是不可分割的单元。被synchronized关键字或其他锁包裹起来的操作也可以认为是原子的。从一个线程观察另外一个线程的时候，看到的都是一个个原子性的操作。

**可见性：**每个工作线程都有自己的工作内存，所以当某个线程修改完某个变量之后，在其他的线程中，未必能观察到该变量已经被修改。volatile关键字要求被修改之后的变量要求立即更新到主内存，每次使用前从主内存处进行读取。因此volatile可以保证可见性。除了volatile以外，synchronized和final也能实现可见性。synchronized保证unlock之前必须先把变量刷新回主内存。final修饰的字段在构造器中一旦完成初始化，并且构造器没有this逸出，那么其他线程就能看到final字段的值。

**有序性：**java的有序性跟线程相关。如果在线程内部观察，会发现当前线程的一切操作都是有序的。如果在线程的外部来观察的话，会发现线程的所有操作都是无序的。因为JMM的工作内存和主内存之间存在延迟，而且java会对一些指令进行重新排序。volatile和synchronized可以保证程序的有序性，

==重排序过程不会影响到单线程程序的执行，却会影响到多线程并发执行的正确性==

### happens-before原则

由于 **指令重排序** 的存在，两个操作之间有happen-before关系， **并不意味着前一个操作必须要在后一个操作之前执行。 仅仅要求前一个操作的执行结果对于后一个操作是可见的，并且前一个操作 按顺序** 排在第二个操作之前。

单线程happen-before原则：在同一个线程中，书写在前面的操作happen-before后面的操作。

锁的happen-before原则：同一个锁的unlock操作happen-before此锁的lock操作。

volatile的happen-before原则：对一个volatile变量的写操作happen-before对此变量的任意操作(当然也包括写操作了)。

happen-before的传递性原则：如果A操作 happen-before B操作，B操作happen-before C操作，那么A操作happen-before C操作。

线程启动的happen-before原则：同一个线程的start方法happen-before此线程的其它方法。

线程中断的happen-before原则：对线程interrupt方法的调用happen-before被中断线程的检测到中断发送的代码。

线程终结的happen-before原则：线程中的所有操作都happen-before线程的终止检测。

对象创建的happen-before原则：一个对象的初始化完成先于他的finalize方法调用。



## volatile

被volatile修饰的共享变量，就会具有以下两个特性：

1. 保证了不同线程对该变量操作的内存可见性。

   volatile关键字解决可见性问题，通俗来说就是线程A对变量的修改，会直接刷新到主内存，线程B当中，在对变量进行读取的时候，发现变量是volatile关键字修饰的变量，直接放弃从工作内存当中读取，而是从主内存中读取

2. 禁止指令重排序。

   volatile的happen-before原则：对一个volatile变量的写操作happen-before对此变量的任意操作(当然也包括写操作了)。

#### volatile和synchronized的区别

- **volatile关键字**是线程同步的**轻量级实现**，所以**volatile性能肯定比synchronized关键字要好**。但是**volatile关键字只能用于变量而synchronized关键字可以修饰方法以及代码块**。synchronized关键字在JavaSE1.6之后进行了主要包括为了减少获得锁和释放锁带来的性能消耗而引入的偏向锁和轻量级锁以及其它各种优化之后执行效率有了显著提升，**实际开发中使用 synchronized 关键字的场景还是更多一些**。
- **多线程访问volatile关键字不会发生阻塞，而synchronized关键字可能会发生阻塞**
- **volatile关键字能保证数据的可见性，但不能保证数据的原子性。synchronized关键字两者都能保证。**
- **volatile关键字主要用于解决变量在多个线程之间的可见性，而 synchronized关键字解决的是多个线程之间访问资源的同步性。**

## synchronized

### 使用

- **修饰实例方法:** 作用于当前对象实例加锁，进入同步代码前要获得当前对象实例的锁

- **修饰静态方法:** 也就是给当前类加锁，会作用于类的所有对象实例，因为静态成员不属于任何一个实例对象，是类成员（ static 表明这是该类的一个静态资源，不管new了多少个对象，只有一份）。所以如果一个线程 A 调用一个实例对象的非静态 synchronized 方法，而线程 B 需要调用这个实例对象所属类的静态 synchronized 方法，是允许的，不会发生互斥现象，**因为访问静态 synchronized 方法占用的锁是当前类的锁，而访问非静态 synchronized 方法占用的锁是当前实例对象锁**。

- **修饰代码块:** 指定加锁对象，对给定对象加锁，进入同步代码库前要获得给定对象的锁。

  **总结：** synchronized 关键字加到 static 静态方法和 synchronized(class)代码块上都是是给 Class 类上锁。synchronized 关键字加到实例方法上是给对象实例上锁。尽量不要使用 synchronized(String a) 因为JVM中，字符串常量池具有缓存功能！

#### 线程安全的单例模式（双重检查锁方式）

```java
public class Singleton {

    private volatile static Singleton uniqueInstance;

    private Singleton() {
    }

    public static Singleton getUniqueInstance() {
       //先判断对象是否已经实例过，没有实例化过才进入加锁代码
        if (uniqueInstance == null) {
            //类对象加锁
            synchronized (Singleton.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new Singleton();
                }
            }
        }
        return uniqueInstance;
    }
}

```

另外，需要注意 uniqueInstance 采用 volatile 关键字修饰也是很有必要。

uniqueInstance 采用 volatile 关键字修饰也是很有必要的， uniqueInstance = new Singleton(); 这段代码其实是分为三步执行：

1. 为 uniqueInstance 分配内存空间
2. 初始化 uniqueInstance
3. 将 uniqueInstance 指向分配的内存地址

但是由于 JVM 具有指令重排的特性，执行顺序有可能变成 1->3->2。指令重排在单线程环境下不会出现问题，但是在多线程环境下会导致一个线程获得还没有初始化的实例。例如，线程 T1 执行了 1 和 3，此时 T2 调用 getUniqueInstance() 后发现 uniqueInstance 不为空，因此返回 uniqueInstance，但此时 uniqueInstance 还未被初始化。

使用 volatile 可以禁止 JVM 的指令重排，保证在多线程环境下也能正常运行。

### synchronized 关键字的底层原理

**synchronized 关键字底层原理属于 JVM 层面。**

**① synchronized 同步语句块的情况**

```java
public class SynchronizedDemo {
    public void method() {
        synchronized (this) {
            System.out.println("synchronized 代码块");
        }
    }
}
Copy to clipboardErrorCopied
```

通过 JDK 自带的 javap 命令查看 SynchronizedDemo 类的相关字节码信息：首先切换到类的对应目录执行 `javac SynchronizedDemo.java` 命令生成编译后的 .class 文件，然后执行`javap -c -s -v -l SynchronizedDemo.class`。

![synchronized关键字原理](https://my-blog-to-use.oss-cn-beijing.aliyuncs.com/2019-6/synchronized%E5%85%B3%E9%94%AE%E5%AD%97%E5%8E%9F%E7%90%86.png)

从上面我们可以看出：

**synchronized 同步语句块的实现使用的是 monitorenter 和 monitorexit 指令，其中 monitorenter 指令指向同步代码块的开始位置，monitorexit 指令则指明同步代码块的结束位置。** 当执行 monitorenter 指令时，线程试图获取锁也就是获取 monitor(monitor对象存在于每个Java对象的对象头中，synchronized 锁便是通过这种方式获取锁的，也是为什么Java中任意对象可以作为锁的原因) 的持有权。当计数器为0则可以成功获取，获取后将锁计数器设为1也就是加1。相应的在执行 monitorexit 指令后，将锁计数器设为0，表明锁被释放。如果获取对象锁失败，那当前线程就要阻塞等待，直到锁被另外一个线程释放为止。

**② synchronized 修饰方法的的情况**

```java
public class SynchronizedDemo2 {
    public synchronized void method() {
        System.out.println("synchronized 方法");
    }
}
Copy to clipboardErrorCopied
```

![synchronized关键字原理](https://my-blog-to-use.oss-cn-beijing.aliyuncs.com/2019-6/synchronized%E5%85%B3%E9%94%AE%E5%AD%97%E5%8E%9F%E7%90%862.png)

synchronized 修饰的方法并没有 monitorenter 指令和 monitorexit 指令，取得代之的确实是 ACC_SYNCHRONIZED 标识，该标识指明了该方法是一个同步方法，JVM 通过该 ACC_SYNCHRONIZED 访问标志来辨别一个方法是否声明为同步方法，从而执行相应的同步调用。

## ThreadLocal

ThreadLocal主要用来存储当前线程上下文的变量信息，它可以保障存储进去的数据，只能被当前线程读取到，并且线程之间不会相互影响。ThreadLocal为变量在每个线程中都创建了一个副本，那么每个线程可以访问自己内部的副本变量。 

​	ThreadLocal有哪些典型的应用场景： 

​	1.数据库事务。通过AOP的方式，对执行数据库事务的函数进行拦截。函数开始前，获取connection开启事务并存储在ThreadLocal中，任何用到connection的地方，从ThreadLocal中获取，函数执行完毕后，提交事务释放connection。 

​	2.web项目中，用户的登录信息通常保存在session中。做一个拦截器，把用户信息放在ThreadLocal中，在任何用到用户信息的时候，只需要从TreadLocal中读取就可以了。

### 原理	

每个线程内部都有一个名为threadLocals 的成员变量，该变量类型为ThreadLocalMap （定制化的HashMap），具有get（），set（）方法，key为ThreadLocal 对象的this引用，value为set中设置的值。虽然threadLocals 并非八大基本类型，但仍存放在线程中（栈），而非ThreadLocal 对象中（堆）。set中设置的值是放在了当前线程的 threadLocals 中，并不是存在 `ThreadLocal` 上，`ThreadLocal` 可以理解为只是`ThreadLocalMap`的封装，传递了变量值。

### ThreadLocal 内存泄露问题

`ThreadLocalMap` 中使用的 key 为 `ThreadLocal` 的弱引用,而 value 是强引用。所以，如果 `ThreadLocal` 没有被外部强引用的情况下，在垃圾回收的时候，key 会被清理掉，而 value 不会被清理掉。这样一来，`ThreadLocalMap` 中就会出现key为null的Entry。假如我们不做任何措施的话，value 永远无法被GC 回收，这个时候就可能会产生内存泄露。ThreadLocalMap实现中已经考虑了这种情况，在调用 `set()`、`get()`、`remove()` 方法的时候，会清理掉 key 为 null 的记录。==使用完 `ThreadLocal`方法后 最好手动调用`remove()`方法==

## Atomic 与 CAS

### CAS原理

### Atomic 原子类

## 锁与同步器

### AQS原理

AQS全称为AbstractQueuedSynchronizer,即抽象同步队列。

AQS是实现同步器的基础组件，并发包中各种锁也是由AQS所实现

### ReentrantLock和synchronized的区别

**① 两者都是可重入锁**

两者都是可重入锁。“可重入锁”概念是：自己可以再次获取自己的内部锁。比如一个线程获得了某个对象的锁，此时这个对象锁还没有释放，当其再次想要获取这个对象的锁的时候还是可以获取的，如果不可锁重入的话，就会造成死锁。同一个线程每次获取锁，锁的计数器都自增1，所以要等到锁的计数器下降为0时才能释放锁。

**② synchronized 依赖于 JVM 而 ReentrantLock 依赖于 API**

synchronized 是依赖于 JVM 实现的，前面我们也讲到了 虚拟机团队在 JDK1.6 为 synchronized 关键字进行了很多优化，但是这些优化都是在虚拟机层面实现的，并没有直接暴露给我们。ReentrantLock 是 JDK 层面实现的（也就是 API 层面，需要 lock() 和 unlock() 方法配合 try/finally 语句块来完成），所以我们可以通过查看它的源代码，来看它是如何实现的。

**③ ReentrantLock 比 synchronized 增加了一些高级功能**

相比synchronized，ReentrantLock增加了一些高级功能。主要来说主要有三点：**①等待可中断；②可实现公平锁；③可实现选择性通知（锁可以绑定多个条件）**

- **ReentrantLock提供了一种能够中断等待锁的线程的机制**，通过lock.lockInterruptibly()来实现这个机制。也就是说正在等待的线程可以选择放弃等待，改为处理其他事情。
- **ReentrantLock可以指定是公平锁还是非公平锁。而synchronized只能是非公平锁。所谓的公平锁就是先等待的线程先获得锁。** ReentrantLock默认情况是非公平的，可以通过 ReentrantLock类的`ReentrantLock(boolean fair)`构造方法来制定是否是公平的。
- synchronized关键字与wait()和notify()/notifyAll()方法相结合可以实现等待/通知机制，ReentrantLock类当然也可以实现，但是需要借助于Condition接口与newCondition() 方法。Condition是JDK1.5之后才有的，它具有很好的灵活性，比如可以实现多路通知功能也就是在一个Lock对象中可以创建多个Condition实例（即对象监视器），**线程对象可以注册在指定的Condition中，从而可以有选择性的进行线程通知，在调度线程上更加灵活。 在使用notify()/notifyAll()方法进行通知时，被通知的线程是由 JVM 选择的，用ReentrantLock类结合Condition实例可以实现“选择性通知”** ，这个功能非常重要，而且是Condition接口默认提供的。而synchronized关键字就相当于整个Lock对象中只有一个Condition实例，所有的线程都注册在它一个身上。如果执行notifyAll()方法的话就会通知所有处于等待状态的线程这样会造成很大的效率问题，而Condition实例的signalAll()方法 只会唤醒注册在该Condition实例中的所有等待线程。

### ReentrantReadWriteLock 读写锁

读写锁在ReentrantLock上进行了拓展使得该锁更适合读操作远远大于写操作对场景。

AQS中维护了一个state状态，其高16位表示读状态，低16位表示写状态。

#### 读写锁的实现

## 线程池

#### 几种常见的线程池及使用场景

1、newSingleThreadExecutor
创建一个单线程化的线程池，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行。

2、newFixedThreadPool
创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待。

3、newCachedThreadPool
创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。

4、newScheduledThreadPool
创建一个定长线程池，支持定时及周期性任务执行。

**可能产生的问题：**

1）newFixedThreadPool和newSingleThreadExecutor:
  主要问题是堆积的请求处理队列可能会耗费非常大的内存，甚至OOM。
2）newCachedThreadPool和newScheduledThreadPool:
  主要问题是线程数最大数是Integer.MAX_VALUE，可能会创建数量非常多的线程，甚至OOM。

#### 线程池的常见参数

