package com.candao.spas.flow.core.model.vo;

import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Node {

    /**
     * 节点名称（唯一）
     *
     * */
    private String nodeId;

    /**
     * 节点名称
     *
     * */
    private String nodeName;

    /**
     * 节点描述
     *
     * */
    private String desc;

    /**
     * 节点类型()
     *
     * @link NodeParserEnum
     *   end: 结束节点
     *   bean: 对象节点
     *   begin: 开始节点
     *   method: 方法节点
     *   service: 服务节点
     *   subflow: 子流程节点
     *   condition: 条件节点
     * */
    private String nodeType;

    /**
     * 节点执行路径（Spring 注解单例对象 OR 子流程的flowId）
     *
     * */
    private String component;

    /**
     * 事件类型实体
     *
     * */
    private TransferEventModel transfer;

    /**
     * 失败重试次数
     *
     * */
    private Integer retryTime = 3;

    /**
     * 是否异步执行
     *
     * */
    private Boolean asyn = false;

    /**
     * 执行后,进入睡眠时长（单位：毫秒）
     *
     * */
    private Integer sleep = 1;

    /**
     * 上一个节点标识Id
     *
     * */
    private String front;

    /**
     * 下一个节点标识Id
     *
     * */
    private String next;
}
