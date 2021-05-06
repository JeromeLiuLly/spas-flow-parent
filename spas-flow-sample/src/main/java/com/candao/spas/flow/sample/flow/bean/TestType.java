package com.candao.spas.flow.sample.flow.bean;

import com.candao.spas.flow.core.model.vo.TransferEventModel;
import com.candao.spas.flow.core.utils.ClassUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestType {

    public static void main(String[] args) {
        TransferEventModel transfer = new TransferEventModel();

        List<String> list = new ArrayList<>();
        list.add("java.lang.String");
        transfer.setInputParamTypesValues(list);

        transfer.getInputParamTypesValues().forEach((value)->{
            System.out.println(value);
            java.lang.Class<?> cls = ClassUtil.forName(value);
            transfer(transfer,cls);
        });
    }

    public static void transfer(TransferEventModel transfer, java.lang.Class cls){
        if (cls.isAssignableFrom(Integer.class)){
            System.out.println("Integer");
        }else if (cls.isAssignableFrom(Double.class)){
            System.out.println("Double");
        }else if (cls.isAssignableFrom(Float.class)){
            System.out.println("Float");
        }else if (cls.isAssignableFrom(Number.class)){
            System.out.println("Number");
        }else if (cls.isAssignableFrom(Map.class)){
            System.out.println("Map");
        }else if (cls.isAssignableFrom(List.class)){
            System.out.println("List");
        }else if (cls.isAssignableFrom(Set.class)){
            System.out.println("Set");
        }else if (cls.isAssignableFrom(String.class)){
            System.out.println("String");
        }
    }
}
