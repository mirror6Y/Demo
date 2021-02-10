package cn.mirror6.elasticsearch.component;

import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author ：gong sun
 * @description:
 * @date ：Created in 2021/2/10 4:31 下午
 */
@Component
public class Api {

    @Resource
    private Config config;

    public IndicesClient getIndex() {
        return config.getClient().indices();
    }

    /**
     * 判断索引是否存在
     *
     * @return bool
     */
    public boolean exist() {
        GetIndexRequest request = new GetIndexRequest("twitter");
        boolean exists = false;
        try {
            exists = config.getClient().indices().exists(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return exists;
    }

    /**
     * 创建索引
     *
     * @return res
     */
    public CreateIndexResponse create() {
        CreateIndexRequest request = new CreateIndexRequest("twitter");
        request.settings(Settings.builder()
                .put("index.number_of_shards", 3)
                .put("index.number_of_replicas", 2)
        );

        Map<String, Object> message = new HashMap<>();
        message.put("type", "text");
        Map<String, Object> properties = new HashMap<>();
        properties.put("message", message);
        Map<String, Object> mapping = new HashMap<>();
        mapping.put("properties", properties);

        request.mapping(String.valueOf(mapping));
        request.alias(new Alias("twitter_alias").filter(QueryBuilders.termQuery("user", "kimchy")));
        request.source("{\n" +
                "    \"settings\" : {\n" +
                "        \"number_of_shards\" : 1,\n" +
                "        \"number_of_replicas\" : 0\n" +
                "    },\n" +
                "    \"mappings\" : {\n" +
                "        \"properties\" : {\n" +
                "            \"message\" : { \"type\" : \"text\" }\n" +
                "        }\n" +
                "    },\n" +
                "    \"aliases\" : {\n" +
                "        \"twitter_alias\" : {}\n" +
                "    }\n" +
                "}", XContentType.JSON);
        CreateIndexResponse response = null;
        try {
            response = config.getClient().indices().create(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 删除索引
     *
     * @return res
     */
    public AcknowledgedResponse delete() {
        DeleteIndexRequest request = new DeleteIndexRequest("twitter");
        try {
            return config.getClient().indices().delete(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 判断文档是否存在
     *
     * @return res
     */
    public boolean exitsDoc() {
        GetIndexRequest request = new GetIndexRequest("schools", "0");
        try {
            return config.getClient().indices().exists(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取文档
     *
     * @return res
     */
    public GetResponse getDoc() {
        GetRequest request = new GetRequest("schools", "0");
        try {
            return config.getClient().get(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 创建文档
     *
     * @return res
     */
    public IndexResponse createDoc() {
        Map<String, String> map = new HashMap<>();
        map.put("班级", "1");
        map.put("人数", "2");
        map.put("楼层", "3");
        IndexRequest request = new IndexRequest("schools");
        request.id("0");
        request.timeout(TimeValue.timeValueSeconds(1L));
        request.source(JSON.toJSON(map), XContentType.JSON);

        try {
            return config.getClient().index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 编辑文档
     *
     * @return res
     */
    public UpdateResponse updateDoc() {
        UpdateRequest request = new UpdateRequest("schools", "0");
        request.timeout("1s");
        Map<String, String> map = new HashMap<>();
        map.put("班级1", "1");
        map.put("人数2", "2");
        map.put("楼层3", "3");
        request.doc(map, XContentType.JSON);
        try {
            return config.getClient().update(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 删除文档
     *
     * @return res
     */
    public DeleteResponse deleteDoc() {
        DeleteRequest request = new DeleteRequest("schools", "0");
        request.timeout("1s");
        try {
            return config.getClient().delete(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 批量创建文档 批量删除、批量修改类似
     *
     * @return res
     */
    public BulkResponse batchCreateDoc() {
        BulkRequest request = new BulkRequest();
        request.timeout("10s");
        ArrayList<Object> data = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Map<String, String> map = new HashMap<>();
            map.put("" + i, "" + i);
            data.add(map);
        }
        for (int i = 0; i < data.size(); i++) {
            request.add(new IndexRequest("schools").id("" + i).source(JSON.toJSON(data.get(i)), XContentType.JSON));
        }
        try {
            return config.getClient().bulk(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 高级查询
     *
     * @return res
     */
    public SearchResponse searchDoc() {
        SearchRequest request = new SearchRequest("schools");
        //构建搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //查询条件 使用QueryBuilders
        //termQuery 精准匹配 matchAllQuery 模糊匹配
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "6");
        searchSourceBuilder.query(termQueryBuilder);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        request.source(searchSourceBuilder);
        try {
            return config.getClient().search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
