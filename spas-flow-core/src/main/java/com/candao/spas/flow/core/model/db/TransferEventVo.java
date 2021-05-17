package com.candao.spas.flow.core.model.db;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "transfer_event_config")
public class TransferEventVo {

    @Id
    @Column(name = "id")
    private Integer id;

    /**
     * 所属工作流ID
     *
     * */
    @Column(name="flowId")
    private String flowId;

    /**
     * 所属节点ID
     *
     * */
    @Column(name="nodeId")
    private String nodeId;

    /**
     * 所属节点名称
     *
     * */
    @Column(name="nodeName")
    private String nodeName;

    /**
     * 所属节点类型
     *
     * */
    @Column(name="nodeType")
    private String nodeType;

    /**
     * 节点执行实现类
     *
     * */
    @Column(name="component")
    private String component;

    /**
     * 所属节点的上个节点名称
     *
     * */
    @Column(name="front")
    private String front;

    /**
     * 所属节点的下个节点名称
     *
     * */
    @Column(name="next")
    private String next;
    /**
     * 事件名称
     *
     * */
    @Column(name="eventName")
    private String eventName;

    /**
     * 事件描述
     *
     * */
    @Column(name="description")
    private String description;

    /**
     * 输入参数
     *
     * */
    @Column(name="input")
    private String input;

    /**
     * 输出参数结构
     *
     * */
    @Column(name="output")
    @Transient
    private OutPutResponseVo output;

    /**
     * 请求地址
     *
     * eventType == Native,填入类路径（非Spring托管,填入类全路径; Spring托管,填入bean对象）
     *
     * */
    @Column(name="url")
    private String url;

    /**
     * 请求地址全链接：fullLink = url + ":" + serverPort + methodName
     *
     * */
    @Column(name="fullLink")
    private String fullLink;

    /**
     * 请求协议（post、get）
     *
     * 默认POST请求
     * */
    @Column(name="requertType")
    private String requertType = "POST";

    /**
     * 服务端口
     *
     * */
    @Column(name="serverPort")
    private String serverPort;

    /**
     * 请求方法
     *
     * */
    @Column(name="methodName")
    private String methodName;

    /**
     * 请求方法的入参类型(有顺序要求,根据请求方法的入参顺序),字符串
     *
     * */
    @Column(name="inputParamTypes")
    @Transient
    private List<String> inputParamTypesValues;

    /**
     * 请求方法的入参类型(有顺序要求,根据请求方法的入参顺序),实现类
     *
     * */
    private List inputParamTypes;

    /**
     * json转换规则
     *
     * */
    @Column(name="convertRule")
    private String convertRule;

    /**
     * 请求超时时间(秒)
     *
     * */
    @Column(name="timeout")
    private Integer timeout = 30;

    /**
     * 事件类型
     *
     * @link EventParserEnum
     *   Http: http请求
     *   Native: 本地方法调用
     *   Rpc: rpc请求
     * */
    @Column(name="eventType")
    private String eventType;

    /**
     * 事件模型版本
     *
     * */
    @Column(name="version")
    private String version;

    /**
     * 事件模型状态
     *
     * */
    @Column(name="status")
    private Integer status = 1;
}
