package com.candao.spas.flow.runner;

import com.candao.spas.flow.sdk.context.FlowContext;
import com.candao.spas.flow.support.factory.FlowDefintitionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class FlowConfigurationRunner{

    @Autowired
    private FlowDefintitionFactory flowDefintitionFactory;

    @Bean
    public FlowContext flowContext(){
        flowDefintitionFactory.initDefintionFactory();
        FlowContext flowContext = new FlowContext();
        flowContext.setFlowDefintitionMap(flowDefintitionFactory.getFlowDefintitionMap());
        return flowContext;
    }
}