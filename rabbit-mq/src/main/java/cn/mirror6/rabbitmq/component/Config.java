package cn.mirror6.rabbitmq.component;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ：gong sun
 * @description:
 * @date ：Created in 2021/2/8 10:12 下午
 */
@Configuration
public class Config {

    @Bean
    public Queue queue() {
        return new Queue("queue");
    }
}
