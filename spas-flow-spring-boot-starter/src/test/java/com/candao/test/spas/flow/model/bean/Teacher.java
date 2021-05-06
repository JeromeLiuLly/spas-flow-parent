package com.candao.test.spas.flow.model.bean;

import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Teacher {

    // 教师工号
    private String sn;

    // 教师名称
    private String name;

    // 教师授课班级
    private List<Class> classes;

    // 教师所属年级
    private Project project;
}
