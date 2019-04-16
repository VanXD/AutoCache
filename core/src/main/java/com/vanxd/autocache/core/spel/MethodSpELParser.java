package com.vanxd.autocache.core.spel;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 *
 */
public class MethodSpELParser {
    private Method method;
    private Object[] args;
    private String expressionStr;
    private Expression expression;
    private ExpressionParser parser = new SpelExpressionParser();
    private StandardEvaluationContext context = new StandardEvaluationContext();


    public MethodSpELParser(String expressionStr, Method method, Object[] args) {
        this.expressionStr = expressionStr;
        this.expression = parser.parseExpression(expressionStr);
        Parameter[] parameters = method.getParameters();
        for (int i = 0;i < parameters.length; i++) {
            String name = parameters[i].getName();
            Object arg = args[i];
            context.setVariable(name, arg);
        }
    }

    public Object getValue() {
        return expression.getValue(context);
    }
}
