# rabbitMq

### **六种队列模型**

#### 1.简单模式【Hello World】

![img](https://www.rabbitmq.com/img/tutorials/python-one-overall.png)

1. 消息产生者将消息放入队列

2. 消息的消费者(consumer) 监听(listen) 消息队列，如果队列中有消息，就消费掉，消息被拿走后，自动从队列中删除

   **隐患**:消息可能没有被消费者正确处理，已经从队列中消失了，造成消息的丢失

   **应用场景**:聊天(中间有一个过渡的服务器;p端，c端)

#### 2.工作队列【Work queues】(资源的竞争)

![img](https://www.rabbitmq.com/img/tutorials/python-two.png)

1. 消息产生者将消息放入队列

2. 消费者可以有多个，消费者1，消费者2，同时监听同一个队列。消息被消费?C1 C2同时争夺当前消息队列的内容，谁先拿到谁负责消费消息

   **隐患**:高并发情况下，默认会产生某一个消息被多个消费者共同使用，可以设置一个开关(syncronize，与同步锁的性能不一样) 保证一条消息只能被一个消费者使用

   **应用场景**:红包;大项目中的资源调度(任务分配系统不需知道哪一个任务执行系统在空闲，直接将任务扔到消息队列中，空闲的系统自动争抢)

#### 3.发布订阅【Publish/Subscribe】(共享资源)

![img](https://www.rabbitmq.com/img/tutorials/exchanges.png)

1. X代表交换机，rabbitMQ内部组件，erlang 消息产生者是代码完成，代码的执行效率不高

2. 消息产生者将消息放入交换机

3. 交换机发布订阅把消息发送到所有消息队列中

4. 对应消息队列的消费者拿到消息进行消费

   **应用场景**:邮件群发，群聊天，广播(广告)

#### 4.路由模式【Routing】

![img](https://www.rabbitmq.com/img/tutorials/direct-exchange.png)

1. 消息生产者将消息发送给交换机

2. 按照路由判断,路由是字符串(info) 当前产生的消息携带路由字符(对象的方法),交换机根据路由的key,只能匹配上路由key对应的消息队列,对应的消费者才能消费消息;

3. 根据业务功能定义路由字符串

   **应用场景**:从系统的代码逻辑中获取对应的功能字符串,将消息任务扔到对应的队列中业务场景:error 通知;EXCEPTION;错误通知的功能;传统意义的错误通知;客户通知;利用key路由,可以将程序中的错误封装成消息传入到消息队列中,开发者可以自定义消费者,实时接收错误;

#### 5.主题模式【Topics】

![img](https://www.rabbitmq.com/img/tutorials/python-five.png)

1. 消息生产者产生消息,把消息交给交换机
2. 交换机根据key的规则模糊匹配到对应的队列,由队列的监听消费者接收消息消费
3. 星号 可以代替一个单词
4. 井号 可以替代零个或多个单词

#### 6.RPC

参考： https://www.cnblogs.com/Jeely/p/10784013.html

### 消息丢失

#### 一.丢失的场景

**1.生产者丢失数据**：生产者将数据发送到mq的时候，可能在传输过程中因为网络等问题而将数据弄丢了

**2.消费者丢失数据**：主要是因为消费者消费时，刚消费到，还没有处理，结果消费者就挂了。这样你重启之后，mq就认为你已经消费过了，然后就丢了数据

**3.消息队列丢失数据**：如果没有开启mq的持久化，那么mq一旦重启，数据就丢了。所以必须开启持久化将消息持久化到磁盘，这样就算mq挂了，恢复之后也会自动读取之前存储的数据。一般情况数据就不会再丢失了，除非极其罕见的情况，mq还没来得及持久化自己就挂了，这样可能导致一部分数据丢失

#### 二.解决方案

**1.生产者防止数据丢失**：

A.事务:可以选择使用mq提供事物功能，即生产者在发送数据之前开启事物，然后发送消息，如果消息没有成功被mq接收到，那么生产者会受到异常报错，这时就可以回滚事物，然后尝试重新发送；如果收到了消息，那么就可以提交事物

```
 channel.txSelect();//开启事物
  try{
      //发送消息
  }catch(Exection e){
      channel.txRollback()；//回滚事物
      //重新提交
  }
```

缺点：mq事物一开启，就会变为同步阻塞操作，生产者会阻塞等待是否发送成功，太耗性能，会造成吞吐量的下降。

B.confirm模式：在生产者那里设置开启confirm模式之后，每次写的消息都会分配一个唯一的id，然后如果写入了mq之中，mq会给你返回一个ack消息，告诉你这个消息发送成功；如果mq没有处理这个消息，会给你返回一个nack接口，告诉你这个消息发送失败，你可以进行重试。而且你可以结合这个机制知道自己在内存里维护每个消息的id，如果超过一定时间还没接收到这个消息的回调，那么你可以进行重发

    //开启confirm
    channel.confirm();
    //发送成功回调
    public void ack(String messageId){
      
    }
    
    // 发送失败回调
    public void nack(String messageId){
        //重发该消息
    }
**二者区别:**事务机制是同步的，你提交了一个事物之后会阻塞住，但是confirm机制是异步的，发送消息之后可以接着发送下一个消息，然后mq会返回回调结果告知成功与否。 一般在生产者这块避免丢失，都是用confirm机制

**2.消费者防止数据丢失：**

使用mq提供的ack机制，首先关闭mq的自动ack，然后每次在确保处理完这个消息之后，在代码里手动调用ack。这样就可以避免消息还没有处理完就ack。

**3.消息队列防止数据丢失：**

设置消息持久化到磁盘。设置持久化有两个步骤：
 ①创建queue的时候将其设置为持久化的，这样就可以保证mq持久化queue的元数据，但是不会持久化queue里面的数据。
 ②发送消息的时候将消息的deliveryMode设置为2，这样消息就会被设为持久化方式，此时mq就会将消息持久化到磁盘上。 

必须要同时开启这两个才可以。

而且持久化可以跟生产的confirm机制配合起来，只有消息持久化到了磁盘之后，才会通知生产者ack，这样就算是在持久化之前mq挂了，数据丢了，生产者收不到ack回调也会进行消息重发。


![img](https://user-gold-cdn.xitu.io/2019/5/22/16adffbe72c60f3e?imageslim)

参考：https://juejin.cn/post/6844903849099018253

### 重复消费

#### **一.幂等性**

幂等性是一个数学与计算机学概念，常见于抽象代数中。 在编程中一个幂等操作的特点是其任意多次执行所产生的影响均与一次执行的影响相同

简单来说，幂等性就是一个数据或者一个请求，给你重复来了多次，你得确保对应的数据是不会改变的，不能出错

#### 二.任何消息中间件都可能产生重复消费的问题

#### 三.保证幂等性操作

保证幂等性即可以解决重复消费的问题，不过这个要结合业务的类型来进行处理。下面提供几个思路供参考：

（1）、可在内存中维护一个set，只要从消息队列里面获取到一个消息，先查询这个消息在不在set里面，如果存在表示已消费过，直接丢弃；如果不存在，则在消费后将其加入set当中。

（2）、如果要写数据库，可以拿唯一键先去数据库查询一下，如果不存在再写，如果存在直接更新或者丢弃消息。

（3）、如果是写redis那没有问题，每次都是set，天然的幂等性。

（4）、让生产者发送消息时，每条消息加一个全局的唯一id，然后消费时，将该id保存到redis里面。消费时先去redis里面查一下有么有，没有再消费。

（5）、数据库操作可以设置唯一键，防止重复数据的插入，这样插入只会报错而不会插入重复数据。

参考：https://juejin.cn/post/6844903849094807560

### 顺序错乱

#### 一.定义

消息队列中的若干消息如果是对同一个数据进行操作，这些操作具有前后的关系，必须要按前后的顺序执行，否则就会造成数据异常。举例： 比如通过mysql binlog进行两个数据库的数据同步，由于对数据库的数据操作是具有顺序性的，如果操作顺序搞反，就会造成不可估量的错误。比如数据库对一条数据依次进行了 插入->更新->删除操作，这个顺序必须是这样，如果在同步过程中，消息的顺序变成了 删除->插入->更新，那么原本应该被删除的数据，就没有被删除，造成数据的不一致问题。

#### 二.错乱场景

①一个queue，有多个consumer去消费，这样就会造成顺序的错误，consumer从mq里面读取数据是有序的，但是每个consumer的执行时间是不固定的，无法保证先读到消息的consumer一定先完成操作，这样就会出现消息并没有按照顺序执行，造成顺序错乱

![img](https://user-gold-cdn.xitu.io/2019/5/22/16adfff98df3c094?imageslim)

②一个queue对应一个consumer，但是consumer里面进行了多线程消费，这样也会造成消息消费顺序错误。

![img](https://user-gold-cdn.xitu.io/2019/5/22/16adfff98e664706?imageslim)

#### 三.解决方案

①拆分多个queue，每个queue对应一个consumer，就是多一些queue而已，确实是麻烦点；这样也会造成吞吐量下降，可以在消费者内部采用多线程的方式取消费

![img](https://user-gold-cdn.xitu.io/2019/5/22/16adfff994b336de?imageslim)

②或者就一个queue但是对应一个consumer，然后这个consumer内部用内存队列做排队，然后分发给底层不同的worker来处理

![img](https://user-gold-cdn.xitu.io/2019/5/22/16adfff995c91bfc?imageslim)

参考：https://juejin.cn/post/6844903849103196173

### 消息积压

#### 一.大量消息在mq里积压了几个小时了还没解决

**场景：**

几千万条数据在mq里积压了七八个小时，从下午4点多，积压到了晚上很晚，10点多，11点多。线上故障了，这个时候要不然就是修复consumer的问题，让他恢复消费速度，然后傻傻的等待几个小时消费完毕。这个肯定不行。一个消费者一秒是1000条，一秒3个消费者是3000条，一分钟是18万条，1000多万条。 所以如果你积压了几百万到上千万的数据，即使消费者恢复了，也需要大概1小时的时间才能恢复过来。

**解决方案：**

这种时候只能操作临时扩容，以更快的速度去消费数据了。具体操作步骤和思路如下：
 ①先修复consumer的问题，确保其恢复消费速度，然后将现有consumer都停掉。

②临时建立好原先10倍或者20倍的queue数量(新建一个topic，partition是原来的10倍)。

③然后写一个临时分发消息的consumer程序，这个程序部署上去消费积压的消息，消费之后不做耗时处理，直接均匀轮询写入临时建好分10数量的queue里面。

④紧接着使用10倍的机器来部署consumer，每一批consumer消费一个临时queue的消息。

⑤这种做法相当于临时将queue资源和consumer资源扩大10倍，以正常速度的10倍来消费消息。

⑥等快速消费完了之后，恢复原来的部署架构，重新用原来的consumer机器来消费消息。

![img](https://user-gold-cdn.xitu.io/2019/5/22/16ae001392a9584e?imageslim)

#### 二.消息设置了过期时间，过期就丢了怎么办

假设你用的是rabbitmq，rabbitmq是可以设置过期时间的，就是TTL，如果消息在queue中积压超过一定的时间就会被rabbitmq给清理掉，这个数据就没了。那这就是第二个坑了。这就不是说数据会大量积压在mq里，而是大量的数据会直接搞丢。

**解决方案：**

这种情况下，实际上没有什么消息挤压，而是丢了大量的消息。所以第一种增加consumer肯定不适用。 这种情况可以采取 “批量重导” 的方案来进行解决。 在流量低峰期(比如夜深人静时)，写一个程序，手动去查询丢失的那部分数据，然后将消息重新发送到mq里面，把丢失的数据重新补回来。

#### 三.积压消息长时间没有处理，mq放不下了怎么办

如果走的方式是消息积压在mq里，那么如果你很长时间都没处理掉，此时导致mq都快写满了，咋办？这个还有别的办法吗？

**解决方案：**

这个就没有办法了，肯定是第一方案执行太慢，这种时候只好采用 “丢弃+批量重导” 的方式来解决了。

首先，临时写个程序，连接到mq里面消费数据，收到消息之后直接将其丢弃，快速消费掉积压的消息，降低MQ的压力，然后走第二种方案，在晚上夜深人静时去手动查询重导丢失的这部分数据

参考：https://juejin.cn/post/6844903849107406856
 

