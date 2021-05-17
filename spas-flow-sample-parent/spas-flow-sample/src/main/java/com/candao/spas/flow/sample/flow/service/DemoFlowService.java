package com.candao.spas.flow.sample.flow.service;

import com.candao.spas.flow.core.model.db.TransferEventVo;
import com.candao.spas.flow.core.model.enums.NodeParserEnum;
import com.candao.spas.flow.core.model.resp.ResponseFlowDataVo;
import com.candao.spas.flow.core.model.vo.FlowDefintion;
import com.candao.spas.flow.core.model.vo.Node;
import com.candao.spas.flow.jackson.EasyJsonUtils;
import com.candao.spas.flow.sample.flow.bean.InitClass;
import com.candao.spas.flow.sample.flow.bean.Project;
import com.candao.spas.flow.sdk.mapper.TransferConfigMapper;
import com.candao.spas.flow.sdk.parseing.FlowParser;
import com.candao.spas.flow.support.factory.FlowDefintitionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DemoFlowService {

    @Resource
    private FlowDefintitionFactory flowDefintitionFactory;

    @Resource
    private TransferConfigMapper transferConfigMapper;

    public ResponseFlowDataVo test(){
        FlowDefintion defintition = flowDefintitionFactory.getFlowDefintion("flow");

        log.info(EasyJsonUtils.toJsonString(defintition));

        //test("flow");

        FlowParser flowParser = new FlowParser(defintition);

        InitClass init = new InitClass();
        Project project = init.initProject();
        ResponseFlowDataVo responseFlowDataVo = null;
        try {
            responseFlowDataVo = flowParser.execute(project);
        }catch (Exception e){
            e.printStackTrace();
        }
        return responseFlowDataVo;
    }

    public ResponseFlowDataVo test(String flowId,Project project){

        FlowDefintion defintition = flowDefintitionFactory.getFlowDefintion(flowId);

        FlowParser flowParser = new FlowParser(defintition);

        //InitClass init = new InitClass();
        //Project project = init.initProject();
        ResponseFlowDataVo responseFlowDataVo = null;
        try {
            responseFlowDataVo = flowParser.execute(project);
        }catch (Exception e){
            e.printStackTrace();
        }
        return responseFlowDataVo;
    }

}
