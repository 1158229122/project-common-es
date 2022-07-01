package com.aebiz.es.common.exector.factory;

import com.aebiz.es.common.exector.EsQueryProxyHandler;
import com.aebiz.es.modle.enums.EsSelectType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author jim
 * @date 2022/6/29 17:33
 */
@Component
@AllArgsConstructor
public class EsQueryProxyHandlerFactory {

    private final List<EsQueryProxyHandler> list;

    public EsQueryProxyHandler match(EsSelectType type){
        for (EsQueryProxyHandler handler : list) {
            if (handler.matching( type)){
                return handler;
            }
        }
        throw new RuntimeException("未找到对应的es查询处理器");

    }
}
