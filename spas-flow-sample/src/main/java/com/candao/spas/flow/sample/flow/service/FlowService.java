package com.candao.spas.flow.sample.flow.service;

import com.candao.spas.flow.core.model.req.RequestFlowDataVo;
import com.candao.spas.flow.core.model.resp.ResponseFlowDataVo;
import com.candao.spas.flow.core.model.vo.FlowDefintion;
import com.candao.spas.flow.core.utils.EasyJsonUtils;
import com.candao.spas.flow.sdk.parseing.FlowParser;
import com.candao.spas.flow.support.factory.FlowDefintitionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.candao.spas.flow.sample.flow.bean.*;

import javax.annotation.Resource;

@Slf4j
@Service
public class FlowService {

    @Resource
    private FlowDefintitionFactory flowDefintitionFactory;

    public ResponseFlowDataVo test(){
        FlowDefintion defintition = flowDefintitionFactory.getFlowDefintion("chain");
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

    public void commonMethod(){
        log.info("我是Spring托管-Method,无参,返回值:void");
    }

    public void commonMethod(RequestFlowDataVo input,ResponseFlowDataVo output){
        log.info("我是Spring托管-Method,带入参,返回值:void");
        TempProject project = EasyJsonUtils.toJavaObject(output.getData(),TempProject.class);
        project.setProjectName("Spring托管-Method_"+project.getProjectName());
        output.setData(project);
    }

    public Object commonMethod2(RequestFlowDataVo input,ResponseFlowDataVo output){
        log.info("我是Spring托管-Method,带入参,返回值:obj");
        TempProject project = EasyJsonUtils.toJavaObject(output.getData(),TempProject.class);
        project.setProjectName("Spring托管-Method_"+project.getProjectName());
        return project;
    }
}
