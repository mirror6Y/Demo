package cn.mirror6.elasticsearch.component;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ：gong sun
 * @description: es配置类
 * @date ：Created in 2021/2/10 4:00 下午
 */
@Configuration
public class Config {

    @Bean
    public RestHighLevelClient getClient() {
        return new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));
    }
}
