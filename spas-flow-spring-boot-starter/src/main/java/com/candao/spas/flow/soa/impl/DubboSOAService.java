package com.candao.spas.flow.soa.impl;

import com.candao.spas.flow.core.model.vo.TransferEventModel;
import com.candao.spas.flow.jackson.EasyJsonUtils;
import com.candao.spas.flow.soa.ISOAService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.rpc.service.GenericService;
import org.springframework.stereotype.Service;
import java.util.Map;


/**
 * 实现基于DUBBO协议方式的远程调用
 *
 * */
@Slf4j
@Service("dubboSOA")
public class DubboSOAService implements ISOAService {

    private static ReferenceConfig<GenericService> reference;
    private static final String GENERIC = "true";
    static {
        reference = new ReferenceConfig();
    }

    @Override
    public Object handle(TransferEventModel transfer, Object o) {

        reference.setInterface(transfer.getUrl());
        reference.setTimeout(transfer.getTimeout() * 1000);
        reference.setGeneric(GENERIC);
        GenericService genericService = reference.get();

        // 构造入参类型
        String[] param = transfer.getInputParamTypesValues().toArray(new String[transfer.getInputParamTypesValues().size()]);
        Object[] objects = transfer.getInputParamTypes().toArray(new Object[transfer.getInputParamTypesValues().size()]);

        Object returnObject = genericService.$invoke(transfer.getMethodName(), param,objects);

        log.info("通用化Service,Dubbo-SOA调用返回内容:"+ EasyJsonUtils.toJsonString(returnObject));

        // 断言是否走http 标准返回数据结构
        if (transfer.getOutput() != null){
            Map<String,Object> responseData = EasyJsonUtils.toJavaObject(returnObject, Map.class);
            return getObject(responseData,transfer);
        }
        return returnObject;
    }
}
