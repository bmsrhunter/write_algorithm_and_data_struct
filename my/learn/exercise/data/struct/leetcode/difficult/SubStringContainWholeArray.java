package com.my.learn.exercise.data.struct.leetcode.difficult;
/*
 * 创建人：baimiao
 * 创建时间：2024/3/8 15:17
 * leetcode no.30
 *
 * 给定一个字符串 s 和一个字符串数组 words。 words 中所有字符串 长度相同。

 s 中的 串联子串 是指一个包含  words 中所有字符串以任意顺序排列连接起来的子串。

例如，如果 words = ["ab","cd","ef"]， 那么 "abcdef"， "abefcd"，"cdabef"， "cdefab"，"efabcd"， 和 "efcdab" 都是串联子串。 "acdbef" 不是串联子串，因为他不是任何 words 排列的连接。
返回所有串联子串在 s 中的开始索引。你可以以 任意顺序 返回答案。
*
* 示例 1：

输入：s = "barfoothefoobarman", words = ["foo","bar"]
输出：[0,9]
解释：因为 words.length == 2 同时 words[i].length == 3，连接的子字符串的长度必须为 6。
子串 "barfoo" 开始位置是 0。它是 words 中以 ["bar","foo"] 顺序排列的连接。
子串 "foobar" 开始位置是 9。它是 words 中以 ["foo","bar"] 顺序排列的连接。
输出顺序无关紧要。返回 [9,0] 也是可以的。
示例 2：

输入：s = "wordgoodgoodgoodbestword", words = ["word","good","best","word"]
输出：[]
解释：因为 words.length == 4 并且 words[i].length == 4，所以串联子串的长度必须为 16。
s 中没有子串长度为 16 并且等于 words 的任何顺序排列的连接。
所以我们返回一个空数组。
示例 3：

输入：s = "barfoofoobarthefoobarman", words = ["bar","foo","the"]
输出：[6,9,12]
解释：因为 words.length == 3 并且 words[i].length == 3，所以串联子串的长度必须为 9。
子串 "foobarthe" 开始位置是 6。它是 words 中以 ["foo","bar","the"] 顺序排列的连接。
子串 "barthefoo" 开始位置是 9。它是 words 中以 ["bar","the","foo"] 顺序排列的连接。
子串 "thefoobar" 开始位置是 12。它是 words 中以 ["the","foo","bar"] 顺序排列的连接。

 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubStringContainWholeArray {

    public static void main(String[] args) {
        String s = "bcabbcaabbccacacbabccacaababcbb";
        String[] words = {"c","b","a","c","a","a","a","b","c"};
        List<Integer> list = findSubString(s, words);
        list.forEach(System.out::println);
    }


    //滑块比较
    private static List<Integer> findSubString(String s, String[] words) {
        List<Integer> positionList = new ArrayList<>();
        if (words != null && s != null && s.length() > 0) {
            int arrSize = words.length;
            int perLength = words[0].length();
            int total = arrSize * perLength;
            int strLen = s.length();
            if (strLen < total) {//长度不够包含全部的数组
                return positionList;
            } else {

                Map<String, Integer> origin = new HashMap<>();
                for (int i = 0; i < arrSize; i++) {
                    String cell = words[i];
                    if (origin.containsKey(cell)) {
                        origin.put(cell, origin.get(cell) + 1);
                    } else {
                        origin.put(cell, 1);
                    }
                }

                Map<String, Integer> trace = new HashMap<>();


                for (int k = 0; k <=strLen - total; k++) {
                    int temp = arrSize;
                    for (int i = k; i < strLen; i = i + perLength) {
                        String sub = s.substring(i, i + perLength);
                        if (origin.containsKey(sub) && origin.get(sub) > 0) {
                            origin.put(sub, origin.get(sub) - 1);
                            Integer val = trace.get(sub);
                            if (val == null) {
                                trace.put(sub, 1);
                            } else {
                                trace.put(sub, val + 1);
                            }
                            temp--;
                            if (temp == 0) {
                                positionList.add(k);
                                trace.forEach((tk, tv) -> origin.put(tk, origin.get(tk) + tv));
                                trace.clear();
                                break;
                            }
                        } else {
                            trace.forEach((tk, tv) -> origin.put(tk, origin.get(tk) + tv));
                            trace.clear();
                            break;
                        }
                    }
                }
            }
        }
        return positionList;
    }


}
