import cn.mirror6.rabbitmq.RabbitMqApplication;
import cn.mirror6.rabbitmq.component.Producer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * @author ：gong sun
 * @description:
 * @date ：Created in 2021/2/8 10:15 下午
 */
@SpringBootTest(classes= RabbitMqApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class ProductTest {

    @Resource
    private Producer producer;

    @Test
    public void sendMsg(){
        producer.send();
    }
}
