package com.candao.spas.flow.sample.flow.bean;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Class {

    // 班级序号
    private String sn;

    // 班级名称
    private String name;

    // 班级人数
    private Integer studentNum;

    // 班级班主任
    private String teacherSN;

    // 学生列表
    private List<String> studentsSN;


}
