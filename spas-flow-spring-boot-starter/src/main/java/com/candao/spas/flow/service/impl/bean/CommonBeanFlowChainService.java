package com.candao.spas.flow.service.impl.bean;

import com.candao.spas.convert.sdk.utils.JsonCovertUtils;
import com.candao.spas.flow.core.model.req.RequestFlowDataVo;
import com.candao.spas.flow.core.model.resp.ResponseFlowDataVo;
import com.candao.spas.flow.core.model.resp.ResponseFlowStatus;
import com.candao.spas.flow.core.model.vo.Node;
import com.candao.spas.flow.core.model.vo.TransferEventModel;
import com.candao.spas.flow.core.utils.EasyJsonUtils;
import com.candao.spas.flow.sdk.service.IService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 通用化Bean对象装填
 *
 * 功能点：1. 更新值域
 *        2. 转换对象
 *        3. 插入新属性
 * */
@Slf4j
@Service
public class CommonBeanFlowChainService implements IService {

    @Override
    public void handle(RequestFlowDataVo input, ResponseFlowDataVo output) throws Exception {

        String sourceJsonData = EasyJsonUtils.toJsonString(inputData(input,output));

        try {
            log.info("通用化Bean,入参内容:" + sourceJsonData);

            Node beanNode = input.getNode();
            TransferEventModel model = beanNode.getTransfer();

            log.info("通用化Bean,转换协议:" + model.getConvertRule());
            String outputJson = JsonCovertUtils.convert(sourceJsonData, model.getConvertRule());
            if (outputJson == null || StringUtils.isEmpty(output)){
                throw new Exception("执行节点:"+beanNode.getNodeId()+",事件类型:"+beanNode.getNodeType()+",转换失败");
            }

            log.info("通用化Bean,出参内容:" + outputJson);

            Object o = EasyJsonUtils.toJavaObject(outputJson,Object.class);
            output.setData(o);

            output.setResponseStatus(ResponseFlowStatus.SUCCESS);
        }catch (Exception e){
            e.printStackTrace();
            output.setResponseStatus(ResponseFlowStatus.FAIL);
            output.setMsg(e.getMessage());
            throw new Exception(e.getMessage(),e);
        }
    }
}
