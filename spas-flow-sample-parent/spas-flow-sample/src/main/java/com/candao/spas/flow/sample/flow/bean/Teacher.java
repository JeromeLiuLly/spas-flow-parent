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
public class Teacher {

    // 教师工号
    private String sn;

    // 教师名称
    private String name;

    // 教师授课班级
    private List<String> classesSN;

    // 教师所属年级
    private String projectSN;
}
