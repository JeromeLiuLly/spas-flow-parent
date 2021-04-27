package com.candao.spas.flow.core.model.vo;

import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TransferEventModel {

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
     * 输入参数
     *
     * */
    private Object input;

    /**
     * 输出参数
     *
     * */
    private Object output;

    /**
     * 请求地址
     *
     * */
    private String url;

    /**
     * 请求方法
     *
     * */
    private String methodName;

    /**
     * 服务端口
     *
     * */
    private String serverPort;

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

}