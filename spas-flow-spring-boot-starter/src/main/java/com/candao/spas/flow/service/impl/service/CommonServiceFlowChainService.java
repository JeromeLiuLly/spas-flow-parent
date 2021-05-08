package com.candao.spas.flow.service.impl.service;

import com.candao.spas.flow.core.model.enums.EventParserEnum;
import com.candao.spas.flow.core.model.req.RequestFlowDataVo;
import com.candao.spas.flow.core.model.resp.ResponseFlowDataVo;
import com.candao.spas.flow.core.model.resp.ResponseFlowStatus;
import com.candao.spas.flow.core.model.vo.Node;
import com.candao.spas.flow.core.model.vo.TransferEventModel;
import com.candao.spas.flow.jackson.EasyJsonUtils;
import com.candao.spas.flow.sdk.service.IService;
import com.candao.spas.flow.soa.ISOAService;
import com.candao.spas.flow.soa.factory.SOAForStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 通用化Service,远程方法调用
 *
 * 功能点：1. 业务逻辑处理
 *
 * */
@Slf4j
@Service
public class CommonServiceFlowChainService implements IService {

    @Autowired
    private SOAForStrategy soaForStrategy;

    @Override
    public void handle(RequestFlowDataVo input, ResponseFlowDataVo output) throws Exception {
        try{

            Object object = inputData(input,output);
            String sourceJsonData = EasyJsonUtils.toJsonString(object);
            log.info("通用化Service,入参内容:" + sourceJsonData);

            Node beanNode = input.getNode();
            TransferEventModel model = beanNode.getTransfer();

            EventParserEnum eventParserEnum = EventParserEnum.getEventInfo(model.getEventType());
            ISOAService service = soaForStrategy.getSOAServiceInstance(eventParserEnum);
            Object returnObj = service.handle(model,object);

            if (returnObj != null){
                output.setData(returnObj);
            }
            log.info("通用化Service,出参内容:" + EasyJsonUtils.toJsonString(output.getData()));
            output.setResponseStatus(ResponseFlowStatus.SUCCESS);
        }catch (Exception e){
            e.printStackTrace();
            output.setResponseStatus(ResponseFlowStatus.FAIL);
            output.setMsg(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }
}
