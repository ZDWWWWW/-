## Redis入门

### Windows安装

1、下载安装包 https://github.com/MSOpenTech/redis/releases

![image-20200405175553863](Redis.assets\image-20200405175553863.png)

2、解鸭

![image-20200405175803706](Redis.assets\image-20200405175803706.png)

3、运行服务端redis-server.exe

![image-20200405175912388](Redis.assets\image-20200405175912388.png)

4、运行客户端redis-cli.exe

![image-20200405180013998](Redis.assets\image-20200405180013998.png)

### Linux下安装

1、下载安装包 redis-5.0.8.tar.gz  https://redis.io/

2、解压安装包 与/opt目录下

![image-20200404094420931](Redis.assets\image-20200404094420931.png)

3、基本的环境安装

```bash
yum install gcc-c++
```

4、进入redis-5.0.8  make一下

```bash
make
```

5、redis默认安装路径  /usr/local/bin

![image-20200404095220572](C:\Users\22815\Desktop\notes\Redis.assets\image-20200404095220572.png)

6、将原生的配置文件复制到安装目录下的gconfig(自己命名自己创建)，以后都用安装目录下的

​	原生的配置文件位置 /opt/redis-5.0.8/redis.conf

![image-20200404095655141](C:\Users\22815\Desktop\notes\Redis.assets\image-20200404095655141.png)

7、redis默认不是后台启动，需要修改配置文件  vim redis.conf

![image-20200404100144180](C:\Users\22815\Desktop\notes\Redis.assets\image-20200404100144180.png)

8、启动redis

/usr/local/bin 目录下

```bash
#启动
redis-server gconfig/redis.conf 
#使用redis客户端连接制定端口 注意打开安全组 下次使用直接输这个就行
redis-cli -p 6379
#输入ping回个pong表示连接成功
```

![image-20200404100816621](C:\Users\22815\Desktop\notes\Redis.assets\image-20200404100816621.png)

9、查看redis进程开启

![image-20200404102034375](C:\Users\22815\Desktop\notes\Redis.assets\image-20200404102034375.png)

10、关闭redis服务 

shutdown+exit

![image-20200404102326196](C:\Users\22815\Desktop\notes\Redis.assets\image-20200404102326196.png)



### 测试性能

**redis-benchmake**是官方自带的压力测试工具

| 序号 | 选项      | 描述                                       | 默认值    |
| :--- | :-------- | :----------------------------------------- | :-------- |
| 1    | **-h**    | 指定服务器主机名                           | 127.0.0.1 |
| 2    | **-p**    | 指定服务器端口                             | 6379      |
| 3    | **-s**    | 指定服务器 socket                          |           |
| 4    | **-c**    | 指定并发连接数                             | 50        |
| 5    | **-n**    | 指定请求数                                 | 10000     |
| 6    | **-d**    | 以字节的形式指定 SET/GET 值的数据大小      | 2         |
| 7    | **-k**    | 1=keep alive 0=reconnect                   | 1         |
| 8    | **-r**    | SET/GET/INCR 使用随机 key, SADD 使用随机值 |           |
| 9    | **-P**    | 通过管道传输 <numreq> 请求                 | 1         |
| 10   | **-q**    | 强制退出 redis。仅显示 query/sec 值        |           |
| 11   | **--csv** | 以 CSV 格式输出                            |           |
| 12   | **-l**    | 生成循环，永久执行测试                     |           |
| 13   | **-t**    | 仅运行以逗号分隔的测试命令列表。           |           |
| 14   | **-I**    | Idle 模式。仅打开 N 个 idle 连接并等待。   |           |

```bash
#测试： 100个并发连接 100000请求
redis-benchmark -h localhost -p 6379 -c 100 -n 100000
```

![image-20200404103819202](C:\Users\22815\Desktop\notes\Redis.assets\image-20200404103819202.png)

怎样分析数据呢？

![image-20200404104334607](C:\Users\22815\Desktop\notes\Redis.assets\image-20200404104334607.png)

### 基础知识

redis有16个数据库![image-20200404105202890](C:\Users\22815\Desktop\notes\Redis.assets\image-20200404105202890.png)

默认使用第0个

**可以使用select进行数据库切换**

```bash
127.0.0.1:6379> select 3
OK
127.0.0.1:6379[3]> dbsize
(integer) 0
127.0.0.1:6379[3]> set name dahuanggou
OK
127.0.0.1:6379[3]> dbsize
(integer) 1
127.0.0.1:6379[3]> get name
"dahuanggou"

```

**查看数据库所有的key**

```bash
127.0.0.1:6379[3]> keys *
1) "name"

```

**清除当前数据库**

```bash
127.0.0.1:6379[3]> flushdb
OK
127.0.0.1:6379[3]> keys *
(empty list or set)

```

**清除所有数据库**

```bash
127.0.0.1:6379[3]> flushall
OK
127.0.0.1:6379[3]> keys *
(empty list or set)

```

**Redis 是单线程的！**

Redis是基于内存操作，CPU不是Redis性能瓶颈，Redis的瓶颈是机器的内存和网络带宽，既然可以使用单线程就使用单线程了。

> Redis为什么单线程还那么快？

误区1：高性能的服务器一定是多线程的？

误区2：多线程一定比单线程效率高？

