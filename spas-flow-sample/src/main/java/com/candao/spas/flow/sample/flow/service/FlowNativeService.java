package com.candao.spas.flow.sample.flow.service;

import com.candao.spas.flow.core.model.req.RequestFlowDataVo;
import com.candao.spas.flow.core.model.resp.ResponseFlowDataVo;
import com.candao.spas.flow.core.utils.EasyJsonUtils;
import com.candao.spas.flow.sample.flow.bean.TempProject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FlowNativeService {


    public void commonMethod(){
        log.info("我是Native-Method,无参,返回值:void");
    }

    public void commonMethod(RequestFlowDataVo input,ResponseFlowDataVo output) throws Exception {
        log.info("我是Native-Method,带入参,返回值:void");
        TempProject project = EasyJsonUtils.toJavaObject(output.getData(),TempProject.class);
        project.setProjectName("Native-Method_"+project.getProjectName());
        output.setData(project);
    }

    public Object commonMethod2(RequestFlowDataVo input,ResponseFlowDataVo output){
        log.info("我是Native-Method,带入参,返回值:obj");
        TempProject project = EasyJsonUtils.toJavaObject(output.getData(),TempProject.class);
        project.setProjectName("Native-Method_"+project.getProjectName());
        return project;
    }

}
