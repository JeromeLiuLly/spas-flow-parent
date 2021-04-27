package com.candao.spas.flow.core.model.enums;

import lombok.Getter;

/**
 * 业务事件类型
 *
 * */
@Getter
public enum EventParserEnum {

    HTTP("http","http事件类型"),

    NATIVE("native","native事件类型,本地调用"),

    DUBBO("dubbo","dubbo事件类型"),

    THRIFT("thrift","thrift事件类型"),

    QRPC("gRPC","gRPC事件类型"),

    SOCKET("socket","socket事件类型");

    private String value;
    private String desc;

    EventParserEnum(String value,String desc){
        this.value = value;
        this.desc = desc;
    }

}
