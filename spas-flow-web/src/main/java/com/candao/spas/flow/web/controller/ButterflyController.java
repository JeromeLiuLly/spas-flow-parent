package com.candao.spas.flow.web.controller;

import com.candao.spas.flow.core.model.resp.ResponseFlowDataVo;
import com.candao.spas.flow.core.utils.StringUtil;
import com.candao.spas.flow.jackson.EasyJsonUtils;
import com.candao.spas.flow.web.bean.ResponseButterflyFlowVo;
import com.candao.spas.flow.web.service.ButterflyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/butterfly")
public class ButterflyController {

    @Autowired
    private ButterflyService butterflyService;

    @PostMapping(value = "/save")
    public ResponseFlowDataVo save(@RequestBody ResponseButterflyFlowVo responseButterflyFlowVo){

        log.info(EasyJsonUtils.toJsonString(responseButterflyFlowVo));
        if (responseButterflyFlowVo != null ) {
            butterflyService.save(responseButterflyFlowVo);
        }
        return ResponseFlowDataVo.generateSuccess();
    }

    @PostMapping(value = "/get")
    public ResponseFlowDataVo get(@RequestParam("flowId") String flowId){
        if (StringUtil.isNullOrBlank(flowId)){
            return ResponseFlowDataVo.generateFail("工作流flowId,不能为空！");
        }
        return ResponseFlowDataVo.generateSuccess(butterflyService.getFlow(flowId));
    }

    @PostMapping(value = "/getFlows")
    public ResponseFlowDataVo getFlows(){
        return ResponseFlowDataVo.generateSuccess(butterflyService.findFlows());
    }
}
