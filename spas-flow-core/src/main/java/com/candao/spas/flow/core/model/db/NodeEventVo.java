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
@Table(name = "node_event_config")
public class NodeEventVo {

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
     * 控件名称
     *
     * */
    @Column(name = "label")
    private String label;

    /**
     * 图标背景颜色
     *
     * */
    @Column(name = "className")
    private String className;

    /**
     * 图标
     *
     * */
    @Column(name = "iconType")
    private String iconType;

    /**
     * 条件控件
     *
     * */
    @Column(name = "shape")
    private String shape;

    /**
     * 条件控件名称
     *
     * */
    @Column(name = "text")
    private String text;

    /**
     * 颜色
     *
     * */
    @Column(name = "color")
    private String color;

    /**
     * 高度
     *
     * */
    @Column(name = "top")
    private Integer top;

    /**
     * 左距离
     *
     * */
    @Column(name = "leftPoint")
    private Integer leftPoint;

    /**
     * 连线点
     *
     * */
    @Column(name = "endpoints")
    private String endpoints;

    /**
     * 节点类型
     *
     * */
    @JsonProperty("Class")
    @Column(name = "vueClass")
    private String vueClass;

    /**
     * 事件模型状态
     *
     * */
    @Column(name = "status")
    private Integer status;
}
