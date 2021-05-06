package com.candao.test.spas.flow.model.bean;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    // 学生学号
    private Integer sn;

    // 学生姓名
    private String name;

    // 语文成绩
    private Integer languages;

    // 数学成绩
    private Integer mathematics;

    // 英语成绩
    private Integer english;
}