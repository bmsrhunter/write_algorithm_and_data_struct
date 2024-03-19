package com.my.learn.exercise.data.struct.leetcode.medium;
/*
 * 创建人：baimiao
 * 创建时间：2024/2/1 17:30
 *
 * leetcode no.3
 */

//最长字符串没有重复字符的长度，数字，字母，下划线
//1. anchor:和minRight 是一对，是左侧的锚点
//2.cursor 是游标从第一个往后一直游走遇到有重复的字符和minRight 较小给minRight同时anchor=cursor,否则cursor++
//3.minRight==cursor说明cursor(含)前面都是无重复的字符，赋值给maxLen,anchor++,cursor++
//4.minRight-cursor=1,anchor=cursor,cursor++
public class LongestSubStrWithoutRepeat {
    public static void main(String[] args) {
//        String str = "aasdbfwerddddddsaweta1823424rabv";
        String str = "abcdadrbxyxui";
        int maxSubStrLen = longestStrWithoutRepeat(str);
        System.out.println(maxSubStrLen);
    }


    //遇到重复的就一直查下去，直到遇到不重复的
    private static int longestStrWithoutRepeat(String str) {
        if (str == null || "".equals(str)) {
            return 0;
        }
        char[] chars = str.toCharArray();
        int length = chars.length;
        if (length == 1) {
            return 1;
        }
        int maxLen = 0;

        int anchor = 0;//锚点
        int cursor = 0;//游标

        int left = length;//和前面字符相比最左的位置

        int temp = 0;


        while (cursor < length - 1) {

            for (int i = cursor + 1; i < left; i++) {
                if (chars[cursor] == chars[i]) {
                    if (left > i) {
                        left = i;
                        temp = cursor;
                    }
                    break;
                }
            }

            cursor++;

            if (cursor == left) {
                maxLen = maxLen > (cursor - anchor) ? maxLen : (cursor - anchor);
                anchor = temp + 1;
                cursor = anchor;
                left = length;
            }
        }
        maxLen = maxLen > (length - anchor) ? maxLen : (length - anchor);
        return maxLen;
    }


}
