package com.my.learn.exercise.data.struct.leetcode.difficult;
/*
 * 创建人：baimiao
 * 创建时间：2024/3/18 9:46
 *
 *
 *
  84. 柱状图中最大的矩形
  给定 n 个非负整数，用来表示柱状图中各个柱子的高度。每个柱子彼此相邻，且宽度为 1 。
  求在该柱状图中，能够勾勒出来的矩形的最大面积。
 例1：
 输入：heights = [2,1,5,6,2,3]
 输出：10
 解释：最大的矩形为图中红色区域，面积为 10

 例2：
 输入： heights = [2,4]
 输出： 4


提示：

1 <= heights.length <=105
0 <= heights[i] <= 104


思想：找出最小值，然后找出所有包含最小值的面积，然后依次包含次小值的。。。。


 */

public class LargestRectangleArea {
    public static void main(String[] args) {
//        int[] arr = {2, 1, 5, 6, 2, 3};
        int[] arr = {0,1,0,1,0,0,1,1,0};
//        int[] arr = {0, 2, 0};
        LargestRectangleArea la = new LargestRectangleArea();
        int area = la.largestArea(arr);
        System.out.println(area);
    }

    private int largestArea(int[] heights) {
        if (heights.length == 1) {
            return heights[0];
        }
        int left = 0;
        int right = heights.length - 1;
        int minPosition = findMinPosition(heights, left, right);
        Box box = new Box();
        box.setValue(heights[minPosition] * (heights.length));
        if (heights[minPosition] == findMax(heights, left, right)) {
            //一样高
        } else {
            compareAndSet(heights, left, minPosition, box);
            compareAndSet(heights, right, minPosition, box);
        }
        return box.getValue();
    }

    private void compareAndSet(int[] heights, int endPoint, int minPoint, Box box) {
        if (endPoint == minPoint) {
            return;
        }
        boolean left = endPoint < minPoint;
        if (left) {
            int localMin = findMinPosition(heights, endPoint, minPoint - 1);
            box.setValue(heights[localMin] * (minPoint - endPoint));
            if (heights[localMin] ==heights[endPoint]) {
                for (int i = endPoint; i <= minPoint; i++) {
                    int step = 0;
                    for (int j = i; j <= minPoint; j++) {
                        if (heights[i] <= heights[j]) {
                            step++;
                        } else {
                            break;
                        }
                    }
                    for (int j = i - 1; j >= endPoint; j--) {
                        if (heights[i] <= heights[j]) {
                            step++;
                        } else {
                            break;
                        }
                    }
                    int temp = heights[i] * step;
                    box.setValue(temp);
                }
            } else {
                compareAndSet(heights, endPoint, localMin, box);
                compareAndSet(heights, minPoint, localMin, box);
            }
        } else {
            int localMin = findMinPosition(heights, minPoint + 1, endPoint);
            box.setValue(heights[localMin] * (endPoint - minPoint));
            if (heights[localMin] == heights[endPoint]) {
                for (int i = minPoint; i <= endPoint; i++) {
                    int step = 0;
                    for (int j = i; j <= endPoint; j++) {
                        if (heights[i] <= heights[j]) {
                            step++;
                        } else {
                            break;
                        }
                    }
                    for (int j = i - 1; j >= minPoint; j--) {
                        if (heights[i] <= heights[j]) {
                            step++;
                        } else {
                            break;
                        }
                    }
                    int temp = heights[i] * step;
                    box.setValue(temp);
                }
            } else {
                compareAndSet(heights, minPoint, localMin, box);
                compareAndSet(heights, endPoint, localMin, box);
            }
        }

    }


    private int findMinPosition(int[] heights, int start, int end) {
        int min = Integer.MAX_VALUE;
        int pos = 0;
        for (int i = start; i <= end; i++) {
            if (heights[i] <= min) {
                min = heights[i];
                pos = i;
            }
        }
        return pos;
    }

    private int findMax(int[] heights, int start, int end) {
        int max = -1;
        for (int i = start; i <= end; i++) {
            if (heights[i] > max) {
                max = heights[i];
            }
        }
        return max;
    }

    class Box {
        private int value;

        public int getValue() {
            return value;
        }

        public void setValue(int max) {
            value = value > max ? value : max;
        }
    }
}
