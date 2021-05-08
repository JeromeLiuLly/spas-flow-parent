package com.candao.spas.flow.service.impl.method;

import com.candao.spas.flow.core.constants.ChainConstants;
import com.candao.spas.flow.core.model.req.RequestFlowDataVo;
import com.candao.spas.flow.core.model.resp.ResponseFlowDataVo;
import com.candao.spas.flow.core.model.resp.ResponseFlowStatus;
import com.candao.spas.flow.core.model.vo.Node;
import com.candao.spas.flow.core.model.vo.TransferEventModel;
import com.candao.spas.flow.core.utils.ClassUtil;
import com.candao.spas.flow.core.utils.EasyJsonUtils;
import com.candao.spas.flow.core.utils.SpringContextUtil;
import com.candao.spas.flow.sdk.service.IService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 通用化Method,本地方法调用
 *
 * 功能点：1. 业务逻辑处理
 *        2. 个性化/复杂度高 对象转换
 *
 * */
@Slf4j
@Service
public class CommonMethodFlowChainService implements IService {

    @Override
    public void handle(RequestFlowDataVo input, ResponseFlowDataVo output) throws Exception {
        try {

            Object o = inputData(input,output);
            String sourceJsonData = EasyJsonUtils.toJsonString(o);
            log.info("通用化Method,入参内容:" + sourceJsonData);

            Node beanNode = input.getNode();
            TransferEventModel model = beanNode.getTransfer();

            Object returnObj;

            // 反射入参
            Object[] inputObj=null;
            if (model.getInputParamTypes() != null){
                inputObj=model.getInputParamTypes().toArray(new Object[model.getInputParamTypes().size()]);
            }

            log.info("通用化Method,入参参数类型:" + EasyJsonUtils.toJsonString(model.getInputParamTypesValues()));
            log.info("通用化Method,入参参数数据:" + EasyJsonUtils.toJsonString(model.getInputParamTypes()));
            // 断言本地调用类型,是否Spring托管
            if (model.getUrl().indexOf(ChainConstants.POINT) > 0){
                returnObj = ClassUtil.methodInvoke(model.getUrl(),model.getMethodName(),inputObj);
            }else{
                Object service  = SpringContextUtil.getBean(model.getUrl());
                returnObj = ClassUtil.methodInvoke(service,model.getMethodName(),inputObj);
            }

            // 判断方法返回参数,覆盖旧output
            if (returnObj != null){
                output.setData(returnObj);
            }

            log.info("通用化Method,出参内容:" + EasyJsonUtils.toJsonString(output.getData()));
            output.setResponseStatus(ResponseFlowStatus.SUCCESS);
        }catch (Exception e){
            e.printStackTrace();
            output.setResponseStatus(ResponseFlowStatus.FAIL);
            output.setMsg(e.getMessage());
            throw new Exception(e.getMessage(),e);
        }
    }
}
