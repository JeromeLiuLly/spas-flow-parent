package com.candao.spas.flow.sample.flow.controller;

import com.candao.spas.flow.core.model.resp.ResponseFlowDataVo;
import com.candao.spas.flow.core.utils.EasyJsonUtils;
import com.candao.spas.flow.sample.dubbo.api.DubboSampleProvider;
import com.candao.spas.flow.sample.flow.service.FlowService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/flow")
public class FlowChainController {

    @Autowired
    private FlowService flowService;

    @DubboReference//(url = "dubbo://127.0.0.1:18110")
    private DubboSampleProvider dubboSampleProvider;

    @PostMapping(value = "/test")
    public ResponseFlowDataVo test(){

        log.info(EasyJsonUtils.toJsonString(dubboSampleProvider.getStudent()));
        return flowService.test();
    }
}
