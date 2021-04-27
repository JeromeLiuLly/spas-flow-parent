package com.candao.spas.flow.sdk.parses.impl.condition;

import com.candao.spas.flow.core.constants.ChainConstants;
import com.candao.spas.flow.core.model.enums.MethodParserEnum;
import com.candao.spas.flow.core.model.resp.ResponseFlowDataVo;
import com.candao.spas.flow.core.model.vo.Node;
import com.candao.spas.flow.core.utils.EasyJsonUtils;
import com.candao.spas.flow.sdk.parseing.FlowParserHandler;
import com.candao.spas.flow.sdk.parses.NodeParser;
import com.candao.spas.flow.sdk.parses.abs.AbstractNodeComponent;
import com.candao.spas.flow.sdk.parses.factory.NodeComponentFactory;
import com.candao.spas.flow.core.utils.SpleUtils;
import com.googlecode.aviator.AviatorEvaluator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 条件节点,需要不断优化处理逻辑[针对：布尔类型、数组类型以及个性化判断]
 *
 *  @link Spel    https://docs.spring.io/spring-framework/docs/5.2.6.RELEASE/spring-framework-reference/core.html#expressions-evaluation
 *  @link Drools
 *  @link Aviator https://www.yuque.com/boyan-avfmj/aviatorscript
 * */
@Slf4j
public class ConditionNodeComponent<T,R> extends AbstractNodeComponent<T,R> {
    private int count = 0;

    @Override
    public void parser(Node node, T input, ResponseFlowDataVo<R> output, MethodParserEnum method) {
        String conditions = node.getComponent();

        // 拆解多个条件
        String[] condition = conditions.split(ChainConstants.COMMA);
        AtomicReference<ResponseFlowDataVo> outputAtomic = new AtomicReference<>(new ResponseFlowDataVo());

        // 断言是否执行业务函数
        if (MethodParserEnum.HANDLE.getValue().equals(method.getValue())){
            Arrays.stream(condition).forEach(str -> {

                // 根据拆解,解析条件内容和目标节点
                String[] split1 = str.split(ChainConstants.COLON);

                // 表达式求值运行
                Object eval = SpleUtils.eval(split1[0], input);
                //Map<String,Object> param = EasyJsonUtils.toJavaObject(input,Map.class);
                //Object eval = AviatorEvaluator.execute(split1[0],param,true);

                // 断言表达式是否满足条件
                if(Boolean.parseBoolean(eval.toString())){
                    String nodeStr = split1[1];
                    Node nodeByCondition = getNodeMap().get(nodeStr);
                    String nextNode = nodeByCondition.getNext();
                    log.info(node.getNodeId()+"_"+method.getValue()+",判断内容: "+split1[0]+" ==>通过,进入下个节点:"+nodeStr);
                    if(!StringUtils.isEmpty(nextNode)){
                        FlowParserHandler flowParserHandler = new FlowParserHandler();
                        flowParserHandler.execNode(nodeByCondition, input, nodeMap,output);
                        outputAtomic.set(output);
                    }else{
                        //解决循环执行节点问题
                        count++;
                        if(count==1) {
                            String type = nodeByCondition.getNodeType();
                            NodeParser nodeInstance = NodeComponentFactory.getNodeInstance(type);
                            nodeInstance.setNodeMap(nodeMap);
                            nodeInstance.parserNode(nodeByCondition, input, output,method);
                            outputAtomic.set(output);
                        }
                    }
                }else{
                    log.info(node.getNodeId()+"_"+method.getValue()+",判断内容: "+split1[0]+" ==>不通过");
                }
            });
        }
    }
}