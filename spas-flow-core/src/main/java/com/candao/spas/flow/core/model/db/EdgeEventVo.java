package com.candao.spas.flow.core.model.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "edge_event_config")
public class EdgeEventVo {

    @Id
    @Column(name = "id")
    private Integer id;

    /**
     * 所属工作流ID
     *
     * */
    @Column(name = "flowId")
    private String flowId;

    /**
     * 所属节点ID
     *
     * */
    @Column(name = "nodeId")
    private String nodeId;

    /**
     * 源连线节点位置
     *
     * */
    @Column(name = "source")
    private String source;

    /**
     * 目标连线节点位置
     *
     * */
    @Column(name = "target")
    private String target;

    /**
     * 源连线节点名称
     *
     * */
    @Column(name = "sourceNode")
    private String sourceNode;

    /**
     * 目标连线节点名称
     *
     * */
    @Column(name = "targetNode")
    private String targetNode;
}
