package com.candao.spas.flow.sdk.parses.impl.subflow;

import com.candao.spas.flow.core.model.enums.MethodParserEnum;
import com.candao.spas.flow.core.model.resp.ResponseFlowDataVo;
import com.candao.spas.flow.core.model.vo.Node;
import com.candao.spas.flow.sdk.exception.FlowException;
import com.candao.spas.flow.sdk.parses.IFlowHandler;
import com.candao.spas.flow.sdk.parses.abs.AbstractNodeComponent;
import com.candao.spas.flow.sdk.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

@Slf4j
public class SubFlowNodeComponent<T,R> extends AbstractNodeComponent<T,R> {

    @Override
    public void parser(String flowId, Node node, T input, ResponseFlowDataVo<R> output, MethodParserEnum method) {

        try{
            String component = node.getComponent();
            IFlowHandler flowHandler = SpringContextUtil.getBean(IFlowHandler.class);
            ResponseFlowDataVo responseDataVo = flowHandler.execute(component,input);
            BeanUtils.copyProperties(responseDataVo,output);
        }catch (Exception e){
            e.printStackTrace();
            throw new FlowException("子流程构造失败:"+e.getMessage());
        }
    }
}
