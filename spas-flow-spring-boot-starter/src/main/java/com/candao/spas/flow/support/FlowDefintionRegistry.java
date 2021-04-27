package com.candao.spas.flow.support;

import com.candao.spas.flow.core.model.vo.FlowDefintion;
import java.util.Map;


public interface FlowDefintionRegistry {

    /**
     * flow注册接口
     *
     * @return map
     *
     * @throws Exception
     */
    Map<String, FlowDefintion> registry() throws Exception;
}