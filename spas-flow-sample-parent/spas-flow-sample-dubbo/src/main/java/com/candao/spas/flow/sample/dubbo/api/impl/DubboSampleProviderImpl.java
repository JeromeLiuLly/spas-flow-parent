package com.candao.spas.flow.sample.dubbo.api.impl;

import com.candao.spas.flow.sample.dubbo.api.DubboSampleProvider;
import com.candao.spas.flow.sample.dubbo.bean.Student;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@DubboService(timeout = 10 * 3000)
public class DubboSampleProviderImpl implements DubboSampleProvider {

    private  static  Map<String,Student> map = new HashMap<>();

    static {
        Student student = new Student();
        student.setSn("1140");
        student.setName("刘练源");

        Student student02 = new Student();
        student02.setSn("1141");
        student02.setName("刘小小");

        Student student03 = new Student();
        student03.setSn("1142");
        student03.setName("刘哒哒");

        map.put(student.getSn(),student);
        map.put(student02.getSn(),student02);
        map.put(student03.getSn(),student03);
    }

    @Override
    public Student getStudent() {
        return map.get("1140");
    }

    @Override
    public Student getStudentById(String Id) {
        log.info("请求参数："+Id);
        return map.get(Id);
    }
}
