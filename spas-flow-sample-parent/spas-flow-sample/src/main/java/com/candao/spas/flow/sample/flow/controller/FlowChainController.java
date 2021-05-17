package com.candao.spas.flow.sample.flow.controller;

import com.candao.spas.flow.core.model.resp.ResponseFlowDataVo;
import com.candao.spas.flow.core.utils.StringUtil;
import com.candao.spas.flow.jackson.EasyJsonUtils;
import com.candao.spas.flow.sample.dubbo.api.DubboSampleProvider;
import com.candao.spas.flow.sample.flow.service.DemoFlowService;
import com.candao.spas.flow.sample.flow.service.FlowService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/flow")
public class FlowChainController {

    @Autowired
    private DemoFlowService demoFlowService;

    @DubboReference(url = "dubbo://127.0.0.1:19001")
    private DubboSampleProvider dubboSampleProvider;

    @PostMapping(value = "/test")
    public ResponseFlowDataVo test(){

        //log.info(EasyJsonUtils.toJsonString(dubboSampleProvider.getStudent()));
        return demoFlowService.test();
    }

    @PostMapping(value = "/{flowId}")
    public ResponseFlowDataVo test(@PathVariable(name = "flowId") String flowId){
        if (StringUtil.isNullOrBlank(flowId)){
            return ResponseFlowDataVo.generateFail("flowId 工作流id 不能为空");
        }
        return demoFlowService.test(flowId);
    }
}
