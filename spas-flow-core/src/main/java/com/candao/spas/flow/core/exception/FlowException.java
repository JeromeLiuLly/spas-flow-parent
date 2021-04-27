package com.candao.spas.flow.core.exception;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class FlowException extends RuntimeException implements Serializable {


    private static final long serialVersionUID = 207763023214407987L;

    public FlowException(String msg) {
        super(msg);
    }

    public FlowException(String msg, Throwable e) {
        super(msg, e);
    }
}