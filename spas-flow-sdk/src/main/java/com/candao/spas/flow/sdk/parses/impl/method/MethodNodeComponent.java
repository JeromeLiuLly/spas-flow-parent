package com.candao.spas.flow.sdk.parses.impl.method;

import com.candao.spas.flow.core.model.enums.MethodParserEnum;
import com.candao.spas.flow.core.model.resp.ResponseFlowDataVo;
import com.candao.spas.flow.core.model.vo.Node;
import com.candao.spas.flow.sdk.parses.abs.AbstractNodeComponent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MethodNodeComponent<T,R> extends AbstractNodeComponent<T,R> {

    @Override
    public void parser(Node node, T input, ResponseFlowDataVo<R> output, MethodParserEnum methodParserEnum) {
        invokeMethod(node,input,output,methodParserEnum);
    }
}
