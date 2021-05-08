package com.candao.spas.flow.mysql.spring.boot.autoconfigure.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 路由数据源类型
 */
@Getter
@AllArgsConstructor
public enum RouteDatasourceTypeEnum {
    /**
     * hikari 类型
     */
    HIKARI("hikari"),
    ;
    private String name;
}