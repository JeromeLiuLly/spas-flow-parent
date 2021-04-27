package com.candao.spas.flow.support.factory;

import com.candao.spas.flow.core.exception.FlowException;
import com.candao.spas.flow.core.model.vo.FlowDefintion;
import com.candao.spas.flow.support.FlowDefintionRegistry;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@Component
public class FlowDefintitionFactory {

    @Autowired
    public List<FlowDefintionRegistry> flowDefintionRegistries;

    public final Map<String, FlowDefintion> flowDefintitionMap = new HashMap<>();

    /**
     * 初始化工作流程模版加载
     *
     */
    public void initDefintionFactory() {
        flowDefintionRegistries.forEach(flowDefintionRegistry -> {
            try {
                flowDefintitionMap.putAll(flowDefintionRegistry.registry());
            } catch (Exception e) {
                throw new FlowException("Flow loading exception",e);
            }
        });
    }

    /**
     * 获取工作流对象
     *
     * */
    public FlowDefintion getFlowDefintion(String flowId){
        return flowDefintitionMap.get(flowId);
    }

    /**
     * 新增/刷新 工作流程
     *
     * */
    public void addFlowDefintitionMap(String key,FlowDefintion flowDefintion){
        flowDefintitionMap.put(key,flowDefintion);
    }
}