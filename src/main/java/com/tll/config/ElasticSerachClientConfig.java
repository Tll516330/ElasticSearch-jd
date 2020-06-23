package com.tll.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author tll
 * @create 2020/6/23 9:23
 * 配置  交给spring容器管理
 */
@Configuration
public class ElasticSerachClientConfig {

    //连接本地es
    @Bean
    public RestHighLevelClient restHighLevelClient(){
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost",9200,"http")));
        return restHighLevelClient;
    }
}
