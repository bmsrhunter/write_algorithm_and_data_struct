package com.my.learn.exercise.data.struct.charsequence;
/*
 * 创建人：baimiao
 * 创建时间：2023/8/16 12:04
 *
 */

import com.alibaba.fastjson.JSON;
import com.google.api.client.util.Lists;

import java.util.List;

/**
 * 字符串匹配算法，grep 就是用BM实现的
 * 算法说明：从模式串最右开始逐【字符】匹配，
 * 1.最右一位不配用坏字符方式，在模式串中找到坏字符，然后将模式串中的【坏字符】与主串【坏字符】对齐；最右一位匹配则进行下一步
 * 2.向左匹配直到出现不相等的字符 ，两个方法①：继续用上一步的算法得出移动步数，②用匹配好的好后缀在模式串的其他部分匹配找到好后缀的子串
 * 将子串与好后缀对齐，算出移动步数
 * 3.将2步骤的两个值比较，得出较大的值，移动响应的步数，继续从 步骤一开始，直到匹配完成
 * 参考链接：https://blog.csdn.net/weixin_49561445/article/details/117427001
 * abcdefabcdefbbccabcdeaabc
 * cabcde
 */

public class BM {
    private static int steps = 0;

    public static void main(String[] args) {
        String text = "HERE IS A SIMPLE EXAMPLE HERE IS A SIMPLE EXAMPLR HERE IS A EXAMPLE EXASPLE HERE IS A SIMPLE EXAMPLE SIMPLE EXAMPLR HERE IS A EXAMPLE EXASPLE SIMPLE EXAMPLR HERE IS A EXAMPLE EXASPLE" +
                "SIMPLE EXAMPLR HERE IS A EXAMPLE EXASPLE";
        String pattern = "EXAMPLE";
//        String text = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab";
//        String pattern = "baaaaaaa";
        List<Integer> result = compare(text, pattern);
        if (!result.isEmpty()) {
            System.out.println(JSON.toJSON(result));
        } else {
            System.out.println("没有匹配的");
        }
        System.out.println("共对比" + steps + "次");
    }

    private static List<Integer> compare(String text, String pattern) {
        List<Integer> list = Lists.newArrayList();
        if ((text == null || text.length() == 0) || (pattern == null || pattern.length() == 0)) {
            return list;
        }
        int textLen = text.length();
        int patternLen = pattern.length();
        int index = 0;//移动多少step
        //  abcdfgtrhabc
        // index ==> abc
        while (index <= textLen - patternLen) {
            int goodSuffixCount = 0;
            for (int i = patternLen - 1; i >= 0; i--) {
                char textChar = text.charAt(index + i);
                steps++;
                if (pattern.charAt(i) != textChar) {
                    index += maxStep(pattern, textChar, goodSuffixCount);
                    break;
                } else {
                    goodSuffixCount++;
                    if (goodSuffixCount == patternLen) {
                        list.add(index);
                        index++;//向右移动一位 继续匹配
                        break;
                    }
                }
            }
        }
        return list;
    }

    private static int maxStep(String pattern, char badChar, int goodSufCharCount) {
        int length = pattern.length();
        int pos = length - 1 - goodSufCharCount;
        //坏字符移动几步
        int rc = rightChar(pattern, pos, badChar);
        int ss = 0;
        if (goodSufCharCount > 0) {
            ss = maxSubGoodSuffixInPrefix(pattern, goodSufCharCount);
        }
        return rc > ss ? rc : ss;
    }

    private static int rightChar(String pattern, int pos, char badChar) {
        int i = 0;
        while (pos >= 0) {
            steps++;
            if (pattern.charAt(pos) == badChar) {
                break;
            } else {
                pos--;
                i++;
            }
        }
        return i;
    }

    // case 1: ==|=u=|===|=u=|=======|=u=| 包含多个好后缀 找到第一个
    // case 2: =v=|============|=u-v=|=v=|  包含部分好后缀，且从头开始 找到开头的部分 abcd 部分后缀 d,cd,bcd
    // case 3: ======================|=u=|  不包含任何后缀 直接整体后移
    //abc
    //好后缀需要移动的步数
    private static int maxSubGoodSuffixInPrefix(String pattern, int goodSuffixCharCount) {
        int length = pattern.length();
        int start = length - goodSuffixCharCount;
        String goodSuffix = pattern.substring(start, length);
        String prefix = pattern.substring(0, start);
        int index = start - 1;
        int maxGoodLen = 0;
        Loop:
        while (index >= 0) {
            steps++;
            for (int i = goodSuffixCharCount - 1; i >= 0 && index >= 0; i--) {
                if (goodSuffix.charAt(i) == prefix.charAt(index)) {
                    maxGoodLen++;
                    if (index == 0) {
                        break Loop; //case 2
                    }
                    index--;
                } else {
                    index--;
                    maxGoodLen = 0;//遇到不匹配的 重置
                    break;
                }
                if (maxGoodLen == goodSuffixCharCount) {//case 1
                    index++;
                    break Loop;
                }
            }
        }
        //计算出向右移动多少步
        return length - (index + maxGoodLen);
    }
}
