package com.candao.spas.flow.sample.flow.service;

import com.candao.spas.flow.core.model.db.TransferEventVo;
import com.candao.spas.flow.core.model.enums.NodeParserEnum;
import com.candao.spas.flow.core.model.resp.ResponseFlowDataVo;
import com.candao.spas.flow.core.model.vo.FlowDefintion;
import com.candao.spas.flow.core.model.vo.Node;
import com.candao.spas.flow.jackson.EasyJsonUtils;
import com.candao.spas.flow.sample.flow.bean.InitClass;
import com.candao.spas.flow.sample.flow.bean.Project;
import com.candao.spas.flow.sdk.mapper.TransferConfigMapper;
import com.candao.spas.flow.sdk.parseing.FlowParser;
import com.candao.spas.flow.support.factory.FlowDefintitionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DemoFlowService {

    @Resource
    private FlowDefintitionFactory flowDefintitionFactory;

    @Resource
    private TransferConfigMapper transferConfigMapper;

    public ResponseFlowDataVo test(){
        FlowDefintion defintition = flowDefintitionFactory.getFlowDefintion("chain");

        log.info(EasyJsonUtils.toJsonString(defintition));

        test("flow");

        FlowParser flowParser = new FlowParser(defintition);

        InitClass init = new InitClass();
        Project project = init.initProject();
        ResponseFlowDataVo responseFlowDataVo = null;
        try {
            responseFlowDataVo = flowParser.execute(project);
        }catch (Exception e){
            e.printStackTrace();
        }
        return responseFlowDataVo;
    }

    public ResponseFlowDataVo test(String flowId){

        FlowDefintion defintition = flowDefintitionFactory.getFlowDefintion(flowId);
        /*List<TransferEventVo> transferEventVos =  transferConfigMapper.findTransferById(flowId);

        TransferEventVo root = transferEventVos.stream().filter(transferEventVo -> Objects.equals(transferEventVo.getFront(), NodeParserEnum.ROOT.getValue())).findFirst().get();

        Map<String,Node> param = transferEventVos.stream().map(transferEventVo -> {
            Node node = new Node();
            BeanUtils.copyProperties(transferEventVo,node);
            return node;
        }).collect(Collectors.toMap(w->w.getNodeId(),w->w));



        FlowDefintion defintition = new FlowDefintion();
        defintition.setFlowId(flowId);
        defintition.setDesc("可视化业务编排");
        defintition.setFlowName(flowId);
        defintition.setStartNodeId(root.getNodeId());
        defintition.setNodeMap(param);

        log.info(EasyJsonUtils.toJsonString(defintition));*/

        FlowParser flowParser = new FlowParser(defintition);

        InitClass init = new InitClass();
        Project project = init.initProject();
        ResponseFlowDataVo responseFlowDataVo = null;
        try {
            responseFlowDataVo = flowParser.execute(project);
        }catch (Exception e){
            e.printStackTrace();
        }
        return responseFlowDataVo;
    }

}
