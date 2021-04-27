package com.candao.spas.flow.sdk.parses;

import com.candao.spas.flow.core.model.resp.ResponseFlowDataVo;

public interface IFlowHandler {
    ResponseFlowDataVo execute(String component, Object input) throws Exception;
}