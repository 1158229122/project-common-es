package com.aebiz.es.config;

import com.aebiz.es.modle.constant.CommonConstant;
import joptsimple.internal.Strings;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author jim
 * @date 2022/6/28 14:15
 */
@ConfigurationProperties(prefix = "elasticsearch")
@Configuration
@Data
public class ElasticsearchProperties {
    /**
     * elasticsearch host 多个节点逗号分割 127.0.0.1:9200,127.0.0.1:9201
     */
    private String hosts = CommonConstant.DEFAULT_ES_HOST;
    /**
     * elasticsearch 认证用户名
     */
    private String username = Strings.EMPTY;

    /**
     * elasticsearch 认证密码
     */
    private String password = Strings.EMPTY;

}
