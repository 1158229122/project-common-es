package com.aebiz.es.common.param.impl;

import com.aebiz.es.common.param.ParamResolver;
import com.aebiz.es.common.util.ReflectionUtils;
import com.aebiz.es.modle.annotation.EsParam;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 参数解析
 * @author jim
 * @date 2022/6/30 11:27
 */
@Component
@AllArgsConstructor
public class ParamResolverDefaultImpl implements ParamResolver {
    /**
     * 参数解析
     *
     * @param args
     * @return
     */
    @Override
    public Map<String, Object> resolver(Method method, Object[] args) {
        if (Objects.isNull(args)||args.length==0){
            return null;
        }
        Map map = new HashMap();
        for (int i = 0; i < args.length; i++) {
            EsParam methodParam = ReflectionUtils.findMethodParam(method, i);
            if (methodParam==null){
                continue;
            }
            if (Objects.isNull(methodParam.value())){
                throw new RuntimeException("未指定 EsParam 的值");
            }
            if (ReflectionUtils.isBaseTypeAndExtend(args[i].getClass())){
                map.put(methodParam.value(),args[i]);
            } else {
                //自定义对象
                Map<String, Object> nestedFieldsMap = ReflectionUtils.getNestedFieldsMap(methodParam.value(), args[i]);
                map.putAll(nestedFieldsMap);
            }
        }
        return map;
    }
}
