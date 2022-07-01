package com.aebiz.es.common.param.impl;

import com.aebiz.es.common.param.ParamHandler;
import com.aebiz.es.common.param.ParamResolver;
import com.aebiz.es.common.util.ReflectionUtils;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrLookup;
import org.apache.commons.lang3.text.StrMatcher;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.springframework.stereotype.Service;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author jim
 * @date 2022/6/30 11:38
 */
@Service
@AllArgsConstructor
public class ParamHandlerDefaultImpl implements ParamHandler {
    private final ParamResolver paramResolver;
    /**
     * 字符串处理替换
     *
     * @param originStr
     * @param method
     * @param args
     * @return
     */
    @Override
    public String handle(String originStr, Method method, Object[] args) {
        if (Objects.isNull(args)||args.length==0) return originStr;
        Map<String, Object> resolver = paramResolver.resolver(method, args);
        StrSubstitutor strSubstitutor = new StrSubstitutor(new MapStrLookup(resolver));
        originStr = strSubstitutor.replace(originStr);
        strSubstitutor = new StrSubstitutor(new SpecialMapStrLookup(resolver),"#{", "}", '#');
        originStr = strSubstitutor.replace(originStr);
        return originStr;
    }

    static class MapStrLookup extends StrLookup {
        private final Map<String,Object> map;
        MapStrLookup(Map map) {
            this.map = map;
        }

        @Override
        public String lookup(String key) {
            if (map == null) {
                return null;
            }
            if (!map.containsKey(key)){
                //也许是开发想写表达式
                ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
                ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("js");
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    scriptEngine.put(entry.getKey(),entry.getValue());
                }

                try {
                    Object oj = scriptEngine.eval(key);
                    if (oj instanceof Number){
                        return new BigDecimal(oj.toString()).stripTrailingZeros().toPlainString();
                    }
                    return oj.toString();
                } catch (ScriptException e) {
                    throw new RuntimeException("字段-> "+key+" 未找到其属性,是否配置了EsParam注解。请检查配置是否正确或表达式有误");
                }
            }
            Object obj = map.get(key);
            if (obj == null) {
                return "null";
            }
            return obj.toString();
        }
    }

    public static String convertByPattern(String value) {

        String regex = "^(-?\\d*)(\\.?0*)$";
        Matcher matcher = Pattern.compile(regex).matcher(value);
        if (matcher.find()) {
            return matcher.group(1);
        }
        String regex1 = "^(-?\\d*\\.\\d*[1-9])(0*)$";
        Matcher matcher1 = Pattern.compile(regex1).matcher(value);
        if (matcher1.find()) {
            return matcher1.group(1);
        }
        return null;
    }



    static class SpecialMapStrLookup extends StrLookup {
        private final Map map;
        SpecialMapStrLookup(Map map) {
            this.map = map;
        }

        @Override
        public String lookup(String key) {
            if (map == null) {
                return null;
            }
            if (!map.containsKey(key)){
                throw new RuntimeException("字段-> "+key+" 未找到其属性,是否配置了EsParam注解。请检查配置是否正确");
            }
            Object obj = map.get(key);
            return ReflectionUtils.getParameterValue(obj);
        }

    }
}
