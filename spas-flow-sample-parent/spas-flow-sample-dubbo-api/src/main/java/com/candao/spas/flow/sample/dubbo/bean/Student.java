package com.candao.spas.flow.sample.dubbo.bean;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Student implements Serializable {

    private String sn;
    private String name;

}
