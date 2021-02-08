package cn.mirror6.rabbitmq.demo;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author ：gong sun
 * @description: 消息消费者
 * @date ：Created in 2021/2/8 11:56 下午
 */
public class Consumer {

    public static void main(String[] args) {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(USER);
        factory.setPassword(PASS);
        factory.setHost(HOST);
        factory.setPort(PORT);

        try {
            Connection conn = factory.newConnection();
            Channel channel = conn.createChannel();
            channel.basicConsume("queue2", true, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    System.out.println("consumer queue2:=" + new String(body));
                }
            });

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
