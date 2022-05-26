package com.atguigu.gmall.product;

import com.netflix.ribbon.template.TemplateParsingException;
import org.apache.catalina.core.StandardContext;
import org.junit.jupiter.api.Test;
import org.springframework.expression.Expression;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Arrays;

//自定义表达式
public class ExpressTest {

    @Test
   void expressTest(){
        //1.准备一个自定义表达式解析器
        SpelExpressionParser parser = new SpelExpressionParser();

//        String spelStr = "hell0:#{1+1}";
        String spelStr = "sku:detail:#{#args[0]}";

        //默认以 ${ 开头,} 结尾
        TemplateParserContext parserContext = new TemplateParserContext();
        //获取表达式对象
        Expression expression = parser.parseExpression(spelStr, parserContext);

        StandardEvaluationContext context = new StandardEvaluationContext();

        //从上下文所有变量去找
        context.setVariable("args", Arrays.asList(11,22,33,44,55));


        String value = expression.getValue(context, String.class);

        System.out.println("value = " + value);
    }
}
