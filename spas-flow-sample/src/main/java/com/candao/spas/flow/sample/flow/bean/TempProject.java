package com.candao.spas.flow.sample.flow.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TempProject {

    private String projectName;
    private TempTeacher teacher;
    private List<String> teachersSN;
    private Integer studentCount;

    @Setter
    @Getter
    public static class TempTeacher{
        private String number;
        private String userName;
    }
}
