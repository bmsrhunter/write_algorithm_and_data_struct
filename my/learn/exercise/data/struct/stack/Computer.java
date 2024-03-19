package com.my.learn.exercise.data.struct.stack;
/*
 * 创建人：baimiao
 * 创建时间：2023/7/21 10:00
 *
 */

/**
 *
 * 计算器：两个栈，一个操作数栈，一个操作符栈，将运算符优先级排序 ，一个操作符后面遇到比他低的就进行运算，"("直接入栈，遇到
 * “)” 进行运算，直至遇到“(”
 *
 *
 * */

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

public class Computer {
    private Stack<Integer> numStack = new Stack<>();
    private Stack<String> operatorStack = new Stack<>();

    private final String eof = "\n";

    private Pattern pattern = Pattern.compile("\\d");

    //  * /
    //  + -
    //  (

    public boolean prior(String operator1, String operator2) {
        if (operator1.equals("+") || operator1.equals("-")) {
            if (operator2.equals("(")) {
                return true;
            }
        } else if (operator1.equals("*") || operator1.equals("/")) {
            if (operator2.equals("*") || operator2.equals("/")) {
                return false;
            }
            return true;
        }
        return false;
    }

    private int compute(int val1, int val2, String operator) {
        switch (operator) {
            case "+":
                return val1 + val2;
            case "-":
                return val2 - val1;
            case "*":
                return val1 * val2;
            case "/":
                return val2 / val1;
            default:
                return 0;
        }
    }

    private int execute(String expression) {
        String[] chars = split(expression);
        for (String c : chars) {
            if (pattern.matcher(c).matches()) {
                numStack.add(Integer.valueOf(c));
            } else {
                operate(c);
            }
        }
        operate(eof);
        return numStack.pop();
    }

    private void operate(String c) {
        if (operatorStack.empty() || "(".equals(c)) {
            operatorStack.add(c);
        } else {
            if (")".equals(c)) {
                while (!"(".equals(operatorStack.peek())) {
                    if (numStack.size() > 1) {
                        numStack.add(compute(numStack.pop(), numStack.pop(), operatorStack.pop()));
                    }
                }
                operatorStack.pop();
            } else {
                if (prior(c, operatorStack.peek())) {
                    operatorStack.add(c);
                } else {
                    numStack.add(compute(numStack.pop(), numStack.pop(), operatorStack.pop()));
                    if (eof.equals(c)) {
                        if (!operatorStack.isEmpty()) {
                            if (!prior(c, operatorStack.peek())) {
                                numStack.add(compute(numStack.pop(), numStack.pop(), operatorStack.pop()));
                            }
                        }
                        operatorStack.add(c);
                    } else {
                        while (!operatorStack.isEmpty()) {
                            numStack.add(compute(numStack.pop(), numStack.pop(), operatorStack.pop()));
                        }
                    }
                }
            }
        }
    }

    private String[] split(String expression) {
        expression = expression.replaceAll("\\s|\\t", "");
        List<String> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (char c : expression.toCharArray()) {
            String sc = String.valueOf(c);
            if (pattern.matcher(sc).matches()) {
                sb.append(c);
            } else {
                if (sb.length() > 0) {
                    list.add(sb.toString());
                    sb.setLength(0);
                }
                list.add(sc);
            }
        }
        if (sb.length() > 0) {
            list.add(sb.toString());
        }
        return list.toArray(new String[0]);
    }

    public static void main(String[] args) {
        String expression = "1 + 2 * 3 + 5 * ( 1 + 3 - ((1) * 3) ) - ( 4 + 5 ) * 6 / 3 + 8 + ( ( 2 + 3 ) - 4 ) * 2 - 1 * 5";
//        String expression = "1 + 2 * 3 + 5 * ( 1 + 3 - 1 * 3 ) - ( 4 + 5 ) * 6 / 3 + 8";
//        String expression = "( ( 2 + 3 ) - 4 )  * 2 - 1 *5";
        Computer computer = new Computer();
        System.out.println(computer.execute(expression));
    }
}
