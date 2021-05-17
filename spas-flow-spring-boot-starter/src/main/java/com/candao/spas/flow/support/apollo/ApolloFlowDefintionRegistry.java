package com.candao.spas.flow.support.apollo;

import com.candao.spas.flow.core.model.vo.FlowDefintion;
import com.candao.spas.flow.jackson.EasyJsonUtils;
import com.candao.spas.flow.support.FlowDefintionRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Order(3)
@Component
public class ApolloFlowDefintionRegistry implements FlowDefintionRegistry {

    @Value("${flow.ApolloAllFlowMaps:{}}")
    private String apolloFlowMaps;

    @Override
    public Map<String, FlowDefintion> registry() throws Exception {
        return registryModel();
    }

    /**
     * 注册流程模型
     *
     * @return
     * @throws Exception
     */
    private Map<String, FlowDefintion> registryModel() throws Exception {
        try {
            Map<String, FlowDefintion> flowMaps = EasyJsonUtils.toJavaObject(apolloFlowMaps, Map.class);
            return flowMaps;
        }catch (Exception e){
            e.printStackTrace();
        }
        return new HashMap<>();
    }
}
