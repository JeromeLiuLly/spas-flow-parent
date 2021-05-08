package com.candao.spas.flow.sample.http.controller;

import com.candao.spas.flow.core.model.resp.ResponseFlowDataVo;
import com.candao.spas.flow.core.utils.EasyJsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/flow/http")
public class HttpSampleController {

    @PostMapping(value = "/demo",consumes = "application/json")
    public ResponseFlowDataVo demo(@RequestBody Object o){
        log.info(EasyJsonUtils.toJsonString(o));

        Map<String,Object> param = new HashMap<>();
        param.put("userName","刘练源");
        param.put("input",o);
        param.put("token","56af4db227eb7c2a007e988bcbca9727");
        return ResponseFlowDataVo.generateSuccess(param);
    }
}
