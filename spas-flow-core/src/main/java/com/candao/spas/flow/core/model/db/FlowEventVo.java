package com.candao.spas.flow.core.model.db;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "flow_event_config")
public class FlowEventVo {

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
     * 所属工作流名称
     *
     * */
    @Column(name = "flowName")
    private String flowName;

    /**
     * 工作流描述
     *
     * */
    @Column(name="description")
    private String description;

    /**
     * 事件模型状态
     *
     * */
    @Column(name="status")
    private Integer status = 1;
}
