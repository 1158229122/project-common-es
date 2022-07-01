package com.aebiz.es.common.exector.impl;

import com.aebiz.es.common.exector.EsQueryProxyHandler;
import com.aebiz.es.common.exector.factory.EsQueryProxyHandlerFactory;
import com.aebiz.es.common.exector.factory.EsResultHandlerFactory;
import com.aebiz.es.common.param.ParamHandler;
import com.aebiz.es.modle.domain.RequestMeta;
import com.aebiz.es.modle.enums.EsSelectType;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import javafx.beans.binding.LongExpression;
import lombok.AllArgsConstructor;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 简单的sql查询
 * @author jim
 * @date 2022/6/29 17:37
 */
@Component
@AllArgsConstructor
public class EsSqlSimpHandlerImpl implements EsQueryProxyHandler {
    private final EsResultHandlerFactory resultHandlerFactory;
    private final RestHighLevelClient restHighLevelClient;
    private final ParamHandler paramHandler;
    private final EsDslHandlerImpl esDslHandler;
    /**
     * 处理
     *
     * @param requestMeta
     * @param method
     * @param args
     * @return
     */
    @Override
    public Object handle(RequestMeta requestMeta, Method method, Object[] args) {
        try {
            String sql = paramHandler.handle(requestMeta.getRequestJson(), method, args);
            CCJSqlParserManager parserManager = new CCJSqlParserManager();
            Select select = (Select) parserManager.parse(new StringReader(sql));
            PlainSelect plain = (PlainSelect) select.getSelectBody();
            Limit limit = plain.getLimit();
            //es 大坑 limit不支持分页。有分页的还是走dsl语句
            Request request ;
            if (limit ==null){
                request = new Request("GET","/_sql?format=json");
            }else {
                request = new Request("GET","/_sql/translate");
                //remove Limit
                plain.setLimit(null);
                sql = plain.toString();
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("query",sql);
            request.setJsonEntity(jsonObject.toJSONString());
            RestClient client = restHighLevelClient.getLowLevelClient();
            Response response = client.performRequest(request);
            if (limit == null){
                return resultHandlerFactory.match(requestMeta.getType()).resolver(sql,response.getEntity(), method);
            }else {
                LongValue offset = limit.getOffset(LongValue.class);
                LongValue size = limit.getRowCount(LongValue.class);
                requestMeta.setType(EsSelectType.DSL);
                requestMeta.setRequestJson(this.convertDsl(response.getEntity(),offset.getValue(),size.getValue()));
                return esDslHandler.handle(requestMeta,method,args);
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    private String convertDsl(HttpEntity entity,Long offset,Long size) throws IOException {
        if (entity == null) {
            throw new IllegalStateException("Response body expected but not returned");
        }
        if (entity.getContentType() == null) {
            throw new IllegalStateException("Elasticsearch didn't return the [Content-Type] header, unable to parse response body");
        }
        String dsl = EntityUtils.toString(entity);
        JSONObject jsonObject = JSON.parseObject(dsl);
        jsonObject.put("from",offset);
        jsonObject.put("size",size);
        return jsonObject.toJSONString();
    }

    /**
     * 匹配查询
     *
     * @param type
     * @return
     */
    @Override
    public boolean matching(EsSelectType type) {
        return Objects.equals(EsSelectType.SQL,type);
    }
}