核心：多线程会产生CPU上下文切换，非常耗时。redis将所有数据放到内存中，没有上下文切换，多次读写都是在一个CPU上，这样采用单线程就是速度最快的！

## 五大数据类型

**Redis-Key**

```bash
# keys *
# set
# get
127.0.0.1:6379> keys * #查看所有的key
(empty list or set)
127.0.0.1:6379> set name huanggou #set key
OK
127.0.0.1:6379> exists name #判断当前的key是否存在
(integer) 1
127.0.0.1:6379> move name 1 #移除当前的key
(integer) 1
127.0.0.1:6379> set name qinjiang
OK
127.0.0.1:6379> keys *
1) "name"
127.0.0.1:6379> get name  #得到key对应的value
"qinjiang"
127.0.0.1:6379> expire name 10 #设置key的过期时间
(integer) 1
127.0.0.1:6379> ttl name #查看当前key的过期时间 
(integer) 3
127.0.0.1:6379> type name #判断当前key的类型
none

```

### String

```bash
########################################################3###
#追加字符串
#获取字符串长度 
127.0.0.1:6379> set key1 v1  #设置值
OK
127.0.0.1:6379> append key1 "hello" #追加字符串，若当前key不存在，则相当于 set key
(integer) 7
127.0.0.1:6379> get key1 #获取值
"v1hello"
127.0.0.1:6379> strlen key1 #获取字符串长度
(integer) 7
#############################################################
#值的增减
127.0.0.1:6379> set views 0 #初始浏览量为0
OK
127.0.0.1:6379> incr views #自增1
(integer) 1
127.0.0.1:6379> incr views
(integer) 2
127.0.0.1:6379> get views
"2"
127.0.0.1:6379> decr views #自减1
(integer) 1
127.0.0.1:6379> decr views
(integer) 0
127.0.0.1:6379> get views
"0"
127.0.0.1:6379> incrby views 10 #指定步长增加
(integer) 10
127.0.0.1:6379> decrby views 5  #指定步长减少
(integer) 5
########################################################
#得到指定范围字符串
#获取全部字符串
127.0.0.1:6379> set key1 "hello,huanggou"
OK
127.0.0.1:6379> getrange key1 0 3 #得到指定范围字符串 [0,3]
"hell"
127.0.0.1:6379> getrange key1 0 -1 #获取全部字符串 = get
"hello,huanggou"
#######################################################3
#替换字符串 
127.0.0.1:6379> set key2 abcdefg
OK
127.0.0.1:6379> setrange key2 1 xx #指定位置替换
(integer) 7
127.0.0.1:6379> get key2
"axxdefg"
######################################################
# 设置过期时间          setex（set with expire）
# 不存在再为key设置值    setnx（set if not exist）
127.0.0.1:6379> setex key3 30 "hello" #set key3 30秒后过期
OK
127.0.0.1:6379> setnx mykey "redis"
(integer) 1
127.0.0.1:6379> setnx mykey "dog" # mykey已存在，set失败
(integer) 0
127.0.0.1:6379> get mykey
"redis"
####################################################33
#批量操作
127.0.0.1:6379> mset k1 v1 k2 v2 k3 v3 #批量设置多个值
OK
127.0.0.1:6379> keys *
1) "k3"
2) "k2"
3) "k1"
127.0.0.1:6379> mget k1 k2 k3 #批量获取多个值
1) "v1"
2) "v2"
3) "v3"
127.0.0.1:6379> msetnx k1 v1 k4 v4 #msetnx 是原子性操作 set k1失败了整段垮掉 
(integer) 0
127.0.0.1:6379> get k4
(nil)
##########################################################
#对象
set user:1{name:zhangsan,age:3} #设置一个user:1对象 值为json字符来保存一个对象
#这里的key是一个巧妙的设计：user：{id}:{filed}
127.0.0.1:6379> mset user:1:name zhangsan user:1:age 2
OK
127.0.0.1:6379> mget user:1:name  user:1:age 
1) "zhangsan"
2) "2"
######################################################33###
#组合技：先获取再设置 
127.0.0.1:6379> getset db redis #若不存在值，返回nil
(nil)
127.0.0.1:6379> get db 
"redis"
127.0.0.1:6379> getset db MongoDB#若存在值，获取原来的值，并设置新的值
"redis"
127.0.0.1:6379> get db 
"MongoDB"

```

String类型的使用场景：value除了是字符还可以是数字

计数器

粉丝数

对象缓存存储



### List

可以把List作为蘸、队列   大部分命令以L开头

**LPUSH RPOP 左进右出 →队列**

**LPUSH LPOP 左进左出 →栈**

![image-20200404192300611](Redis.assets\image-20200404192300611.png)

