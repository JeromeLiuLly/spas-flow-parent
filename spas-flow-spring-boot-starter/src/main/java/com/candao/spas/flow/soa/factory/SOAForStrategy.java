package com.candao.spas.flow.soa.factory;

import com.candao.spas.flow.core.exception.FlowException;
import com.candao.spas.flow.core.model.enums.EventParserEnum;
import com.candao.spas.flow.core.utils.EasyJsonUtils;
import com.candao.spas.flow.soa.ISOAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SOA 服务实例策略工厂
 *
 * */
@Component
public class SOAForStrategy {

    @Autowired
    Map<String, ISOAService> soaStrategy = new ConcurrentHashMap<>();

    /**
     * 根据枚举值,获取服务实例
     *
     * @param eventParserEnum 服务枚举
     *
     * @return
     * */
    public ISOAService getSOAServiceInstance(EventParserEnum eventParserEnum){
        ISOAService soaService = soaStrategy.get(eventParserEnum.getValue());

        if (soaService == null){
            throw new FlowException("soaStrategy的服务策略:["+ EasyJsonUtils.toJsonString(eventParserEnum) +"],无法获取服务实例");
        }
        return soaService;
    }
}
