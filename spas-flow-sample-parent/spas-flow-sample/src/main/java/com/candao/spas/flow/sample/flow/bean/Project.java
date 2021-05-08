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
public class Project {

    // 年级序号
    private String sn;

    // 年级主任
    private Teacher teacher;

    // 年级名称
    private String name;

    // 年级老师集合
    private List<String> teachersSN;

    // 年级学生总人数
    private Integer studentCount;

    // 班级集合
    private List<String> classesSN;
}
