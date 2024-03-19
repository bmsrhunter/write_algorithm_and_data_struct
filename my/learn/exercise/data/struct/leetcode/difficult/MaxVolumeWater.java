package com.my.learn.exercise.data.struct.leetcode.difficult;
/*
 * 创建人：baimiao
 * 创建时间：2024/3/11 10:21
 *
 * 接雨水问题
 *
 * 给定 n 个非负整数表示每个宽度为 1 的柱子的高度图，计算按此排列的柱子，下雨之后能接多少雨水。
 *
 * 输入：height = [0,1,0,2,1,0,1,3,2,1,2,1]
输出：6
解释：上面是由数组 [0,1,0,2,1,0,1,3,2,1,2,1] 表示的高度图，在这种情况下，可以接 6 个单位的雨水（蓝色部分表示雨水）。
示例 2：

输入：height = [4,2,0,3,2,5]
输出：9
 */

import java.util.ArrayList;
import java.util.List;

public class MaxVolumeWater {
    public static void main(String[] args) {
//        int[] height = {0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1};
//        int[] height = {4, 2, 0, 3, 2, 5};
        int[] height = {1, 2, 3, 4, 3, 1};
        int max = totalVolume(height);
        System.out.println(max);
    }

    //分治思想,本质是每个分段的最高点和次高点中较小值 min(a,b)*(|position(a)-position(b)|-1)-sum(中间的各个高度和)
    private static int totalVolume(int[] height) {
        if (height == null || height.length <= 2) {
            return 0;
        }
        int sum = 0;
        List<Integer> result = new ArrayList<>();
        int length = height.length;
        int endPosition = length - 1;
        int maxPosition = findMaxPosition(height, 0, endPosition);
        calculate(height, 0, maxPosition, true, result);
        calculate(height, endPosition, maxPosition, false, result);
        for (int i = 0; i < result.size(); i++) {
            sum += result.get(i);
        }
        return sum;
    }

    private static int findMaxPosition(int[] height, int from, int to) {
        int max = -1;
        int position = from;
        for (int i = from; i <= to; i++) {
            if (max < height[i]) {
                max = height[i];
                position = i;
            }
        }
        return position;
    }


    private static void calculate(int[] height, int endPoint, int maxPoint, boolean left, List<Integer> result) {
        if (endPoint == maxPoint) {
            return;
        }
        int secondPoint;
        if (left) {
            secondPoint = findMaxPosition(height, endPoint, maxPoint - 1);
            if (secondPoint == endPoint) {
                int total = height[secondPoint] * (maxPoint - 1 - secondPoint);
                if (total > 0) {
                    for (int i = secondPoint + 1; i <= maxPoint - 1; i++) {
                        total = total - height[i];
                    }
                    result.add(total);
                }
            } else {
                calculate(height, endPoint, secondPoint, true, result);
                int total = height[secondPoint] * (maxPoint - 1 - secondPoint);
                if (total > 0) {
                    for (int i = secondPoint + 1; i <= maxPoint - 1; i++) {
                        total = total - height[i];
                    }
                    result.add(total);
                }
            }
        } else {
            secondPoint = findMaxPosition(height, maxPoint + 1, endPoint);
            if (secondPoint == endPoint) {
                int total = height[secondPoint] * (secondPoint - 1 - maxPoint);
                if (total > 0) {
                    for (int i = maxPoint + 1; i <= secondPoint - 1; i++) {
                        total = total - height[i];
                    }
                    result.add(total);
                }
            } else {
                int total = height[secondPoint] * (secondPoint - 1 - maxPoint);
                if (total > 0) {
                    for (int i = maxPoint + 1; i <= secondPoint - 1; i++) {
                        total = total - height[i];
                    }
                    result.add(total);
                }
                calculate(height, endPoint, secondPoint, false, result);
            }
        }
    }


}
