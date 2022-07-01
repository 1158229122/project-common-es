package com.aebiz.es.common.exector.factory;

import com.aebiz.es.common.exector.EsProxyHandler;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author jim
 * @date 2022/6/28 17:49
 */
@Component
@AllArgsConstructor
public class EsProxyExecuteFactory {
    private final List<EsProxyHandler> executeHandle;

    public EsProxyHandler match(Class clazz){
        for (EsProxyHandler handler : executeHandle) {
            if (handler.matching( clazz)){
                return handler;
            }
        }
        throw new RuntimeException("未找到对应的es处理器");

    }
}
