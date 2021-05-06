package com.candao.spas.flow.sample.flow.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    // 学生学号
    private Integer sn;

    // 学生姓名
    private String name;

    // 班级序号
    private String classSN;

    // 语文成绩
    private Integer languages;

    // 数学成绩
    private Integer mathematics;

    // 英语成绩
    private Integer english;
}