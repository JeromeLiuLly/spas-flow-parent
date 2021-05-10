package com.candao.spas.flow.sdk.parses;

import com.candao.spas.flow.core.model.db.TransferEventVo;
import com.candao.spas.flow.core.model.enums.MethodParserEnum;
import com.candao.spas.flow.core.model.enums.NodeParserEnum;
import com.candao.spas.flow.core.model.req.RequestFlowDataVo;
import com.candao.spas.flow.core.model.resp.ResponseFlowDataVo;
import com.candao.spas.flow.core.model.vo.Node;
import com.candao.spas.flow.core.model.vo.TransferEventModel;
import com.candao.spas.flow.redis.handler.DataUtil;
import com.candao.spas.flow.sdk.mapper.TransferConfigMapper;
import com.candao.spas.flow.sdk.service.IService;
import com.candao.spas.flow.sdk.utils.ClassUtil;
import com.candao.spas.flow.sdk.utils.SpringContextUtil;
import org.springframework.beans.BeanUtils;
import java.util.Map;

public interface NodeParser<T,R> {

    /**
     * 初始化入参
     *
     * @param input
     *
     * @return
     */
    default RequestFlowDataVo initInput(String flowId, Node node, T input){
        RequestFlowDataVo baseInputTarget = new RequestFlowDataVo();
        baseInputTarget.setData(input);

        // 根据flowId,nodeId ,加载事件类型模型对象,排除 Begin、End、Subflow、Condition 节点
        if (!NodeParserEnum.returnCollectionList().contains(node.getNodeType())) {
            String redisKey = "flow:"+flowId+":"+node.getNodeId();
            // 优先走Redis,再走DB
            TransferEventVo transferEventVo = DataUtil.getDataFromRedisOrDataGeter(redisKey,TransferEventVo.class,()->{
                TransferConfigMapper transferConfigMapper = (TransferConfigMapper) SpringContextUtil.getBean("transferConfigMapper");
                TransferEventVo transfer = transferConfigMapper.getTransferById(flowId, node.getNodeId());
                return transfer;
            });

            TransferEventModel transferEventModel = new TransferEventModel();
            BeanUtils.copyProperties(transferEventVo, transferEventModel);

            node.setTransfer(transferEventModel);
        }
        baseInputTarget.setNode(node);
        baseInputTarget.setFlowId(flowId);
        return baseInputTarget;
    }

    /**
     * 解析节点信息
     *
     * @param node 节点信息
     * @parma input  入参
     * @param output 出参
     * @param methodParserEnum 执行方法类型
     *
     * @return
     */
    default void parserNode(String flowId,Node node, T input, ResponseFlowDataVo<R> output, MethodParserEnum methodParserEnum){
        parser(flowId,node, input, output, methodParserEnum);
    };

    /**
     * 反射调用目标方法
     *
     * @param node 节点信息
     * @parma input  入参
     * @param output 出参
     * @param methodParserEnum 执行方法类型
     * */
    default void invokeMethod(String flowId,Node node, T input, ResponseFlowDataVo<R> output, MethodParserEnum methodParserEnum){
        RequestFlowDataVo request = initInput(flowId, node, input);
        String component = node.getComponent();
        IService service = (IService) SpringContextUtil.getBean(component);
        ClassUtil.methodInvoke(service, methodParserEnum.getValue(), request, output);
    }


    /**
     * 执行接口
     *
     * @param node   执行节点
     * @parma input  入参
     * @param output 出参
     * @param methodParserEnum 执行方法类型
     *
     * @return
     * */
    void parser(String flowId, Node node, T input, ResponseFlowDataVo<R> output, MethodParserEnum methodParserEnum);


    /**
     * 设置工作流节点链路
     *
     * @param nodeMap  执行节点集合
     * */
    void setNodeMap(Map<String, Node> nodeMap);
}