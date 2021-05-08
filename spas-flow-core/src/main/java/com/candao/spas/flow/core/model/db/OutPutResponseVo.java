package com.candao.spas.flow.core.model.db;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OutPutResponseVo {

    private String code;
    private Object value;
    private String data;
    private String msg;

}
