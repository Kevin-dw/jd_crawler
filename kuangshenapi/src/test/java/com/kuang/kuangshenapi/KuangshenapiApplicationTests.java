package com.kuang.kuangshenapi;


import com.alibaba.fastjson.JSON;
import com.kuang.pojo.User;
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
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class KuangshenapiApplicationTests {

    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;
    @Test
    void testCreatIndex() throws IOException {
        //1、测试索引的创建
        CreateIndexRequest request = new CreateIndexRequest("duwen_index");
        //2、执行请求
        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(createIndexResponse);

    }
    @Test
    void testExistIndex() throws IOException {
        GetIndexRequest request=new GetIndexRequest("ku_index");
        boolean exists=client.indices().exists(request,RequestOptions.DEFAULT);
        System.out.println(exists);
    }
    @Test
    //删除
    void testDeleteIndex() throws IOException {
        DeleteIndexRequest request=new DeleteIndexRequest("ku_index");
        AcknowledgedResponse delete= client.indices().delete(request, RequestOptions.DEFAULT);
        System.out.println(delete.isAcknowledged());
    }
    //添加文档
    @Test
    void testAddDocument() throws IOException {
        User user = new User("杜文",3);
        //创建请求
        IndexRequest indexRequest=new IndexRequest("duwen_index");
        //规则put/duwen_index/_doc/1
        indexRequest.id("1");
        indexRequest.timeout(TimeValue.timeValueSeconds(1));
        //或者indexRequest.timeout("1s");
        //将数据放入请求 json
        IndexRequest source = indexRequest.source(JSON.toJSONString(user), XContentType.JSON);
        //客户端发送请求
        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println(indexResponse.toString());//返回IndexResponse[index=duwen_index,type=_doc,id=1,version=1,result=created,seqNo=0,primaryTerm=1,shards={"total":2,"successful":1,"failed":0}]
        System.out.println(indexResponse.status());//对应命令返回状态CREATEd
    }
    //获取文档，判断是否存在 get/index/doc/1
    @Test
    void testIsExists() throws IOException {
        GetRequest getRequest = new GetRequest("duwen_index", "1");
        getRequest.fetchSourceContext(new FetchSourceContext(false));//过滤，不获取返回的sources上下文
        getRequest.storedFields("_none_");//不获取排序字段
        boolean exists=client.exists(getRequest,RequestOptions.DEFAULT);
        System.out.println(exists);
    }
    //获取文档信息
    @Test
    void testGetDocument() throws IOException {
        GetRequest getRequest = new GetRequest("duwen_index", "1");
        boolean exists=client.exists(getRequest,RequestOptions.DEFAULT);
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        System.out.println(getResponse.getSourceAsString());//打印文档内容
        System.out.println(getResponse);//返回全部内容和命令
    }
    //更新文档信息
    @Test
    void testUpdateDocument() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest("duwen_index", "1");
        updateRequest.timeout("1s");
        User user=new User("李彦宏",55);
        updateRequest.doc(JSON.toJSONString(user),XContentType.JSON);
        UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println(updateResponse.toString());
        System.out.println(updateResponse.status());
    }
    //删除文档信息
    @Test
    void testDelete() throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest("duwen_index","1");
        deleteRequest.timeout(TimeValue.timeValueSeconds(1));
        DeleteResponse delete = client.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(delete.status());
    }
    //批量插入数据
    @Test
    void testBulkRequest() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("1s");

        ArrayList<User> userArrayList=new ArrayList<>();
        userArrayList.add(new User("BB",1));
        userArrayList.add(new User("BB",2));
        userArrayList.add(new User("BB",3));
        userArrayList.add(new User("杜",4));
        userArrayList.add(new User("杜文",5));
        userArrayList.add(new User("杜文",5));

        for (int i = 0; i < userArrayList.size(); i++) {
            //批量更新和删除直接在这里修改就可以了
            bulkRequest.add(new IndexRequest("duwen_index")
                    .id(""+(i+1))
                    .source(JSON.toJSONString(userArrayList.get(i)),XContentType.JSON));
        }
        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulkResponse.hasFailures());//返回falsefalse代表成功，没有失败
    }

    //查询
    //SearchRequest搜索请求
    //SearchsourceBuilder条件构造
    //HighlightBuilder构建高亮
    // TermQueryBuilder精确查询
    //MatchALLQueryBuilder
    //xxx QueryBuilder对应我们刚才看到的命令
    @Test
    void testSearch() throws IOException {
        SearchRequest searchRequest = new SearchRequest("duwen_index");
        //构建搜索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //可以设置高亮
        sourceBuilder.highlighter();
        //QueryBuilders工具类匹配
        TermQueryBuilder termQuery = QueryBuilders.termQuery("name", "杜文");
        //MatchAllQueryBuilder allQueryBuilder = QueryBuilders.matchAllQuery();//匹配所有
        sourceBuilder.query(termQuery);
        //分页
/*        sourceBuilder.from();
        sourceBuilder.size();*/
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));//延时时间60秒
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(JSON.toJSONString(searchResponse.getHits()));
        System.out.println("==========================================");
        for (SearchHit documentFields : searchResponse.getHits().getHits()) {
            System.out.println(documentFields.getSourceAsMap());
        }
    }
}
