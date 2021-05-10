package com.candao.spas.flow.sample.aviator;

import com.candao.spas.flow.jackson.EasyJsonUtils;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.googlecode.aviator.utils.Reflector;
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

        stu stux = new stu();
        pro pro = new pro();
        stux.setAge(20);
        stux.setName("hehe");
        pro.setAge(21);
        stux.setP(pro);

        System.out.println(EasyJsonUtils.toJsonString(stux));


        Expression expression = AviatorEvaluator.compile("p.num != nil && p.num < 100", true);
        System.out.println(expression.getVariableFullNames());
        System.out.println(expression.execute(EasyJsonUtils.toJavaObject(stux,Map.class)));
        System.out.println(Reflector.getProperty(stux,"p.age"));
        /*
        Boolean news3 = (Boolean) AviatorEvaluator.execute("num<100", EasyJsonUtils.toJavaObject(stu,Map.class));
        System.out.println("news3:"+news3);

        String yourname = "jeromeliu";
        Map<String, Object> env = new HashMap<String, Object>();
        env.put("yourname", yourname);
        String result = (String) AviatorEvaluator.execute(" 'hello ' + yourname ", env);
        System.out.println(result);*/

        //Map<String, Object> env2 = new HashMap<String, Object>();
        //env2.put("aa", EasyJsonUtils.toJavaObject(stu,Map.class));
        //System.out.println(EasyJsonUtils.toJsonString(env2));
        /*Boolean news = (Boolean) AviatorEvaluator.execute("p.age>20&&name=='hehe'", EasyJsonUtils.toJavaObject(stux,Map.class));
        System.out.println(news);

        Boolean news2 = (Boolean) AviatorEvaluator.execute("(p.age + age>42) || name=='hhe'", EasyJsonUtils.toJavaObject(stux,Map.class));
        System.out.println(news2);*/
        //Expression#getVariableNames
    } 
}