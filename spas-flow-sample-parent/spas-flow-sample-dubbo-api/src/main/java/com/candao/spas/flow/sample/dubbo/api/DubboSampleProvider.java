package com.candao.spas.flow.sample.dubbo.api;


import com.candao.spas.flow.sample.dubbo.bean.Student;

public interface DubboSampleProvider {

    public Student getStudent();

    public Student getStudentById(String Id);

}
