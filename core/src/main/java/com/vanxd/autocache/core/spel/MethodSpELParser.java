package com.vanxd.autocache.core.spel;

import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;

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
    private final static DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();


    public MethodSpELParser(String expressionStr, Method method, Object[] args) {
        this.expressionStr = expressionStr;
        this.expression = parser.parseExpression(expressionStr);
        String[] parameterNames = nameDiscoverer.getParameterNames(method);
        if (null != parameterNames && parameterNames.length != 0) {
            for (int i = 0;i < parameterNames.length; i++) {
                String name = parameterNames[i];
                Object arg = args[i];
                context.setVariable(name, arg);
            }
        }
    }

    public Object getValue() {
        return expression.getValue(context);
    }
}
