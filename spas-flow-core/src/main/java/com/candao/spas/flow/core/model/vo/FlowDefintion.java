package com.candao.spas.flow.core.model.vo;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FlowDefintion {
    /**
     * 流程标识(唯一)
     *
     * */
    private String flowId;

    /**
     * 流程名称
     *
     * */
    private String flowName;

    /**
     * 流程描述
     *
     * */
    private String desc;

    /**
     * 开始节点id
     *
     * */
    private String startNodeId;

    /**
     * 子流程节点
     *
     * */
    private Map<String,Node> nodeMap;
}