package com.candao.test.spas.flow.model.bean;


import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Class {

    // 班级名称
    private String name;

    // 班级人数
    private Integer studentNum;

    // 班级班主任
    private Teacher teacher;
}
