package com.candao.spas.flow.sample.http.controller;

import com.candao.spas.flow.core.model.resp.ResponseFlowDataVo;
import com.candao.spas.flow.jackson.EasyJsonUtils;
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
        param.put("token","b172b2e2d3434f31f5ae7d4780c06faf");
        return ResponseFlowDataVo.generateSuccess(param);
    }
}
