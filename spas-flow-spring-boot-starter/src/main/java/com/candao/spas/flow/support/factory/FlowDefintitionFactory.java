package com.candao.spas.flow.support.factory;

import com.candao.spas.flow.jackson.EasyJsonUtils;
import com.candao.spas.flow.sdk.exception.FlowException;
import com.candao.spas.flow.core.model.vo.FlowDefintion;
import com.candao.spas.flow.support.FlowDefintionRegistry;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@Slf4j
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
                Map<String,FlowDefintion> param = flowDefintionRegistry.registry();
                for(String key : param.keySet()){
                    if (!flowDefintitionMap.containsKey(key)){
                        flowDefintitionMap.put(key,param.get(key));
                    }else{
                        log.warn("存在相同工作流ID："+key+",工作流流程:"+EasyJsonUtils.toJsonString(param.get(key))+",该工作流无法进入缓存,注意检查。");
                    }
                }
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