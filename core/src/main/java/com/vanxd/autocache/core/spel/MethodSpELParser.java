package com.vanxd.autocache.core.spel;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 *
 */
public class MethodSpELParser {
    private String expressionStr;
    private Expression expression;
    private ExpressionParser parser = new SpelExpressionParser();
    private StandardEvaluationContext context = new StandardEvaluationContext();


    public MethodSpELParser(String expressionStr) {
        this.expressionStr = expressionStr;
        this.expression = parser.parseExpression(expressionStr);
        context.setVariable("name", "123");
    }

    public String getValue() {
        return (String) expression.getValue(context);
    }
}
