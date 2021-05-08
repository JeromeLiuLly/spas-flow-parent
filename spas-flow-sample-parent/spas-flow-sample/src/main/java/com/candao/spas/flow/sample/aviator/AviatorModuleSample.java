package com.candao.spas.flow.sample.aviator;

import com.googlecode.aviator.AviatorEvaluator;

public class AviatorModuleSample {

    public static void main(String[] args)  {
        String moduleClass = "" +
                "package com.candao.spas.flow.sample.aviator;\n" +
                "import com.googlecode.aviator.annotation.Import;\n" +
                "@Import(ns = \"str\")\n" +
                "  public static class StringModule2 {\n" +
                "    public static boolean isBlank(final String s) {\n" +
                "      return s == null || s.trim().length() == 0;\n" +
                "    }\n" +
                "  }";

        try {


            //Class<?> instanceClass = Class.forName("com.candao.spas.flow.sample.aviator.StringModule2").getResource(moduleClass);

            AviatorEvaluator.getInstance().addModule(StringModule.class);

            String script = "let str = require('str'); str.isBlank(s) ";

            System.out.println(AviatorEvaluator.execute(script, AviatorEvaluator.newEnv("s", "hello")));
            System.out.println(AviatorEvaluator.execute(script, AviatorEvaluator.newEnv("s", " ")));
            System.out.println(AviatorEvaluator.execute(script, AviatorEvaluator.newEnv("s", null)));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
