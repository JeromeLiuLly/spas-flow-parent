package com.candao.spas.flow.support.impl;

import com.candao.spas.flow.core.model.vo.FlowDefintion;
import com.candao.spas.flow.sdk.context.FlowContext;
import com.candao.spas.flow.support.abs.AbstractFlowHandler;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

@Component
public class FlowHandler extends AbstractFlowHandler {

    @Resource
    private FlowContext flowContext;

    @Override
    public FlowDefintion flowDefintion(String flowId) {
        return flowContext.getFlowDefinition(flowId);
    }


}