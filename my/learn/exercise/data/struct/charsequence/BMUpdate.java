package com.my.learn.exercise.data.struct.charsequence;
/*
 * 创建人：baimiao
 * 创建时间：2023/8/16 12:04
 *
 */

import com.alibaba.fastjson.JSON;
import com.google.api.client.util.Lists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 字符串匹配算法，grep 就是用BM实现的
 * 算法说明：从模式串最右开始逐【字符】匹配，
 * 1.最右一位不配用坏字符方式，在模式串中找到坏字符，然后将模式串中的【坏字符】与主串【坏字符】对齐；最右一位匹配则进行下一步
 * 2.向左匹配直到出现不相等的字符 ，两个方法①：继续用上一步的算法得出移动步数，②用匹配好的好后缀在模式串的其他部分匹配找到好后缀的子串
 * 将子串与好后缀对齐，算出移动步数
 * 3.将2步骤的两个值比较，得出较大的值，移动响应的步数，继续从 步骤一开始，直到匹配完成
 * 参考链接：https://blog.csdn.net/weixin_49561445/article/details/117427001
 *         https://blog.csdn.net/weixin_45616285/article/details/128198762?utm_medium=distribute.pc_relevant.none-task-blog-2~default~baidujs_baidulandingword~default-0-128198762-blog-117427001.235^v38^pc_relevant_default_base&spm=1001.2101.3001.4242.1&utm_relevant_index=3
 * abcdefabcdefbbccabcdeaabc
 * cabcde
 */

public class BMUpdate {
    private static int steps = 0;

    private static Map<Character, List<Integer>> map = new HashMap<>();
    private static Node[] nodes;


    //[17,60,93,126,167,207]
    public static void main(String[] args) {
        String text = "HERE IS A SIMPLE EXAMPLE HERE IS A SIMPLE EXAMPLR HERE IS A EXAMPLE EXASPLE HERE IS A SIMPLE EXAMPLE SIMPLE EXAMPLR HERE IS A EXAMPLE EXASPLE SIMPLE EXAMPLR HERE IS A EXAMPLE EXASPLE" +
                "SIMPLE EXAMPLR HERE IS A EXAMPLE EXASPLE";
        String pattern = "EXAMPLE";
//        String text = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab";
//        String pattern = "aaaaaaab";
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
        if ((text == null || text.length() == 0) || (pattern == null || pattern.length() == 0) || (text.length() < pattern.length())) {
            return list;
        }
        charPosition(pattern);
        nodes = goodSuffix(pattern);
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
                    index += maxStep(textChar, i, goodSuffixCount);
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


    //charAt 坏字符与模式串匹配的位置
    //  ccsdacdffdfgsfdsdfsf
    //     ftrdefdfg
    //         ↑
    private static int maxStep(char badChar, int badCharAt, int goodSufCharCount) {
        //坏字符移动几步
        int bcStep = 0;
        List<Integer> positions = map.get(badChar);
        if (positions != null) {
            int size = positions.size();
            for (int i = 0; i < size; i++) {
                steps++;
                int cur = positions.get(i);
                if (badCharAt > cur) {
                    bcStep = badCharAt - cur;
                    break;
                }
            }
        }
        if (bcStep == 0) {//charAt左边不存在这个badChar了或者本身就没有badChar,向右
            bcStep = 1;
        }


        int goodSufStep = 0;
        if (goodSufCharCount > 0) {
            goodSufStep = nodes[goodSufCharCount - 1].getStep();
        }
        steps++;
        return bcStep > goodSufStep ? bcStep : goodSufStep;
    }


    private static void charPosition(String pattern) {
        int length = pattern.length();
        for (int i = length - 1; i >= 0; i--) {
            Character c = pattern.charAt(i);
            int pos = i;
            map.compute(c, (a, b) -> {
                if (b == null) {
                    List<Integer> list = new ArrayList<>();
                    list.add(pos);
                    return list;
                }
                b.add(pos);
                return b;
            });
        }
    }

    // case 1: ==|=u=|===|=u=|=======|=u=| 包含多个好后缀 找到第一个
    // case 2: =v=|============|=u-v=|=v=|  包含部分好后缀，且从头开始 找到开头的部分 abcd 部分后缀 d,cd,bcd
    // case 3: ======================|=u=|  不包含任何后缀 直接整体后移

    //大于最大的好后缀的使用最大的 cbarcbaccba 最大的好后缀是
    //根据map得出最右的元素的坐标 然后第一个指针从最右开始，第二个指针从第二个开始依次向左开始匹配

    //                 xxxxxxxxxxxxbbbbcxxxxxxacccxssa
//                                         ↑__↑   ↑__↑
    private static Node[] goodSuffix(String pattern) {
        int length = pattern.length();
        Node[] nodes = new Node[length - 1];
        int leftPointer;
        int rightPointer;
        char lastChar = pattern.charAt(length - 1);
        List<Integer> lastPos = map.get(lastChar);
        int goodSuffixCharCount = 0;
        Loop:
        for (int i = 1; i < lastPos.size(); i++) {
            int pos = lastPos.get(i);//左指针的锚点
            leftPointer = pos;
            rightPointer = length - 1;
            while (leftPointer >= 0) {
                int counter = length - rightPointer;//好后缀字符数量
                steps++;
                if (pattern.charAt(rightPointer) == pattern.charAt(leftPointer)) {
                    if (counter > goodSuffixCharCount) {//前面没有匹配到
                        goodSuffixCharCount++;
                        Node node;
                        if (leftPointer == 0) {
                            node = new Node(rightPointer - leftPointer, true);
                            nodes[counter - 1] = node;
                            break Loop;
                        }
                        node = new Node(rightPointer - leftPointer, false);
                        nodes[counter - 1] = node;
                    } else {
                        if (leftPointer == 0) {
                            nodes[goodSuffixCharCount] = new Node(rightPointer - leftPointer, true);
                        }
                    }
                    if (rightPointer - 1 == pos) { //首尾重合了
                        break;
                    } else {
                        rightPointer--;
                        leftPointer--;
                    }
                } else { //没有匹配下一个起始点再重新匹配
                    break;
                }
            }
        }
        Node end = new Node(length, false);
        for (int i = 0; i < length - 1; i++) {
            Node node = nodes[i];
            if (node != null) {
                boolean head = node.isHead();
                if (head) {
                    end = node;
                }
            } else {
                nodes[i] = end;
            }
        }
        return nodes;
    }

    private static class Node {
        private int step;
        private boolean head;

        public Node(int step, boolean head) {
            this.step = step;
            this.head = head;
        }


        public int getStep() {
            return step;
        }

        public void setStep(int step) {
            this.step = step;
        }

        public boolean isHead() {
            return head;
        }

        public void setHead(boolean head) {
            this.head = head;
        }
    }

}
