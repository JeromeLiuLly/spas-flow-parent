package com.candao.spas.flow.core.model.vo;

import lombok.*;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TransferEventModel {

    /**
     * 所属工作流ID
     *
     * */
    private String flowId;

    /**
     * 所属节点ID
     *
     * */
    private String nodeId;

    /**
     * 事件名称
     *
     * */
    private String eventName;

    /**
     * 事件描述
     *
     * */
    private String desc;

    /**
     * 输入参数结构
     *
     * */
    private Object input;

    /**
     * 输出参数结构
     *
     * */
    private Map<String,Object> output;

    /**
     * 请求地址
     *
     * eventType == Native,填入类路径（非Spring托管,填入类全路径; Spring托管,填入bean对象）
     *
     * */
    private String url;

    /**
     * 请求地址全链接：fullLink = url + ":" + serverPort + methodName
     *
     * */
    private String fullLink;

    /**
     * 请求协议（post、get）
     *
     * 默认POST请求
     * */
    private String requertType = "POST";

    /**
     * 服务端口
     *
     * */
    private Integer serverPort;

    /**
     * 请求方法、路由端点
     *
     * */
    private String methodName;

    /**
     * 请求方法的入参类型(有顺序要求,根据请求方法的入参顺序),字符串
     *
     * */
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
    private String convertRule;

    /**
     * 请求超时时间(秒)
     *
     * */
    private Integer timeout = 30;

    /**
     * 事件类型
     *
     * @link EventParserEnum
     *   Http: http请求
     *   Native: 本地方法调用
     *   Rpc: rpc请求
     * */
    private String eventType;

    /**
     * 事件模型版本
     *
     * */
    private String version;

    /**
     * 事件模型状态
     *
     * */
    private Boolean status;

    public String getFullLink(){
        return getUrl() +":"+ getServerPort() + getMethodName();
    }
}
