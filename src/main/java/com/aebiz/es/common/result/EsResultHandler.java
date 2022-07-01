package com.aebiz.es.common.result;

import com.aebiz.es.common.exector.MatchingQuery;
import org.apache.http.HttpEntity;

import java.lang.reflect.Method;

/**
 * @author jim
 * @date 2022/6/29 18:29
 */
public interface EsResultHandler extends MatchingQuery {

    Object resolver(String originRequest,final HttpEntity entity, Method method);
}
