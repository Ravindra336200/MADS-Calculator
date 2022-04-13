package com.kinfolkstech.madscalculator;

import junit.framework.TestCase;

import org.junit.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class CalculatorActivityTest extends TestCase {

    //Testing the Solve method of Calculator Class
    @Test
    public void testSolve() {
        List<String> testExpressions=new ArrayList<String>();
        testExpressions.add("50+20/10");
        testExpressions.add("50/20+5");
        testExpressions.add("25-2*10");
        testExpressions.add("10/2-20");
        testExpressions.add("10-2-3");
        testExpressions.add("10/2/5");
        testExpressions.add("10/2/4+1");

        List<Integer> actualResult=new ArrayList<Integer>();
        actualResult.add(7);
        actualResult.add(2);
        actualResult.add(5);
        actualResult.add(-15);
        actualResult.add(5);
        actualResult.add(1);
        actualResult.add(1);

        List<Integer> testResult=new ArrayList<Integer>();

        for(String exp:testExpressions)
        {
            int result=CalculatorActivity.solve(exp);
            testResult.add(result);
        }
        assertEquals(actualResult,testResult);

    }
}