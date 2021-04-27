package com.candao.spas.flow.sdk.parses;

import com.candao.spas.flow.core.model.enums.MethodParserEnum;
import com.candao.spas.flow.core.model.req.RequestFlowDataVo;
import com.candao.spas.flow.core.model.resp.ResponseFlowDataVo;
import com.candao.spas.flow.core.model.vo.Node;
import com.candao.spas.flow.sdk.service.IService;
import com.candao.spas.flow.core.utils.SpringContextUtil;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Map;

public interface NodeParser<T,R> {

    /**
     * 初始化入参
     *
     * @param input
     *
     * @return
     */
    default RequestFlowDataVo initInput(Node node, T input){
        RequestFlowDataVo baseInputTarget = new RequestFlowDataVo();
        baseInputTarget.setData(input);
        baseInputTarget.setNode(node);
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
    default void parserNode(Node node, T input, ResponseFlowDataVo<R> output, MethodParserEnum methodParserEnum){
        parser(node, input, output,methodParserEnum);
    };

    /**
     * 反射调用目标方法
     *
     * @param node 节点信息
     * @parma input  入参
     * @param output 出参
     * @param methodParserEnum 执行方法类型
     * */
    default void invokeMethod(Node node, T input, ResponseFlowDataVo<R> output, MethodParserEnum methodParserEnum){
        RequestFlowDataVo request = initInput(node, input);
        String component = node.getComponent();
        IService service  = (IService) SpringContextUtil.getBean(component);

        // 选择目标服务实例对象
        Method method = ReflectionUtils.findMethod(service.getClass(), methodParserEnum.getValue(), request.getClass(), output.getClass());

        // 选择目标服务实例的服务的方法
        ReflectionUtils.invokeMethod(method, service, request, output);
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
    void parser(Node node, T input, ResponseFlowDataVo<R> output, MethodParserEnum methodParserEnum);


    /**
     * 设置工作流节点链路
     *
     * @param nodeMap  执行节点集合
     * */
    void setNodeMap(Map<String, Node> nodeMap);
}