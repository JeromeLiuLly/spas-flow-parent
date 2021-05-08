package com.candao.spas.flow.sample.flow.bean;


import com.candao.spas.flow.core.utils.EasyJsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InitClass {

    public Teacher initMainTeacher(String projectSN){
        Teacher teacher = new Teacher();

        teacher.setSn("T1140120101");
        teacher.setName("张小小");
        teacher.setProjectSN(projectSN);

        return teacher;
    }

    public Teacher initZhangSanTeacher(String projectSN){
        Teacher teacher = new Teacher();

        teacher.setSn("T1140120102");
        teacher.setName("张三");
        teacher.setProjectSN(projectSN);

        return teacher;
    }

    public Teacher initLisiTeacher(String projectSN){
        Teacher teacher = new Teacher();

        teacher.setSn("T1140120103");
        teacher.setName("李四");

        teacher.setProjectSN(projectSN);

        return teacher;
    }

    public Teacher initZhaoWuTeacher(String projectSN){
        Teacher teacher = new Teacher();

        teacher.setSn("T1140120104");
        teacher.setName("赵五");

        teacher.setProjectSN(projectSN);

        return teacher;
    }

    public List<com.candao.spas.flow.sample.flow.bean.Class> initClass(String projectSN){

        List<com.candao.spas.flow.sample.flow.bean.Class> classes = new ArrayList<>();
        com.candao.spas.flow.sample.flow.bean.Class cls01 = new com.candao.spas.flow.sample.flow.bean.Class();

        cls01.setSn("cls-01");
        cls01.setName("初三一班");
        cls01.setStudentNum(50);
        cls01.setTeacherSN(initZhangSanTeacher(projectSN).getSn());

        com.candao.spas.flow.sample.flow.bean.Class cls02 = new com.candao.spas.flow.sample.flow.bean.Class();

        cls02.setSn("cls-02");

        cls02.setName("初三二班");
        cls02.setStudentNum(48);
        cls02.setTeacherSN(initLisiTeacher(projectSN).getSn());

        com.candao.spas.flow.sample.flow.bean.Class cls03 = new com.candao.spas.flow.sample.flow.bean.Class();

        cls03.setSn("cls-03");
        cls03.setName("初三三班");
        cls03.setStudentNum(44);
        cls03.setTeacherSN(initZhaoWuTeacher(projectSN).getSn());

        classes.add(cls01);
        classes.add(cls02);
        classes.add(cls03);
        return classes;
    }

    public Project initProject(){
        Project project = new Project();

        project.setSn("1140");
        project.setName("初三");

        // 设置班级主任
        project.setTeacher(initMainTeacher(project.getSn()));

        List<Class> classes = initClass(project.getSn());

        Integer count = classes.get(0).getStudentNum() + classes.get(1).getStudentNum() + classes.get(2).getStudentNum();
        project.setStudentCount(count);


        project.setClassesSN(classes.stream().map(Class::getSn).collect(Collectors.toList()));

        project.setTeachersSN(classes.stream().map(Class::getTeacherSN).collect(Collectors.toList()));

        return project;
    }

    public static void main(String[] args) {
        InitClass init = new InitClass();
        Project project = init.initProject();
        System.out.println(EasyJsonUtils.toJsonString(project));
    }
}
