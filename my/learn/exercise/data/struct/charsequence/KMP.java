package com.my.learn.exercise.data.struct.charsequence;
/*
 * 创建人：baimiao
 * 创建时间：2023/8/14 9:44
 *
 */

import com.wifiin.common.JSON;

import java.util.ArrayList;
import java.util.List;

public class KMP {
    public static void main(String[] args) {
//        String a = "rfserhyjxfhyjtyuetfsdghhyjxfhyu";
//        String pattern = "hyjxfhy";
        String a =       "aaaaaaaaaaaab";
        String pattern =     "aaaaaaaab";
        System.out.println("next 数组：" + JSON.common().toJSON(next(pattern)));
        //ababcac
        //  abca
        //[-1,0,0,1]
        System.out.println(JSON.common().toJSON(compare(a, pattern)));
    }


    private static List<Integer> compare(String origin, String pattern) {
        int count=0;
        List<Integer> list = new ArrayList<>();
        if (pattern == null || origin == null || origin.length() < pattern.length()) {
            return null;
        }
        int next[] = next(pattern);
        int oriIndex = 0;
        int patternIndex = 0;
        int length = origin.length();
        int pl = pattern.length();
        while (oriIndex < length) {
            if (patternIndex == -1) {
                oriIndex++;
                patternIndex = 0;
                continue;
            }
            System.out.println("第"+(++count)+"次比较");
            if (origin.charAt(oriIndex) == pattern.charAt(patternIndex)) {
                oriIndex++;
                patternIndex++;
            } else {
                patternIndex = next[patternIndex];
                continue;
            }
            if (patternIndex == pl) {
                list.add(oriIndex-patternIndex);
                patternIndex=0;
            }
        }
        return list;
    }


    //next数组，寻找模式串的从第一个字符到 length-1长度的每一个子串的 前缀和后缀 相等的 字串长度
    //eg: ababc ==> {a,ab,abc,abab}==>{0,0,0,2}
    //其中abab先是 前缀 aba 后缀 bab ,不匹配然后 各自回退一位 前缀ab 后缀ab相等 得到next值是2
    private static int[] next(String pattern) {
        int length = pattern.length();
        int next[] = new int[length];
        next[0] = -1;
        String tmp;
        for (int i = length - 1; i > 0; i--) {
            tmp = pattern.substring(0, i);
            next[i] = maxFactor(tmp, 1);
        }
        return next;
    }

    private static int maxFactor(String chars, int step) {
        int length = chars.length();
        if (length == step) {
            return 0;
        }
        String prefix = chars.substring(0, length - step);
        String suffix = chars.substring(step, length);
        if (prefix.equals(suffix)) {
            return length - step;
        } else {
            return maxFactor(chars, ++step);
        }
    }

}
