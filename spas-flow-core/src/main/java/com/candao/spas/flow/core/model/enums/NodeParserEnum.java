package com.candao.spas.flow.core.model.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 节点类型
 *
 * */
@Getter
public enum NodeParserEnum {

    /**
     * 根节点,表识符
     *
     *
     * */
    ROOT("root","根节点"),

    /**
     * 开始节点不进入以下方法
     * 
     * @method success
     * @method complate
     * @method fail
     * @method rollback
     *
     * */
    BEGIN("begin","开始节点"),

    /**
     * 结束节点不进入以下方法
     *
     * @method success
     * @method complate
     * @method fail
     * @method rollback
     *
     * */
    END("end","结束节点"),

    /**
     * 条件节点不进入以下方法
     *
     * @method success
     * @method complate
     * @method fail
     * @method rollback
     *
     * */
    CONDITION("condition","条件节点"),

    METHOD("method","方法节点"),

    BEAN("bean","对象填槽节点"),

    SERVICE("service","服务节点"),

    SUBFLOW("subflow","子流程节点");

    private String value;
    private String desc;

    NodeParserEnum(String value,String desc){
        this.value = value;
        this.desc = desc;
    }

    public static List<String> returnCollectionList() {
        List<String> returnList = new ArrayList<>();
        returnList.add(NodeParserEnum.BEGIN.value);
        returnList.add(NodeParserEnum.CONDITION.value);
        returnList.add(NodeParserEnum.SUBFLOW.value);
        returnList.add(NodeParserEnum.END.value);
        return returnList;
    }
}