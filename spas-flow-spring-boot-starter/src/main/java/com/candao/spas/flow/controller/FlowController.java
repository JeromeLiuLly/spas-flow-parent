package com.candao.spas.flow.controller;

import com.candao.spas.flow.core.model.resp.ResponseFlowDataVo;
import com.candao.spas.flow.sdk.context.FlowContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/flows")
public class FlowController {

    @Autowired
    private FlowContext flowContext;

    @PostMapping("/show")
    public ResponseFlowDataVo show(){
        return ResponseFlowDataVo.generateSuccess(flowContext.getFlowDefintitionMap());
    }

}
