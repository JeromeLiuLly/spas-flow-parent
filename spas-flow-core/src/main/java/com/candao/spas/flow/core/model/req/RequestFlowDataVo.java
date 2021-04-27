package com.candao.spas.flow.core.model.req;

import com.candao.spas.flow.core.model.vo.AbstractRequestData;
import com.candao.spas.flow.core.model.vo.Node;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class RequestFlowDataVo<T> extends AbstractRequestData<T> implements Serializable {

    /**
     * 时间戳,精确到秒
     */
    private Long timestamp;

    private Node node;
}
