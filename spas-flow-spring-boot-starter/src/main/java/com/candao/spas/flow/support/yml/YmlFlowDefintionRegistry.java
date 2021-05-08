package com.candao.spas.flow.support.yml;

import com.candao.spas.flow.core.constants.ChainConstants;
import com.candao.spas.flow.core.model.converter.FlowDefintionConverter;
import com.candao.spas.flow.core.model.dto.YmlFlowDto;
import com.candao.spas.flow.core.model.vo.FlowDefintion;
import com.candao.spas.flow.jackson.EasyJsonUtils;
import com.candao.spas.flow.support.FlowDefintionRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.YamlMapFactoryBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class YmlFlowDefintionRegistry implements FlowDefintionRegistry {

    @Autowired
    private FlowDefintionConverter flowDefintionConverter;

    @Override
    public Map<String, FlowDefintion> registry() throws Exception{
        return registryModel();
    }

    /**
     * 注册流程模型
     *
     * @return
     * @throws Exception
     */
    public Map<String, FlowDefintion> registryModel() throws Exception {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(ChainConstants.CLASSPATH_FLOW);
        Map<String, FlowDefintion> flowMap = new HashMap<>();
        Arrays.stream(resources).forEach(resource->{
            YamlMapFactoryBean yamlMapFactoryBean = new YamlMapFactoryBean();
            yamlMapFactoryBean.setResources(resource);
            yamlMapFactoryBean.afterPropertiesSet();
            Map<String, Object> object = yamlMapFactoryBean.getObject();
            YmlFlowDto flow = EasyJsonUtils.toJavaObject(object, YmlFlowDto.class);
            flowMap.put(flow.getFlowId(),buildFlowDefintition(flow));
        });
        return flowMap;
    }


    private FlowDefintion buildFlowDefintition(YmlFlowDto flow){
        FlowDefintion flowDefintion = flowDefintionConverter.domain2dto(flow);
        flowDefintion.setNodeMap(flowDefintionConverter.domain2Map(flow.getNodes()));
        return flowDefintion;
    }
}