```bash
###########################################################
#PUSH
#获取列表中全部元素
#获取列表指定范围元素[0,1]
127.0.0.1:6379> lpush list one #将一个值或多个值放到列表头部（左）
(integer) 1
127.0.0.1:6379> lpush list two
(integer) 2
127.0.0.1:6379> lpush list shree
(integer) 3
127.0.0.1:6379> lrange list 0 -1 #获取列表中全部元素
1) "shree"
2) "two"
3) "one"
127.0.0.1:6379> lrange list 0 1 #获取列表指定范围元素[0,1]
1) "shree"
2) "two"
127.0.0.1:6379> rpush list right #将一个值或多个值放到列表尾部（右）
(integer) 4
127.0.0.1:6379> lrange list 0 -1
1) "shree"
2) "two"
3) "one"
4) "right"
#############################################################
#POP
127.0.0.1:6379> lpop list #移除列表头部元素（左）
"shree"
127.0.0.1:6379> rpop list #移除列表尾部元素（右）
"right"
#############################################################3
#通过下标获取值
127.0.0.1:6379> lindex list 0 #通过下标获取值
"two"
###############################################################
#返回列表长度
127.0.0.1:6379> lpush list one
(integer) 1
127.0.0.1:6379> lpush list two
(integer) 2
127.0.0.1:6379> lpush list three
(integer) 3
127.0.0.1:6379> llen list #返回列表长度
(integer) 3
##############################################################
#移除指定的值
127.0.0.1:6379> lpush list three
(integer) 4
127.0.0.1:6379> lrange list 0 -1
1) "three"
2) "three"
3) "two"
4) "one"
127.0.0.1:6379> lrem list 1 one #移除 list 中 1个 one
(integer) 1
127.0.0.1:6379> lrange list 0 -1
1) "three"
2) "three"
3) "two"
127.0.0.1:6379> lrem list 2 three #移除 list 中 2个 three
(integer) 2
127.0.0.1:6379> lrange list 0 -1
1) "two"
127.0.0.1:6379> 
###############################################################
#截取元素，截取后改变list的元素
127.0.0.1:6379> rpush mylist "hello"
(integer) 1
127.0.0.1:6379> rpush mylist "hello1"
(integer) 2
127.0.0.1:6379> rpush mylist "hello2"
(integer) 3
127.0.0.1:6379> rpush mylist "hello3"
(integer) 4
127.0.0.1:6379> 
127.0.0.1:6379> ltrim mylist 1 2 #通过下标截取指定长度，和lrange不同的是，截取后改变list的元素
OK
127.0.0.1:6379> lrange mylist 0 -1
1) "hello1"
2) "hello2"
#################################################################
#组合命令
127.0.0.1:6379> rpush mylist "hello"
(integer) 1
127.0.0.1:6379> rpush mylist "hello1"
(integer) 2
127.0.0.1:6379> rpush mylist "hello2"
(integer) 3
127.0.0.1:6379> rpoplpush mylist myotherlist #移除列表元素并将其移动到新的列表中
"hello2"
127.0.0.1:6379> lrange mylist 0 -1
1) "hello"
2) "hello1"
127.0.0.1:6379> lrange myotherlist 0 -1
1) "hello2"
###############################################################
#更新当前下标的值
127.0.0.1:6379> lrange mylist 0 -1
1) "hello"
2) "hello1"
127.0.0.1:6379> lset mylist 0 WWWW  #更新当前下标的值
OK
127.0.0.1:6379> lrange mylist 0 -1
1) "WWWW"
2) "hello1"
#####################################################################3
# 插入元素          linsert 表名 before/after 旧元素 新元素 
127.0.0.1:6379> rpush mylist hello
(integer) 1
127.0.0.1:6379> rpush mylist world
(integer) 2
127.0.0.1:6379> linsert mylist before world other #插入“world”前面
(integer) 3
127.0.0.1:6379> lrange mylist 0 -1
1) "hello"
2) "other"
3) "world"
127.0.0.1:6379> linsert mylist after world new #插入“world”后面
(integer) 4
127.0.0.1:6379> lrange mylist 0 -1
1) "hello"
2) "other"
3) "world"
4) "new"

```

### Set

Set中的值不能重复  命令以s开头

```bash
###############################################################
#集合中添加元素
#查看指定set的所有值
#判断一个值是否在set集合中
#获取set中元素个数
#移除指定元素
#随机返回set中指定个数的元素
127.0.0.1:6379> sadd myset "hello"  #集合中添加元素
(integer) 1
127.0.0.1:6379> sadd myset "kuangshen"
(integer) 1
127.0.0.1:6379> sadd myset "lovekuangshen"
(integer) 1
127.0.0.1:6379> smembers myset  #查看指定set的所有值
1) "hello"
2) "lovekuangshen"
3) "kuangshen"
127.0.0.1:6379> sismember myset hello #判断一个值是否在set集合中
(integer) 1
127.0.0.1:6379> sismember myset world
(integer) 0
127.0.0.1:6379> scard myset #获取set中元素个数
(integer) 3
127.0.0.1:6379> srem myset hello #移除指定元素
(integer) 1
127.0.0.1:6379> srandmember myset 1 #随机返回set中指定个数的元素
"lovekuangshen"
######################################################################
#随机移除一个元素
#将指定的值，移动到另一个set
127.0.0.1:6379> smembers myset
1) "lovekuangshen"
2) "lovekuangshen2"
3) "kuangshen"
127.0.0.1:6379> spop myset #随机移除一个元素
"lovekuangshen"
127.0.0.1:6379> smembers myset
1) "lovekuangshen2"
2) "kuangshen"
127.0.0.1:6379> smove myset myset2 kuangshen #将指定的值，移动到另一个set
(integer) 1
127.0.0.1:6379> smembers myset
1) "lovekuangshen2"
127.0.0.1:6379> smembers myset2
1) "kuangshen"
#####################################################################3
# 交集（共同关注）  并集
127.0.0.1:6379> sadd key1 a
(integer) 1
127.0.0.1:6379> sadd key1 b
(integer) 1
127.0.0.1:6379> sadd key1 c
(integer) 1
127.0.0.1:6379> sadd key2 c
(integer) 1
127.0.0.1:6379> sadd key2 d
(integer) 1
127.0.0.1:6379> sadd key2 e
(integer) 1
127.0.0.1:6379> sdiff key1 key2 #key1中和key2不同的元素
1) "b"
2) "a"
127.0.0.1:6379> sinter key1 key2 #交集
1) "c"
127.0.0.1:6379> sunion key1 key2 #并集
1) "b"
2) "c"
3) "a"
4) "e"
5) "d"

```

