package com.aebiz.es.config;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author jim
 * @date 2022/6/28 14:24
 */
@Configuration
@AllArgsConstructor
public class ElasticsearchConfig {

    private final ElasticsearchProperties elasticsearchProperties;

    public static HttpHost[] hostGen(String[] elasticsearchHost) {
        if (ArrayUtils.isEmpty(elasticsearchHost)) {
            throw new IllegalArgumentException("elasticsearch host must not empty");
        }
        return Arrays.stream(elasticsearchHost).map(
                HttpHost::create
        ).toArray(HttpHost[]::new);
    }

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        String hosts = elasticsearchProperties.getHosts();
        RestClientBuilder builder;
        configCheck();
        if (StringUtils.isAnyEmpty(elasticsearchProperties.getUsername(), elasticsearchProperties.getPassword())) {
            /** ps: 创建非认证客户端*/
            builder = RestClient.builder(hostGen(hosts.split(",")));
        } else {
            /** ps: 创建认证客户端*/
            CredentialsProvider credentials = credentials();
            builder = RestClient.builder(hostGen(hosts.split(","))).setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder.setDefaultCredentialsProvider(credentials));
        }
        return new RestHighLevelClient(builder);
    }

    @Bean
    public RestClient getRestClient() {
        String hosts = elasticsearchProperties.getHosts();
        configCheck();
        if (StringUtils.isAnyEmpty(elasticsearchProperties.getUsername(), elasticsearchProperties.getPassword())) {
            /** ps: 创建非认证客户端*/
            return RestClient.builder(hostGen(hosts.split(","))).build();
        } else {
            /** ps: 创建认证客户端*/
            CredentialsProvider credentials = credentials();
            return RestClient.builder(hostGen(hosts.split(","))).setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder.setDefaultCredentialsProvider(credentials)).build();
        }
    }

    /**
     * 构建客户端认证信息
     *
     * @return
     */
    private CredentialsProvider credentials() {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(elasticsearchProperties.getUsername(), elasticsearchProperties.getPassword()));
        return credentialsProvider;
    }

    /**
     * 检查配置项
     */
    private void configCheck() {
        if (Objects.isNull(elasticsearchProperties)) {
            throw new IllegalArgumentException("elasticSearch config hosts is null");
        }

        if (StringUtils.isEmpty(elasticsearchProperties.getHosts())) {
            throw new IllegalArgumentException("elasticSearch config hosts is empty");
        }
    }


}
