package com.candao.spas.flow.core.utils;

import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public final class SpleUtils {

    private volatile static SpleUtils instance = null;
    final ExpressionParser parser;

    private SpleUtils(){
        this.parser = new SpelExpressionParser();
    }

    public static SpleUtils getInstance() {
        if (instance == null) {
            synchronized (SpleUtils.class) {
                if (instance == null) {
                    instance = new SpleUtils();
                }
            }
        }
        return instance;
    }

    /**
     * @param expr spel表达式
     * @param contextValue 校验对象
     *
     * */
    public static Object eval(String expr, Object contextValue) {
        StandardEvaluationContext context = new StandardEvaluationContext(contextValue);
        //配置MapAccessor,支持map格式获取
        context.addPropertyAccessor(new MapAccessor());

        return getInstance().parser.parseExpression(expr).getValue(context);
    }

    public static void evalSet(String expr, Object contextValue, Object value) {
        StandardEvaluationContext conetxt = new StandardEvaluationContext(contextValue);
        getInstance().parser.parseExpression(expr).setValue(conetxt, value);
    }
}
