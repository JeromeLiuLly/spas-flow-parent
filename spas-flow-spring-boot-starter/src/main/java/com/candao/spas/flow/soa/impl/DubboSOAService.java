package com.candao.spas.flow.soa.impl;

import com.candao.spas.flow.core.model.enums.EventParserEnum;
import com.candao.spas.flow.core.model.vo.TransferEventModel;
import com.candao.spas.flow.core.utils.EasyJsonUtils;
import com.candao.spas.flow.soa.ISOAService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.rpc.service.GenericService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 实现基于DUBBO协议方式的远程调用
 *
 * */
@Slf4j
@Service("dubboSOA")
public class DubboSOAService implements ISOAService {

    private static ReferenceConfig<GenericService> reference;
    static {
        reference = new ReferenceConfig();
    }

    @Override
    public Object handle(TransferEventModel transfer, Object... o) {

        if (transfer == null){
            transfer = mockModel();
        }

        reference.setInterface(transfer.getUrl());
        reference.setTimeout(transfer.getTimeout());
        reference.setGeneric("true");
        GenericService genericService = reference.get();

        // 构造入参类型
        String[] param = (String[]) transfer.getInputParamTypes().toArray(new String[transfer.getInputParamTypes().size()]);
        Object returnObject = genericService.$invoke(transfer.getMethodName(), param,o);

        log.info("通用化Service,Dubbo-SOA调用返回内容:"+EasyJsonUtils.toJsonString(returnObject));

        // 断言是否走http 标准返回数据结构
        if (transfer.getOutput() != null){
            Map<String,Object> responseData = EasyJsonUtils.toJavaObject(returnObject, Map.class);
            return getObject(responseData,transfer);
        }

        return returnObject;
    }

    private TransferEventModel mockModel(){
        TransferEventModel model = new TransferEventModel();

        model.setMethodName("getAccountByToken");
        model.setUrl("com.candao.auth.dubbo.api.AccountProvider");
        model.setEventType(EventParserEnum.DUBBO.getValue());
        model.setTimeout(30000);

        List<String> paramList = new ArrayList<>();
        paramList.add("java.lang.String");
        model.setInputParamTypes(paramList);

        return model;
    }
}