### Hash

本质和String类型没太大区别，本质还是key-value

hash更适合对象的存储，string更适合字符串的存储

命令以h开头

```bash
#####################################################################3
#set一个的key-value
#get一个key对应的value
#删除指定的key，对应的value也没有了
127.0.0.1:6379> hset myhash field1 kuangshen #set一个的key-value
(integer) 1
127.0.0.1:6379> hget myhash field1 #get key对应的value
"kuangshen"
127.0.0.1:6379> hmset myhash field1 hello field2 world #set多个的key-value
OK
127.0.0.1:6379> hmget myhash field1  field2 #get 多个key对应的value
1) "hello"
2) "world"
127.0.0.1:6379> hgetall myhash #获取全部的key-value
1) "field1"
2) "hello"
3) "field2"
4) "world"
127.0.0.1:6379> hdel myhash field1 #删除指定的key，对应的value也没有了
(integer) 1
127.0.0.1:6379> hgetall myhash
1) "field2"
2) "world"
#####################################################################3
#获取hash表 key-value 数量
#判断hash中key是否存在
#只获取hash中的key
#只获取hash中的value
127.0.0.1:6379> hmset myhash field1 hello field2 world
OK
127.0.0.1:6379> hlen myhash #获取hash表 key-value 数量
(integer) 2
127.0.0.1:6379> hexists myhash field1 #判断hash中key是否存在
(integer) 1
127.0.0.1:6379> hexists myhash field3
(integer) 0
127.0.0.1:6379> hkeys myhash #只获取hash中的key
1) "field2"
2) "field1"
127.0.0.1:6379> hvals myhash #只获取hash中的value
1) "world"
2) "hello"
#####################################################################3
#指定增量 
#不存在再为key设置值   hsetnx（set if not exist）
127.0.0.1:6379> hset myhash field 5
(integer) 1
127.0.0.1:6379> hincrby myhash field 10 #指定增量 同incrby
(integer) 15
127.0.0.1:6379> hsetnx myhash field4 hello #同setnx
(integer) 1
127.0.0.1:6379> hsetnx myhash field4 worlf
(integer) 0



```

### Zset

在Set基础上，增加了一个字段 score → 有序集合

命令以z开头

```bash
#####################################################################3
#添加值
127.0.0.1:6379> zadd myset 1 one #添加一个值
(integer) 1
127.0.0.1:6379> zadd myset 2 two 3 three #添加多个值
(integer) 1
127.0.0.1:6379> zrange myset 0 -1
1) "one"
2) "two"
3) "three"
#####################################################################3
#排序
127.0.0.1:6379> zadd salary 2500 xiaohong
(integer) 1
127.0.0.1:6379> zadd salary 5000 zhangsan
(integer) 1
127.0.0.1:6379> zadd salary 500 kuangshen
(integer) 1
127.0.0.1:6379> zrangebyscore salary -inf +inf #score从小到大排序 范围为[-00，+00]
1) "kuangshen"
2) "xiaohong"
3) "zhangsan"
127.0.0.1:6379> zrangebyscore salary -inf +inf withscores #score从小到大排序 且给出score
1) "kuangshen"
2) "500"
3) "xiaohong"
4) "2500"
5) "zhangsan"
6) "5000"
127.0.0.1:6379> zrangebyscore salary -inf 2500 withscores # socre 范围为[-00,2500]
1) "kuangshen"
2) "500"
3) "xiaohong"
4) "2500"
#####################################################################3
#移除指定元素
#获取元素个数
127.0.0.1:6379> zrange salary 0 -1 
1) "kuangshen"
2) "xiaohong"
3) "zhangsan"
127.0.0.1:6379> zrem salary xiaohong #移除指定元素
(integer) 1
127.0.0.1:6379> zrange salary 0 -1
1) "kuangshen"
2) "zhangsan"
127.0.0.1:6379> zcard salary #获取元素个数
(integer) 2
#####################################################################3
#获取指定score区间成员数量
127.0.0.1:6379> zadd myset 1 hello
(integer) 1
127.0.0.1:6379> zadd myset 2 world 3 kuangshen
(integer) 2
127.0.0.1:6379> zcount myset 1 3 #获取指定score区间成员数量
(integer) 3
127.0.0.1:6379> zcount myset 1 2
(integer) 2




```

## 三种特殊数据类型

### Geospatial 地理位置

![image-20200405102423505](C:\Users\22815\Desktop\notes\Redis.assets\image-20200405102423505.png)

只有6个命令

> geoadd 将指定的地理空间位置（纬度、经度、名称）添加到指定的key中

