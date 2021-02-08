package cn.mirror6.rabbitmq.demo;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author ：gong sun
 * @description: 点对点消息生产者
 * @date ：Created in 2021/2/8 9:14 下午
 */
public class Product {

    public static void main(String[] args) {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(USER);
        factory.setPassword(PASS);
        factory.setHost(HOST);
        factory.setPort(PORT);

        try {
            //创建连接
            Connection conn = factory.newConnection();
            //创建管道
            Channel channel = conn.createChannel();
            //声明队列
            //参数1：定义的队列名称
            //参数2：队列中的数据是否持久化（如果选择了持久化）
            //参数3: 是否排外（当前队列是否为当前连接私有）
            //参数4：自动删除（当此队列的连接数为0时，此队列会销毁（无论队列中是否还有数据））
            //参数5：设置当前队列的参数
            channel.queueDeclare("queue1", true, false, false, null);
            byte[] messageBodyBytes = "Hello, world!".getBytes();
            //推送
            //参数1：交换机名称，如果直接发送信息到队列，则交换机名称为""
            //参数2：目标队列名称
            //参数3：设置当前这条消息的属性（设置过期时间 10）
            //参数4：消息的内容
            channel.basicPublish("", "queue1", null, messageBodyBytes);
            channel.close();
            conn.close();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    private static final String USER = "guest";
    private static final String PASS = "guest";
    private static final String HOST = "localhost";
    private static final int PORT = 5672;

}
