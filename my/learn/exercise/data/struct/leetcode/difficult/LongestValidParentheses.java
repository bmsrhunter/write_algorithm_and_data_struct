package com.my.learn.exercise.data.struct.leetcode.difficult;
/*
 * 创建人：baimiao
 * 创建时间：2024/3/9 20:36
 *
 * 给你一个只包含 '(' 和 ')' 的字符串，找出最长有效（格式正确且连续）括号
子串
的长度。



示例 1：

输入：s = "(()"
输出：2
解释：最长有效括号子串是 "()"
示例 2：

输入：s = ")()())"
输出：4
解释：最长有效括号子串是 "()()"
示例 3：

输入：s = ""
输出：0


提示：

0 <= s.length <= 3 * 104
s[i] 为 '(' 或 ')'
 *
 */

import java.util.Stack;

public class LongestValidParentheses {
    public static void main(String[] args) {
        String s = "(((()))))()()";
        int result = longestValidParentheses(s);
        System.out.println(result);
    }

    //( 作为1
    //0如果与（匹配放到栈中就是2，否则就是0 作为终止符号
    //>=2 的数值直接合并
//核心是栈的使用
    public static int longestValidParentheses(String s) {
        if (s == null || s.length() < 2) {
            return 0;
        }
        int length = s.length();
        char[] character = s.toCharArray();
        int max = 0;
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < length; i++) {
            Character c = character[i];
            if (c.equals('(')) {
                stack.push(1);
            } else {
                if (!stack.isEmpty()) {
                    int val = stack.pop();
                    if (val == 1) {
                        val = 2;
                        max = max > val ? max : val;
                        if (!stack.isEmpty()) {
                            int top = stack.peek();
                            if (top <= 1) {
                                stack.push(val);
                            } else {
                                int sum = stack.pop() + val;
                                max = max > sum ? max : sum;
                                stack.push(sum);
                            }
                        } else {
                            stack.push(val);
                        }
                    } else if (val == 0) {
                        stack.push(0);
                    } else {//()(()[)]
                        if (stack.isEmpty()) {
                            stack.push(val);
                            stack.push(0);
                        } else {
                            int top = stack.pop();
                            if (top == 1) {
                                int value = val + 2;
                                max = max > value ? max : value;
                                if (!stack.isEmpty()) {
                                    int tp = stack.peek();
                                    if (tp > 1) {
                                        int tvl = stack.pop() + value;
                                        max = max > tvl ? max : tvl;
                                        stack.push(tvl);
                                    } else {
                                        stack.push(value);
                                    }
                                } else {
                                    stack.push(value);
                                }
                            } else if (top == 0) {
                                stack.push(val);
                                stack.push(0);
                            } else {

                            }
                        }
                    }
                } else {
                    stack.push(0);//终止符号
                }
            }
        }
        return max;
    }
}
