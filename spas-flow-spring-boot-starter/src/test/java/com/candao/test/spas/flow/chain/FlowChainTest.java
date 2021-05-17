package com.candao.test.spas.flow.chain;

import com.candao.spas.flow.core.model.vo.FlowDefintion;
import com.candao.spas.flow.jackson.EasyJsonUtils;
import com.candao.spas.flow.sdk.parseing.FlowParser;
import com.candao.spas.flow.support.factory.FlowDefintitionFactory;
import com.candao.test.spas.flow.model.bean.InitClass;
import com.candao.test.spas.flow.model.bean.Project;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class FlowChainTest {

    @Autowired
    private FlowDefintitionFactory flowDefintitionFactory;

    //@Test
    public void main(){
        //flowChain();
        flowChainWork();
    }

    /**
     * 验证 Chain 工作流对象生成
     *
     * */
    public void flowChain(){
        log.info(EasyJsonUtils.toJsonString(flowDefintitionFactory.getFlowDefintitionMap()));
    }

    /**
     * 验证 Chain 工作流对象生成
     *
     * */
    @Test
    public void flowChainWork(){
        FlowDefintion defintition = flowDefintitionFactory.getFlowDefintion("chain");
        FlowParser flowParser = new FlowParser(defintition);

        InitClass init = new InitClass();
        Project project = init.initProject();
        log.info(EasyJsonUtils.toJsonString(project));
        try {
            flowParser.execute(project);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
