package com.candao.spas.flow.service.impl;

import com.candao.spas.flow.core.model.req.RequestFlowDataVo;
import com.candao.spas.flow.core.model.resp.ResponseFlowDataVo;
import com.candao.spas.flow.core.model.resp.ResponseFlowStatus;
import com.candao.spas.flow.sdk.service.IService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NodeChainImpl implements IService {


    @Autowired
    private DemoService demoService;

    @Override
    public void handle(RequestFlowDataVo input, ResponseFlowDataVo output) throws Exception {
        log.info("handle-" + input.getData());
        demoService.sayHello();
        output.setStatus(ResponseFlowStatus.SUCCESS.getStatus());
        output.setData(input.getData());
    }
}
