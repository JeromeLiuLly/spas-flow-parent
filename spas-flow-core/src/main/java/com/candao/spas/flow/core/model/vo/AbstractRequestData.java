package com.candao.spas.flow.core.model.vo;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractRequestData<T> implements Serializable {

	 /**
     * 请求业务具体内容-json串对象
     */
    protected T data;
}