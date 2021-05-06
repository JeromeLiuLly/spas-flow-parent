package com.candao.test.spas.flow.model.bean;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class InitClass {

    // ============

    @Test
    public Teacher initMainTeacher(){
        Teacher teacher = new Teacher();

        teacher.setSn("T1140120101");
        teacher.setName("张小小");

        Project project = new Project();
        project.setName("初三");
        teacher.setProject(project);

        return teacher;
    }

    @Test
    public Teacher initZhangSanTeacher(){
        Teacher teacher = new Teacher();

        teacher.setSn("T1140120102");
        teacher.setName("张三");

        Project project = new Project();
        project.setName("初三");
        teacher.setProject(project);

        return teacher;
    }

    @Test
    public Teacher initLisiTeacher(){
        Teacher teacher = new Teacher();

        teacher.setSn("T1140120103");
        teacher.setName("李四");

        Project project = new Project();
        project.setName("初三");
        teacher.setProject(project);

        return teacher;
    }

    @Test
    public Teacher initZhaoWuTeacher(){
        Teacher teacher = new Teacher();

        teacher.setSn("T1140120104");
        teacher.setName("赵五");

        Project project = new Project();
        project.setName("初三");
        teacher.setProject(project);

        return teacher;
    }

    @Test
    public List<Teacher> initTeacher(Project project){

        List<Teacher> teachers = new ArrayList<>();

        Teacher teacher01 = initZhangSanTeacher();

        Teacher teacher02 = initLisiTeacher();

        Teacher teacher03 = initZhaoWuTeacher();


        teachers.add(teacher01);
        teachers.add(teacher02);
        teachers.add(teacher03);

        return teachers;
    }

    @Test
    public List<com.candao.test.spas.flow.model.bean.Class> initClass(Project project){

        List<com.candao.test.spas.flow.model.bean.Class> classes = new ArrayList<>();
        com.candao.test.spas.flow.model.bean.Class cls01 = new com.candao.test.spas.flow.model.bean.Class();

        cls01.setName("初三一班");
        cls01.setStudentNum(50);
        cls01.setTeacher(initZhangSanTeacher());

        com.candao.test.spas.flow.model.bean.Class cls02 = new com.candao.test.spas.flow.model.bean.Class();

        cls02.setName("初三二班");
        cls02.setStudentNum(48);
        cls02.setTeacher(initLisiTeacher());

        com.candao.test.spas.flow.model.bean.Class cls03 = new com.candao.test.spas.flow.model.bean.Class();

        cls03.setName("初三三班");
        cls03.setStudentNum(44);
        cls03.setTeacher(initZhaoWuTeacher());

        classes.add(cls01);
        classes.add(cls02);
        classes.add(cls03);
        return classes;
    }

    @Test
    public Project initProject(){
        Project project = new Project();

        project.setName("初三");

        List<Class> classes = initClass(project);

        Integer count = classes.get(0).getStudentNum() + classes.get(1).getStudentNum() + classes.get(2).getStudentNum();
        project.setStudentCount(count);

        project.setTeacher(initMainTeacher());
        project.setClasses(classes);

        List<Teacher> list = new ArrayList<>();
        for(Class cls : classes){
            list.add(cls.getTeacher());
        }
        project.setTeachers(list);

        return project;
    }
}