```bash
#geoadd添加城市地理位置
#规则：两级无法添加，一般通过java导入数据
#参数 key value（维度 精度 名称）
#有效的经度从-180度到180度。
#有效的纬度从-85.05112878度到85.05112878度。
#当坐标位置超出上述指定范围时，该命令将会返回一个错误。
127.0.0.1:6379> geoadd china:city 116.40 39.90 beijing
(integer) 1
127.0.0.1:6379> geoadd china:city 121.47 31.23 shanghai
(integer) 1
127.0.0.1:6379> geoadd china:city 160.50 29.53 chongqing
(integer) 1
127.0.0.1:6379> geoadd china:city 114.05 22.52 shenzhen 120.16 30.24 hangzhou
(integer) 2
127.0.0.1:6379> geoadd china:city 108.96 34.26 xian
(integer) 1

```

> geopos 从key里返回所有给定位置元素的位置（经度和纬度）

```bash
127.0.0.1:6379> geopos china:city beijing
1) 1) "116.39999896287918091"
   2) "39.90000009167092543"
127.0.0.1:6379> geopos china:city beijing chongqing
1) 1) "116.39999896287918091"
   2) "39.90000009167092543"
2) 1) "160.49999982118606567"
   2) "29.52999957900659211"

```

> geodist 返回两个给定位置之间的距离

```bash
#m 表示单位为米。
#km 表示单位为千米。
#mi 表示单位为英里。
#ft 表示单位为英尺。
127.0.0.1:6379> geodist china:city beijing shanghai
"1067378.7564"
127.0.0.1:6379> geodist china:city beijing shanghai km
"1067.3788"

```

> georadius 以给定的经纬度为中心， 找出某一半径内的元素

```bash
127.0.0.1:6379> georadius china:city 110 30 1000 km #以100 30 经纬度为中心 寻找方圆1000KM内城市
1) "xian"
2) "shenzhen"
3) "hangzhou"
127.0.0.1:6379> georadius china:city 110 30 500 km
1) "xian"
127.0.0.1:6379> georadius china:city 110 30 500 km withdist #显示距离
1) 1) "xian"
   2) "483.8340"
127.0.0.1:6379> georadius china:city 110 30 500 km withcoord #显示经纬度
1) 1) "xian"
   2) 1) "108.96000176668167114"
      2) "34.25999964418929977"
127.0.0.1:6379> georadius china:city 110 30 1000 km withcoord withdist count 1 #筛选出指定数量的结果
1) 1) "xian"
   2) "483.8340"
   3) 1) "108.96000176668167114"
      2) "34.25999964418929977"

```

> georadiusbymember  找出位于指定范围内的元素，中心点是由给定的位置元素决定

```bash
127.0.0.1:6379> georadiusbymember china:city beijing 1000 km
1) "beijing"
2) "xian"
127.0.0.1:6379> georadiusbymember china:city shanghai 400 km
1) "hangzhou"
2) "shanghai"

```

> geohash 返回一个或多个位置元素的 Geohash 表示

```bash
#将二维的经纬度 转变为一维的字符串，两个字符串越接近 则距离越近
127.0.0.1:6379> geohash china:city beijing hangzhou
1) "wx4fbxxfke0"
2) "wtmkn31bfb0"

```

> GEO底层通过Zset实现，通过Zset命令可以操作geo

```bash
127.0.0.1:6379> zrange china:city 0 -1 #查看全部元素
1) "xian"
2) "shenzhen"
3) "hangzhou"
4) "shanghai"
5) "beijing"
6) "chongqing"
127.0.0.1:6379> zrem china:city beijing #移除指定元素
(integer) 1
127.0.0.1:6379> zrange china:city 0 -1
1) "xian"
2) "shenzhen"
3) "hangzhou"
4) "shanghai"
5) "chongqing"

```

### Hyperloglog 基数统计

Redis 在 2.8.9 版本添加了 HyperLogLog 结构。

Redis HyperLogLog 是用来做基数统计的算法，HyperLogLog 的优点是，在输入元素的数量或者体积非常非常大时，计算基数所需的空间总是固定 的、并且是很小的。

在 Redis 里面，每个 HyperLogLog 键只需要花费 12 KB 内存，就可以计算接近 2^64 个不同元素的基 数。这和计算基数时，元素越多耗费内存就越多的集合形成鲜明对比。

但是，因为 HyperLogLog 只会根据输入元素来计算基数，而不会储存输入元素本身，所以 HyperLogLog 不能像集合那样，返回输入的各个元素。

> 什么是基数? 

比如数据集 {1, 3, 5, 7, 5, 7, 8}， 那么这个数据集的基数集为 {1, 3, 5 ,7, 8}, 基数(不重复元素)为5。 基数估计就是在误差可接受的范围内，快速计算基数。

```bash
127.0.0.1:6379> pfadd mykey a b c d e f g h i j #创建
(integer) 1
127.0.0.1:6379> pfcount mykey #统计基数数量
(integer) 10
127.0.0.1:6379> pfadd mykey2 i j z x c v b n m
(integer) 1
127.0.0.1:6379> pfcount mykey2
(integer) 9
127.0.0.1:6379> pfmerge mykey3 mykey mykey2 #合并
OK
127.0.0.1:6379> pfcount mykey3
(integer) 15

```

**如果允许容错，使用Hyperloglog类型**

**不允许容错，使用Set等数据类型**



### Bitmap 位图

操作二进制位来记录，value只有0和1两种状态的“数据结构”

