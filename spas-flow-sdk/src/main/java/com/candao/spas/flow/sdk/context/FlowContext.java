package com.candao.spas.flow.sdk.context;

import com.candao.spas.flow.core.model.vo.FlowDefintion;
import lombok.*;

import java.util.Map;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FlowContext {
    private Map<String, FlowDefintion> flowDefintitionMap;

    public FlowDefintion getFlowDefinition(String flowId){
        return getFlowDefintitionMap().get(flowId);
    }
}