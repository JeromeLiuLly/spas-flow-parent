package com.candao.spas.flow.sample.aviator;

import com.candao.spas.flow.core.utils.EasyJsonUtils;
import com.googlecode.aviator.AviatorEvaluator;
import lombok.Getter;
import lombok.Setter;


import java.util.HashMap;
import java.util.Map;

public class SayHello {

    @Getter
    @Setter
    static class stu {
        private Integer age;
        private String name;
        private pro p;

    }

    @Getter
    @Setter
    static class pro {
        private Integer age;


    }


    public static void main(String[] args) {
        String yourname = "jeromeliu";
        Map<String, Object> env = new HashMap<String, Object>();
        env.put("yourname", yourname); 
        String result = (String) AviatorEvaluator.execute(" 'hello ' + yourname ", env);
        System.out.println(result);

        stu stu = new stu();
        pro pro = new pro();
        stu.setAge(20);
        stu.setName("hehe");
        pro.setAge(21);
        stu.setP(pro);
        Map<String, Object> env2 = new HashMap<String, Object>();
        env2.put("aa", EasyJsonUtils.toJavaObject(stu,Map.class));
        System.out.println(EasyJsonUtils.toJsonString(env2));
        Boolean news = (Boolean) AviatorEvaluator.execute("p.age>20&&name=='hehe'", EasyJsonUtils.toJavaObject(stu,Map.class));
        System.out.println(news);

        Boolean news2 = (Boolean) AviatorEvaluator.execute("(p.age + age>42) || name=='hhe'", EasyJsonUtils.toJavaObject(stu,Map.class));
        System.out.println(news2);
    } 
}