```bash
#记录周一到周日的打卡
#周一： 1  周二： 1 周三： 0 周四： 0 ······
127.0.0.1:6379> setbit sign 0 1
(integer) 0
127.0.0.1:6379> setbit sign 1 1
(integer) 0
127.0.0.1:6379> setbit sign 2 0
(integer) 0
127.0.0.1:6379> setbit sign 3 0
(integer) 0
127.0.0.1:6379> setbit sign 4 1
(integer) 0
127.0.0.1:6379> setbit sign 5 1
(integer) 0
127.0.0.1:6379> setbit sign 6 0
(integer) 0
#查看某天是否有打卡
127.0.0.1:6379> getbit sign 3
(integer) 0
127.0.0.1:6379> getbit sign 5
(integer) 1
#统计打卡的天数
127.0.0.1:6379> bitcount sign
(integer) 4

```

## 事务

==Redis单条命令保证原子性，但是事务不保证原子性==

==Redis事务没有隔离级别的概念==

所有的命令在事务中，并没有直接被执行，只有发起执行命令的时候才会执行。

> 正常执行事务

```bash
#开启事务
127.0.0.1:6379> multi
OK
#命令入队
127.0.0.1:6379> set k1 v1
QUEUED
127.0.0.1:6379> set k2 v2
QUEUED
127.0.0.1:6379> get k2
QUEUED
127.0.0.1:6379> set k3 v3
QUEUED
#执行事务
127.0.0.1:6379> exec
1) OK
2) OK
3) "v2"
4) OK

```

> 放弃事务

```bash
127.0.0.1:6379> multi
OK
127.0.0.1:6379> set k1 v1
QUEUED
127.0.0.1:6379> set k2 v2
QUEUED 
127.0.0.1:6379> set k4 v4
QUEUED
127.0.0.1:6379> discard
OK
127.0.0.1:6379> get k4
(nil)

```

> 编译型异常（命令有问题），事务中所有命令都不执行

```bash
127.0.0.1:6379> multi
OK
127.0.0.1:6379> set k1 v1
QUEUED
127.0.0.1:6379> fee3dcfrwt k2 v2  #错误的命令
(error) ERR unknown command `fee3dcfrwt`, with args beginning with: `k2`, `v2`, 
127.0.0.1:6379> set k3 v3
QUEUED
127.0.0.1:6379> exec #执行事务报错
(error) EXECABORT Transaction discarded because of previous errors.
127.0.0.1:6379> get v3#所有的命令都不会被执行
(nil)

```

> 运行时异常（1/0=?）,执行事务的时候，发生错误的命令抛出异常，其他命令可以执行。

```bash
127.0.0.1:6379> multi
OK
127.0.0.1:6379> set k1 v1
QUEUED
127.0.0.1:6379> incr k1 #执行失败
QUEUED
127.0.0.1:6379> set k2 v2
QUEUED
127.0.0.1:6379> get k2
QUEUED
127.0.0.1:6379> exec
1) OK
2) (error) ERR value is not an integer or out of range
3) OK
4) "v2"

```

### **监控（ watch）**

watch在事务开启前，事务结束后，watch一起结束

悲观锁：很悲观，无论做什么都加锁

乐观锁：很乐观，不上锁，更新的时候，对数据是否冲突进行检测。

> 正常执行成功

```bash
127.0.0.1:6379> set money 100
OK
127.0.0.1:6379> set out 0
OK
127.0.0.1:6379> watch money
OK
127.0.0.1:6379> multi
OK
127.0.0.1:6379> decrby money 20
QUEUED
127.0.0.1:6379> incrby out 20
QUEUED
127.0.0.1:6379> exec
1) (integer) 80
2) (integer) 20

```

> 多线程修改，使用watch 可以当做乐观锁操作
>
> 若修改失败，先unwatch再重新watch直到事务成功  实现乐观锁

```bash
#线程1
127.0.0.1:6379> watch money
OK
127.0.0.1:6379> multi
OK
127.0.0.1:6379> decrby money 10
QUEUED
127.0.0.1:6379> incrby out 10
QUEUED
127.0.0.1:6379> exec #执行前，线程2修改了本线程 watch的值，导致当前事务失败
(nil)
#######################################
#线程2
127.0.0.1:6379> get money
"80"
127.0.0.1:6379> set money 1000
OK
#######################################
#线程1
127.0.0.1:6379> unwatch
OK
127.0.0.1:6379> watch money
OK
127.0.0.1:6379> multi
OK
127.0.0.1:6379> decrby money 10
QUEUED
127.0.0.1:6379> incrby out 10
QUEUED
127.0.0.1:6379> exec
1) (integer) 990
2) (integer) 30


```

## SpringBoot整合Redis

通过SpingData操作 redis



**源码解析  RedisAutoConfiguration**

```java
@Bean
//条件判断，我们可以自定义一个redisTemplate来替换这个默认的
@ConditionalOnMissingBean(name = "redisTemplate")
public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory)
   throws UnknownHostException {
   //默认的RedisTemplate没有过多的设置，redis对象是需要序列化的
   //两个泛型都是Object，以后试用需要强转<String,Object>
   RedisTemplate<Object, Object> template = new RedisTemplate<>();
   template.setConnectionFactory(redisConnectionFactory);
   return template;
}

@Bean
@ConditionalOnMissingBean//鱿鱼String是redis最常使用的类型，为他专门设计了顶层api
public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory)
   throws UnknownHostException {
   StringRedisTemplate template = new StringRedisTemplate();
   template.setConnectionFactory(redisConnectionFactory);
   return template;
}
```

