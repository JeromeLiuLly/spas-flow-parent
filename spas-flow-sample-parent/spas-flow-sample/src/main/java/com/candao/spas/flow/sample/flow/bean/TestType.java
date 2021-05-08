package com.candao.spas.flow.sample.flow.bean;

import com.candao.spas.flow.core.model.vo.TransferEventModel;
import com.candao.spas.flow.sdk.utils.ClassUtil;

import java.util.ArrayList;
import java.util.List;

public class TestType {

    public static void main(String[] args) {
        TransferEventModel transfer = new TransferEventModel();

        Student student = new Student();
        student.setName("刘练源");

        Teacher teacher = new Teacher();
        List<String> classesSN = new ArrayList<>();
        classesSN.add("12");
        classesSN.add("34");
        teacher.setClassesSN(classesSN);
        teacher.setName("刘练源");

        List<String> list = new ArrayList<>();
        list.add("com.candao.spas.flow.sample.flow.bean.Teacher");
        list.add("java.lang.String:getName");
        list.add("java.util.List:getClassesSN");
        transfer.setInputParamTypesValues(list);

        transfer.getInputParamTypesValues().forEach((value)->{
            System.out.println(value);
            String[] split = value.split(":");
            java.lang.Class<?> cls;
            if (split.length > 1){
                cls = ClassUtil.forName(split[0]);
                transfer(teacher,transfer,cls,split[1]);
            }else {
                cls = ClassUtil.forName(value);
                transfer(teacher,transfer,cls,null);
            }
        });
    }

    public static void transfer(Object o , TransferEventModel transfer, java.lang.Class cls,String method){
        if (method == null){
            System.out.println("自定义");
        }else{
            System.out.println(ClassUtil.methodInvoke(o,method));
        }

    }
}
