package com.tll.service;

import com.alibaba.fastjson.JSON;
import com.tll.config.ElasticSerachClientConfig;
import com.tll.pojo.Content;
import com.tll.utils.HtmlParseUtil;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.Highlighter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author tll
 * @create 2020/6/23 9:09
 * 前端业务编写
 */
@Service
public class ContentService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    //1.解析数据放入es中
    public Boolean  parseContent(String keywords) throws IOException {
        List<Content> contents = new HtmlParseUtil().parseJD(keywords);
        //把查询的数据放入到es中批量插入
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("2m");
        for (int i = 0; i <contents.size() ; i++) {
            bulkRequest.add(new IndexRequest("jd_goods")
                    .source(JSON.toJSONString(contents.get(i)), XContentType.JSON));
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        return !bulk.hasFailures();
    }

    //获取这些数据是实现搜索功能
    public List<Map<String,Object>> searchPage(String keywords,int pageNo,int pageSize) throws IOException {
        if (pageNo<=1){
            pageNo = 1;
        }
        //条件查询
        SearchRequest searchRequest = new SearchRequest("jd_goods");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        //分页
        searchSourceBuilder.from(pageNo);
        searchSourceBuilder.size(pageSize);

        //精准查询
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title", keywords);
        searchSourceBuilder.query(termQueryBuilder);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        //执行搜索
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        //解析结果
        ArrayList<Map<String,Object>> list = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            list.add(hit.getSourceAsMap());
        }
        return list;
    }

    //高亮显示
    public List<Map<String,Object>> searchHight(String keywords,int pageNo,int pageSize) throws IOException {
        if (pageNo<=1){
            pageNo = 1;
        }
        //条件查询
        SearchRequest searchRequest = new SearchRequest("jd_goods");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        //分页
        searchSourceBuilder.from(pageNo);
        searchSourceBuilder.size(pageSize);

        //精准查询
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title", keywords);
        searchSourceBuilder.query(termQueryBuilder);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        //高亮显示
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        //高亮显示那些内容
        highlightBuilder.field("title");
        //取消高亮显示多个
        highlightBuilder.requireFieldMatch(false);
        //样式前缀
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);

        //执行搜索
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        //解析结果
        ArrayList<Map<String,Object>> list = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField title = highlightFields.get("title");

            //原来的结果
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            //解析高亮的字段，将原来的字段换为高亮即可！
            if (title!=null){
                Text[] fragments = title.getFragments();
                String n_title = "";
                for (Text fragment : fragments) {
                    n_title += fragment;
                }
                //高亮字段替换掉原来的字段即可
                sourceAsMap.put("title",n_title);

            }
            list.add(sourceAsMap);
        }
        return list;
    }
}
