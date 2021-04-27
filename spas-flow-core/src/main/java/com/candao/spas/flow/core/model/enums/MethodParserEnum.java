package com.candao.spas.flow.core.model.enums;

import lombok.Getter;


/**
 * 执行节点核心方法类型
 *
 * */
@Getter
public enum MethodParserEnum {

    HANDLE("handle","业务执行方法"),

    SUCCESS("success","成功后执行方法"),

    COMPLATE("complate","最终执行方法(无论[成功/失败]都会执行)"),

    FAIL("fail","失败/异常后执行方法"),

    ROLLBACK("rollback","回滚方法");

    private String value;

    private String desc;

    MethodParserEnum(String value,String desc){
        this.value = value;
        this.desc = desc;
    }
}