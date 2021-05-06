package com.candao.spas.flow.service.impl;

import com.candao.spas.flow.core.model.enums.NodeParserEnum;
import com.candao.spas.flow.core.model.req.RequestFlowDataVo;
import com.candao.spas.flow.core.model.resp.ResponseFlowDataVo;
import com.candao.spas.flow.core.model.resp.ResponseFlowStatus;
import com.candao.spas.flow.core.utils.EasyJsonUtils;
import com.candao.spas.flow.sdk.service.IService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NodeChainImpl implements IService {


    @Override
    public void handle(RequestFlowDataVo input, ResponseFlowDataVo output) throws Exception {
        log.info("工作流id:" + input.getFlowId() +",节点名称"+ input.getNode().getNodeId());
        if (NodeParserEnum.ROOT.getValue().equals(input.getNode().getFront())){
            log.info("handle-input-" + EasyJsonUtils.toJsonString(input.getData()));
            output.setData(input.getData());
        }else{
            log.info("handle-out-" + EasyJsonUtils.toJsonString(output.getData()));
            output.setData("测试下");
        }
        output.setStatus(ResponseFlowStatus.SUCCESS.getStatus());
    }
}
