package com.candao.spas.flow.sample.flow.service;

import com.candao.spas.flow.core.model.req.RequestFlowDataVo;
import com.candao.spas.flow.core.model.resp.ResponseFlowDataVo;
import com.candao.spas.flow.core.model.vo.FlowDefintion;
import com.candao.spas.flow.jackson.EasyJsonUtils;
import com.candao.spas.flow.sdk.parseing.FlowParser;
import com.candao.spas.flow.support.factory.FlowDefintitionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.candao.spas.flow.sample.flow.bean.*;

import javax.annotation.Resource;

@Slf4j
@Service
public class FlowService {

    public void commonMethod(){
        log.info("我是Spring托管-Method,无参,返回值:void");
    }

    public ResponseFlowDataVo commonMethod(RequestFlowDataVo input,ResponseFlowDataVo output){
        log.info("我是Spring托管-Method,带入参,返回值:ResponseFlowDataVo");
        TempProject project = EasyJsonUtils.toJavaObject(input.getData(),TempProject.class);
        project.setProjectName("Spring托管-Method_"+project.getProjectName());
        //project.setProjectName("dee600f6dd1c2e9d");
        //project.setStudentCount(1027);
        output.setData(project);

        return  output;
    }

    public Object commonMethod2(RequestFlowDataVo input,ResponseFlowDataVo output){
        log.info("我是Spring托管-Method,带入参,返回值:obj");
        TempProject project = EasyJsonUtils.toJavaObject(input.getData(),TempProject.class);
        project.setProjectName("Spring托管-Method_"+project.getProjectName());
        //project.setProjectName("dee600f6dd1c2e9d");
        //project.setStudentCount(1027);
        return project;
    }
}
