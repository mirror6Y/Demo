import cn.mirror6.elasticsearch.ElasticsearchApplication;
import cn.mirror6.elasticsearch.component.Api;
import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * @author ：gong sun
 * @description:
 * @date ：Created in 2021/2/10 4:35 下午
 */
@SpringBootTest(classes = ElasticsearchApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class ApiTest {

    @Resource
    private Api api;

    @Test
    public void exist() {
        System.out.println(JSON.toJSON(api.exist()));
    }

    @Test
    public void createIndex() {
        System.out.println(JSON.toJSON(api.create()));
    }

    @Test
    public void deleteIndex() {
        System.out.println(JSON.toJSON(api.delete()));
    }

    @Test
    public void existDoc() {
        System.out.println(JSON.toJSON(api.exitsDoc()));
    }

    @Test
    public void getDoc() {
        System.out.println(JSON.toJSON(api.getDoc()));
    }

    @Test
    public void createDoc() {
        System.out.println(JSON.toJSON(api.createDoc()));
    }

    @Test
    public void updateDoc() {
        System.out.println(JSON.toJSON(api.updateDoc()));
    }

    @Test
    public void deleteDoc() {
        System.out.println(JSON.toJSON(api.deleteDoc()));
    }

    @Test
    public void batchCreateDoc() {
        System.out.println(JSON.toJSON(api.batchCreateDoc()));
    }

    @Test
    public void searchDoc() {
        System.out.println(JSON.toJSON(api.searchDoc()));
    }
}
