package cn.mirror6.rabbitmq.component;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author ：gong sun
 * @description: 消费者
 * @date ：Created in 2021/2/9 12:16 上午
 */
@Component
public class Consumer {
    /**
     * 监听器监听指定的Queue
     */
    @RabbitListener(queues = "queue")
    public void receive(String str) {
        System.out.println("Receive:" + str);
    }

}
