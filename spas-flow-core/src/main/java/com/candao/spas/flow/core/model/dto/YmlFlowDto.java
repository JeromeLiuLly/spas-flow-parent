package com.candao.spas.flow.core.model.dto;

import com.candao.spas.flow.core.model.vo.Node;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class YmlFlowDto {

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

    /***
     * 流程节点
     *
     * */
    private List<NodeDto> nodes;

    @Setter
    @Getter
    @ToString
    @NoArgsConstructor
    public static class NodeDto {
        private Node node;
    }
}