package com.candao.test.spas.flow.model.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    // 年级主任
    private Teacher teacher;

    // 年级名称
    private String name;

    // 年级老师集合
    private List<Teacher> teachers;

    // 年级学生总人数
    private Integer studentCount;

    // 班级集合
    private List<Class> classes;
}
