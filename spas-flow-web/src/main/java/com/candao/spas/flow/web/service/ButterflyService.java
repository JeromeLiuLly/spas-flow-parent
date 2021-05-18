package com.candao.spas.flow.web.service;

import com.candao.spas.flow.core.model.db.*;
import com.candao.spas.flow.core.utils.FastDateFormat;
import com.candao.spas.flow.core.utils.FastDateUtils;
import com.candao.spas.flow.core.utils.StringUtil;
import com.candao.spas.flow.jackson.EasyJsonUtils;
import com.candao.spas.flow.sdk.mapper.EdgeConfigMapper;
import com.candao.spas.flow.sdk.mapper.FlowConfigMapper;
import com.candao.spas.flow.sdk.mapper.NodeConfigMapper;
import com.candao.spas.flow.sdk.mapper.TransferConfigMapper;
import com.candao.spas.flow.web.bean.ButterflyEdges;
import com.candao.spas.flow.web.bean.ButterflyFlow;
import com.candao.spas.flow.web.bean.ResponseButterflyFlowVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class ButterflyService {

    @Resource
    private EdgeConfigMapper edgeConfigMapper;

    @Resource
    private NodeConfigMapper nodeConfigMapper;

    @Resource
    private TransferConfigMapper transferConfigMapper;

    @Resource
    private FlowConfigMapper flowConfigMapper;


    public void update(String flowId){
        String version = FastDateUtils.format(new Date(),FastDateFormat.DATE_TIME);
        transferConfigMapper.updateTransferVersionAndStatus(flowId,version,0);

        Example example = new Example(NodeEventVo.class);
        example.createCriteria().andEqualTo("flowId",flowId);

        nodeConfigMapper.deleteByExample(example);
        edgeConfigMapper.deleteByExample(example);
    }

    public void save(ResponseButterflyFlowVo responseButterflyFlowVo){
        List<ButterflyFlow> butterflyFlow = responseButterflyFlowVo.getNodes();
        List<ButterflyEdges> butterflyEdges = responseButterflyFlowVo.getEdges();

        AtomicReference<String> flowId = new AtomicReference<>("");

        List<EdgeEventVo> edgeEventVos = butterflyEdges.stream().map(edge->{
            EdgeEventVo edgeEventVo = new EdgeEventVo();
            BeanUtils.copyProperties(edge,edgeEventVo);
            return  edgeEventVo;
        }).collect(Collectors.toList());

        List<TransferEventVo> transferEventVos = new ArrayList<>();
        List<NodeEventVo> nodeEventVos = butterflyFlow.stream().map(flow ->{
            NodeEventVo nodeEventVo = new NodeEventVo();
            TransferEventVo transferEventVo = new TransferEventVo();

            nodeEventVo.setLeftPoint(flow.getLeft());
            nodeEventVo.setEndpoints(EasyJsonUtils.toJsonString(flow.getEndpoints()));

            flowId.set(flow.getFlowId());
            BeanUtils.copyProperties(flow,nodeEventVo);
            if (!StringUtil.isNullOrBlank(flow.getOutput())) {
                transferEventVo.setOutput(EasyJsonUtils.toJavaObject(flow.getOutput(), OutPutResponseVo.class));
            }
            if (!StringUtil.isNullOrBlank(flow.getInputParamTypesValues())) {
                transferEventVo.setInputParamTypesValues(EasyJsonUtils.toJavaList(flow.getInputParamTypesValues(), String.class));
            }
            BeanUtils.copyProperties(flow,transferEventVo);
            transferEventVos.add(transferEventVo);
            return nodeEventVo;
        }).collect(Collectors.toList());

        update(flowId.get());

        transferConfigMapper.saveTransfers(transferEventVos);
        nodeConfigMapper.insertList(nodeEventVos);
        edgeConfigMapper.insertList(edgeEventVos);
    }

    public ResponseButterflyFlowVo getFlow(String flowId){
        List<TransferEventVo> transferEventVos = transferConfigMapper.findTransferById(flowId);
        List<NodeEventVo> nodeEventVos = nodeConfigMapper.findNodesById(flowId);
        List<EdgeEventVo> edgeEventVos = edgeConfigMapper.findEdgesById(flowId);

        Map<String,NodeEventVo> nodeEventVoMaps = nodeEventVos.stream().collect(Collectors.toMap(w -> w.getNodeId(), w -> w));

        List<ButterflyFlow> butterflyFlows = transferEventVos.stream().map(transferEventVo -> {

            ButterflyFlow butterflyFlow = new ButterflyFlow();
            BeanUtils.copyProperties(transferEventVo,butterflyFlow);
            NodeEventVo nodeEventVo = nodeEventVoMaps.get(transferEventVo.getNodeId());
            BeanUtils.copyProperties(nodeEventVo,butterflyFlow);
            butterflyFlow.setId(transferEventVo.getNodeId());
            butterflyFlow.setEndpoints(EasyJsonUtils.toJavaObject(nodeEventVo.getEndpoints(),Object.class));
            butterflyFlow.setLeft(nodeEventVo.getLeftPoint());
            butterflyFlow.setOutput(EasyJsonUtils.toJsonString(transferEventVo.getOutput()));
            butterflyFlow.setInputParamTypesValues(EasyJsonUtils.toJsonString(transferEventVo.getInputParamTypesValues()));
            return  butterflyFlow;
        }).collect(Collectors.toList());

        List<ButterflyEdges> butterflyEdges = edgeEventVos.stream().map(edgeEventVo -> {
            ButterflyEdges edges = new ButterflyEdges();
            BeanUtils.copyProperties(edgeEventVo,edges);
            return edges;
        }).collect(Collectors.toList());

        ResponseButterflyFlowVo responseButterflyFlowVo = new ResponseButterflyFlowVo();
        responseButterflyFlowVo.setEdges(butterflyEdges);
        responseButterflyFlowVo.setNodes(butterflyFlows);
        return responseButterflyFlowVo;
    }

    public List<FlowEventVo> findFlows(){
        return flowConfigMapper.findFlows();
    }

    public void addFlow(FlowEventVo flowEventVo) {
        flowConfigMapper.insert(flowEventVo);
    }
}