**源码解析 RedisTemplate**

序列化配置

![image-20200406105953025](2020-4-5 Redis 180015.assets\image-20200406105953025.png)

![image-20200406110134527](2020-4-5 Redis 180015.assets\image-20200406110134527.png)





1、导入依赖

```xml
<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

2、配置文件配置

```properties
spring.redis.host=127.0.0.1
spring.redis.port=6379
```

3、测试1

```java
package com.kuang;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
class SpringbootRedisApplicationTests {

   @Autowired
   private RedisTemplate redisTemplate;
   @Autowired
   private StringRedisTemplate stringRedisTemplate;
   @Test
   void contextLoads() {
      //*****  redisTemplate API******
      //opsForValue 操作字符串 类似String
      //opsForList 操作列表 类似List
      //opsForSet
      //opsForHash
      //opsForZSet
      //opsForGeo
      //略略略
      redisTemplate.opsForValue().set("mykey","kuangshen");
      System.out.println(redisTemplate.opsForValue().get("mykey"));

      stringRedisTemplate.
   }

}
```

3.5、乱七八糟的知识点（springboot zookeeper里有讲）

Redis把对象作为value时，需要将对象**序列化**，否则会报错

==在企业中，我们对所有的pojo类进行序列化。==

实现序列化很简单，只需要将类实现Serializable接口即可。



4、自己实现RedisTemplate（选做题）

序列化方式由JDK序列化更改为Json序列化，性能有所不同，详情见视频



5、编写工具类RedisUtil（选做题）

再次封装RedisTemplate中API，只包括RedisTemplate中常用方法，用起来更舒服一点

## Redis.conf详解

> units单位对大小写不敏感

![image-20200406154038849](C:\Users\22815\Desktop\notes\2020-4-5 Redis 180015.assets\image-20200406154038849.png)

> INCLUDES 

![image-20200406154257179](C:\Users\22815\Desktop\notes\2020-4-5 Redis 180015.assets\image-20200406154257179.png)

> 网络  NETWORK 	

```bash
bind 127.0.0.1 #绑定的ip

protected-mode yes #保护模式

port 6379 #端口设置
```

![image-20200406154523744](C:\Users\22815\Desktop\notes\2020-4-5 Redis 180015.assets\image-20200406154523744.png)

> 通用 GENERAL

```bash 
daemonize yes # 以守护进程方式运行，默认为NO,需要自己开为yes
pidfile /var/run/redis_6379.pid #以后台方式（守护进程）运行，则需指定一个pid进程文件

# 日志
# Specify the server verbosity level.
# This can be one of:
# debug (a lot of information, useful for development/testing)
# verbose (many rarely useful info, but not a mess like the debug level)
# notice (moderately verbose, what you want in production probably)
# warning (only very important / critical messages are logged)
loglevel notice
logfile "" #日志的文件位置，为空则单纯输出
datebases 16 #默认16个数据库


```

> 快照 SNAPSHOTTING

持久化，在规定的时间内，执行了多少次操作，则会用到持久化文件 .rdb .aof

Redis是内存数据库，若没有持久化，则数据断电既狮

```bash
save 900 1 #900s内，若至少有1个key进行了修改，则进行持久化操作
save 300 10 #同上
save 60 10000 #同上

stop-writes-on-bgsave-error yes #持久化若出错，是否继续redis运行

#是否压缩rdb文件，会消耗cpu资源
rdbcompression yes


#保存rdb文件时，进行校验
rdbchecksum yes

#rdb文件保存目录
dir ./
```



> 复制 REPLICATION  通过配置文件实现主从复制，从机在配置文件里认大哥



> 安全 SECURITY

可以设置Redis密码，8过主要是用命令设置

> 限制 LIMITS

```bash
maxclients 10000 #最大客户端数

maxmemory <bytes> #最大内存

maxmemory-policy noeviction #内存到达上限的处理策略
```





> aof配置   APPEND ONLY MODE

```bash
appendonly no #默认不开启aof模式，默认使用rdb持久化方式，在大部分的情况使用rdb即可

appendfilename "appendonly.aof" #持久化文件名

appendfsync everysec #aof同步策略 每秒执行一次



