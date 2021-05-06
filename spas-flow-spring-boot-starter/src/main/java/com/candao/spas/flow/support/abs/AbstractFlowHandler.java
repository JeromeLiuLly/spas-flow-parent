package com.candao.spas.flow.support.abs;

import com.candao.spas.flow.core.exception.FlowException;
import com.candao.spas.flow.core.model.resp.ResponseFlowDataVo;
import com.candao.spas.flow.core.model.vo.FlowDefintion;
import com.candao.spas.flow.sdk.parseing.FlowParser;
import com.candao.spas.flow.sdk.parses.IFlowHandler;

import java.io.IOException;

public abstract class AbstractFlowHandler implements IFlowHandler {

    @Override
    public ResponseFlowDataVo execute(String flowId, Object input) {
        FlowParser flowParser = new FlowParser(flowDefintion(flowId));
        ResponseFlowDataVo execute;
        try{
            execute = flowParser.execute(input);
        }catch (IOException e){
            throw new FlowException("Flow execution exception");
        }
        return execute;
    }

    public abstract FlowDefintion flowDefintion(String flowId);
}