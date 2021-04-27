package com.candao.spas.flow.sdk.parseing;

import com.candao.spas.flow.core.model.resp.ResponseFlowDataVo;
import com.candao.spas.flow.core.model.vo.FlowDefintion;
import com.candao.spas.flow.core.model.vo.Node;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FlowParser {

    /**
     * 工作流信息
     *
     * */
    private FlowDefintion defintition;

    /**
     * 执行流程
     * @param baseInput 入参
     *
     * @return
     *
     * @throws IOException
     */
    public ResponseFlowDataVo execute(Object baseInput) throws IOException {
        ResponseFlowDataVo responseDataVo = new ResponseFlowDataVo();

        // 工作流开始节点
        String startNode = defintition.getStartNodeId();

        // 开始节点信息
        Node node = defintition.getNodeMap().get(startNode);

        log.info("工作流ID:" + defintition.getFlowId()+",工作流名称:"+defintition.getFlowName()+",开始节点:"+defintition.getStartNodeId());
        FlowParserHandler flowParserHandler = new FlowParserHandler();
        flowParserHandler.execNode(node,baseInput,defintition.getNodeMap(),responseDataVo);

        return responseDataVo;
    }
}