```

## Redis持久化

数据都是缓存在内存中的，当你重启系统或者关闭系统后，缓存在内存中的数据都会消失殆尽，再也找不回来了。所以，为了让数据能够长期保存，就要将 Redis 放在缓存中的数据做持久化存储。

### RDB(Redis DataBase)持久化

RDB持久化是指在指定的时间间隔内将内存中的数据集快照写入磁盘，实际操作过程是fork一个子进程，先将数据集写入临时文件，写入成功后，再替换之前的文件，用二进制压缩存储。

![img](2020-4-5 Redis 180015.assets\388326-20170726161552843-904424952.png)

> 触发机制

1、满足save规则的条件

2、执行flushall命令

3、退出redis

备份自动生成 dump.rdb 文件

> 恢复rdb文件

将rdb文件放到redis启动目录，redis启动时会自动检查dump.rdb 恢复其中数据

**优点：**

1、适合大规模的数据恢复

**缺点**：

1、系统一旦在定时持久化之前出现宕机现象，此前没有来得及写入磁盘的数据都将丢失。

2、由于RDB是通过fork子进程来协助完成数据持久化工作的，占据内存空间





### AOF（append only file）持久化

AOF持久化以日志的形式记录服务器所处理的每一个写、删除操作，查询操作不会记录，以文本的方式记录，可以打开文件看到详细的操作记录。

默认不开启，需要在配置文件中打开。

默认同步策略 每秒钟同步一次（可能会丢失一秒的数据）

![img](2020-4-5 Redis 180015.assets\388326-20170726161604968-371688235.png)

生成appendonly.aof文件



**优点**：

1、该机制可以带来更高的数据安全性，即数据持久性。Redis中提供了3中同步策略，即每秒同步、每修改同步和不同步。事实上，每秒同步也是异步完成的，其效率也是非常高的，所差的是一旦系统出现宕机现象，那么这一秒钟之内修改的数据将会丢失。而每修改同步，我们可以将其视为同步持久化，即每次发生的数据变化都会被立即记录到磁盘中。可以预见，这种方式在效率上是最低的。至于无同步，无需多言，我想大家都能正确的理解它。

**缺点**：	

1、对于相同数量的数据集而言，AOF文件通常要大于RDB文件。RDB 在恢复大数据集时的速度比 AOF 的恢复速度要快

2、根据同步策略的不同，AOF在运行效率上往往会慢于RDB。





## Redis发布订阅

Redis 发布订阅(pub/sub)是一种消息通信模式：发送者(pub)发送消息，订阅者(sub)接收消息。

![image-20200406195342664](C:\Users\22815\Desktop\notes\2020-4-5 Redis 180015.assets\image-20200406195342664.png)



订阅端：

```bash
127.0.0.1:6379> SUBSCRIBE redisChat  #订阅一个频道 redisChat
Reading messages... (press Ctrl-C to quit) #订阅端一直处于监听过程
1) "subscribe"
2) "redisChat"
3) (integer) 1
```

发送端：

```bash
127.0.0.1:6379> PUBLISH redisChat "Redis is a great caching technique" #发布消息到频道
(integer) 1
127.0.0.1:6379> PUBLISH redisChat "Learn redis by runoob.com"
(integer) 1
```

发送端发布后，订阅端：

```bash
127.0.0.1:6379> SUBSCRIBE redisChat  #订阅一个频道 redisChat
Reading messages... (press Ctrl-C to quit) #订阅端一直处于监听过程
1) "subscribe"
2) "redisChat"
3) (integer) 1
1) "message"
2) "redisChat"
3) "Redis is a great caching technique"
1) "message"
2) "redisChat"
3) "Learn redis by runoob.com"
```

## Redis主从复制

**概念**

主从复制，是指将一台Redis服务器的数据，复制到其他的Redis服务器。前者称为主节点，后者称为从节点；**数据的复制是单向的，只能由主节点到从节点。**

**默认的情况下，每台服务器都是主机。**一般情况下只要配置从机即可。

如果是由命令行操作来配置的从机，一但从机重启，就会变成主机。建议在配置文件里配置。

==主机可以写+读，从机只能读。==

**主从复制的作用**

1、数据冗余：实现数据的热备份，是持久化意外的一种数据冗余方式。

2、故障恢复：当主节点出现问题时，可以由从节点提供服务，实现快速的故障恢复。

3、负载均衡：在主从复制的基础上，配合读写分离，可以由主节点提供写服务，由从节点提供读服务，分担服务器负载。

4、高可用基石：主从复制还是哨兵和集群能够实施的基础。



> slaveof命令

如图，想让6380节点成为6379的从节点，只需要执行 `slaveof` 命令即可，此复制命令是异步进行的，redis会自动进行后续数据复制的操作。
 注：一般生产环境不允许主从节点都在一台机器上，因为没有任何的价值。

![img](2020-4-5 Redis 180015.assets\14795543-0f267981152a248a.webp)

### 哨兵模式

主从切换技术的方法是：当主服务器宕机后，需要手动把一台从服务器切换为主服务器，这就需要人工干预，费事费力，还会造成一段时间内服务不可用。这不是一种推荐的方式，更多时候，我们优先考虑哨兵模式。

哨兵模式是一种特殊的模式，首先Redis提供了哨兵的命令，**哨兵是一个独立的进程，作为进程，它会独立运行。**其原理是**哨兵通过发送命令，等待Redis服务器响应，从而监控运行的多个Redis实例。**

![img](2020-4-5 Redis 180015.assets\11320039-3f40b17c0412116c.webp)

假设主服务器宕机，哨兵1先检测到这个结果，系统并不会马上进行failover过程，仅仅是哨兵1主观的认为主服务器不可用，这个现象成为**主观下线**。当后面的哨兵也检测到主服务器不可用，并且数量达到一定值时，那么哨兵之间就会进行一次投票，投票的结果由一个哨兵发起，进行failover操作。切换成功后，就会通过发布订阅模式，让各个哨兵把自己监控的从服务器实现切换主机，这个过程称为**客观下线**。



## Redis缓存穿透与雪崩

### 缓存穿透（查不到）

**布隆过滤器**

**缓存空对象**

### 缓存击穿（查询量过大）

### 缓存雪崩