package com.candao.spas.flow.sdk.parses.impl.begin;


import com.candao.spas.flow.core.model.enums.MethodParserEnum;
import com.candao.spas.flow.core.model.req.RequestFlowDataVo;
import com.candao.spas.flow.core.model.resp.ResponseFlowDataVo;
import com.candao.spas.flow.core.model.resp.ResponseFlowStatus;
import com.candao.spas.flow.core.model.vo.Node;
import com.candao.spas.flow.sdk.parses.abs.AbstractNodeComponent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BeginNodeComponent<T,R> extends AbstractNodeComponent<T,R> {

    @Override
    public void parser(String flowId, Node node, T input, ResponseFlowDataVo<R> output, MethodParserEnum methodParserEnum) {
        log.info("工作流Id: "+flowId+",节点名称: "+node.getNodeId()+",进入开始节点");
        RequestFlowDataVo<T> requestDataVo = initInput(flowId,node,input);
        output.setStatus(ResponseFlowStatus.SUCCESS.getStatus());
        output.setData((R) requestDataVo.getData());
        output.setInput((R) requestDataVo.getData());
    }
}