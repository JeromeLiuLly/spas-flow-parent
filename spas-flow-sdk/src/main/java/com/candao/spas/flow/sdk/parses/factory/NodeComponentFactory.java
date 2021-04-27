package com.candao.spas.flow.sdk.parses.factory;

import com.candao.spas.flow.core.model.enums.NodeParserEnum;
import com.candao.spas.flow.sdk.parses.NodeParser;
import com.candao.spas.flow.sdk.parses.impl.bean.BeanNodeComponent;
import com.candao.spas.flow.sdk.parses.impl.begin.BeginNodeComponent;
import com.candao.spas.flow.sdk.parses.impl.condition.ConditionNodeComponent;
import com.candao.spas.flow.sdk.parses.impl.end.EndNodeComponent;
import com.candao.spas.flow.sdk.parses.impl.method.MethodNodeComponent;
import com.candao.spas.flow.sdk.parses.impl.service.ServiceNodeComponent;
import com.candao.spas.flow.sdk.parses.impl.subflow.SubFlowNodeComponent;

import java.util.HashMap;
import java.util.Map;

/**
 * 节点类型注册工厂
 *
 * */
public class NodeComponentFactory {

    private final static Map<String,NodeParser> cacheParser = new HashMap<>();

    static {
        // 固定节点
        cacheParser.put(NodeParserEnum.BEGIN.getValue(),new BeginNodeComponent());
        cacheParser.put(NodeParserEnum.END.getValue(),new EndNodeComponent());

        // 功能节点
        cacheParser.put(NodeParserEnum.BEAN.getValue(),new BeanNodeComponent());
        cacheParser.put(NodeParserEnum.METHOD.getValue(),new MethodNodeComponent());
        cacheParser.put(NodeParserEnum.SUBFLOW.getValue(),new SubFlowNodeComponent());
        cacheParser.put(NodeParserEnum.SERVICE.getValue(),new ServiceNodeComponent());
        cacheParser.put(NodeParserEnum.CONDITION.getValue(),new ConditionNodeComponent());
    }

    public static NodeParser getNodeInstance(String nodeName){
        return cacheParser.get(nodeName);
    }
}