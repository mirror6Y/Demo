package cn.mirror6.rabbitmq.component;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author ：gong sun
 * @description: 生产者
 * @date ：Created in 2021/2/8 9:36 下午
 */
@Component
public class Producer {

    @Resource
    private AmqpTemplate template;

    public void send() {
        template.convertAndSend("queue", "hello,rabbit~");
    }

}
