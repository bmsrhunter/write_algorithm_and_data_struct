package com.my.learn.exercise.data.struct.leetcode.difficult;
/*
 * 创建人：baimiao
 * 创建时间：2024/3/15 15:03
 *
 * 4. 寻找两个正序数组的中位数
困难
相关标签
相关企业
给定两个大小分别为 m 和 n 的正序（从小到大）数组 nums1 和 nums2。请你找出并返回这两个正序数组的 中位数 。

算法的时间复杂度应该为 O(log (m+n)) 。

* 时间复杂度是O(log (m+n)) 可以想到是二分法查找，假设将两个数组合并，就是一个部分有序的数组，用最小值和最大值的平均数当成分界点，左右夹逼


示例 1：

输入：nums1 = [1,3], nums2 = [2]
输出：2.00000
解释：合并数组 = [1,2,3] ，中位数 2
示例 2：

输入：nums1 = [1,2], nums2 = [3,4]
输出：2.50000
解释：合并数组 = [1,2,3,4] ，中位数 (2 + 3) / 2 = 2.5
*
* nums1.length == m
nums2.length == n
0 <= m <= 1000
0 <= n <= 1000
1 <= m + n <= 2000
-106 <= nums1[i], nums2[i] <= 106
 *
 */

import java.util.Arrays;

public class TwoArrayMiddle {

    public static void main(String[] args) {
//        int[] m = {2, 4, 6, 8, 11, 7, 43, 6, 1};//0 1 2 2  2 4 5 6 6 7 8 9 11 33 43 61
//        int[] n = {9, 33, 61, 2, 0, 5, 2};
        int[] m = {1,3};
        int[] n = {2};
        Arrays.sort(m);
        Arrays.sort(n);
        double middle = findMiddlePosition(m, n);
        System.out.println(middle);
    }

//{-1,0,0,0,0,1}

    private static double findMiddlePosition(int[] m, int[] n) {
        int mLen = m.length;
        int nLen = n.length;
        if (mLen > 0 && nLen > 0) {
            int r = Math.max(m[mLen - 1], n[nLen - 1]);
            int l = Math.min(m[0], n[0]);
            int k = (mLen + nLen) / 2;
            boolean mLarge = m.length >= n.length;
            boolean only = (nLen + mLen) % 2 > 0;
            if (only) {
                while (l < r) {
                    int middle = (int) Math.floor((l + r) / 2.0);
                    if (count(m, n, middle, mLarge) > k) {
                        r = middle;
                    } else {
                        l = middle + 1;
                    }
                }
                return l;
            } else {
                int left1 = l;
                int r1 = r;
                while (left1 < r1) {
                    int middle = (int) (Math.floor((left1 + r1) / 2.0));
                    if (count(m, n, middle, mLarge) >= k) {
                        r1 = middle;
                    } else {
                        left1 = middle + 1;
                    }
                }
                int left2 = left1;
                int r2 = r;
                while (left2 < r2) {
                    int middle = (int) (Math.floor((left2 + r2) / 2.0));
                    if (count(m, n, middle, mLarge) >= k + 1) {
                        r2 = middle;
                    } else {
                        left2 = middle + 1;
                    }
                }
                return (left1 + left2) / 2.0;
            }

        } else if (mLen > 0) {
            double middle;
            if (mLen % 2 == 0) {
                middle = (m[mLen / 2] + m[mLen / 2 - 1]) / 2.0;
            } else {
                middle = m[mLen / 2];
            }
            return middle;
        } else {
            double middle;
            if (nLen % 2 == 0) {
                middle = (n[nLen / 2] + n[nLen / 2 - 1]) / 2.0;
            } else {
                middle = n[nLen / 2];
            }
            return middle;
        }
    }

    private static int count(int[] m, int[] n, int val, boolean mLarge) {
        int count = 0;
        if (mLarge) {
            for (int i = 0; i < n.length; i++) {
                if (m[i] <= val) {
                    count++;
                }
                if (n[i] <= val) {
                    count++;
                }
            }
            for (int i = m.length - 1; i >= n.length; i--) {
                if (m[i] <= val) {
                    count++;
                }
            }
        } else {
            for (int i = 0; i < m.length; i++) {
                if (m[i] <= val) {
                    count++;
                }
                if (n[i] <= val) {
                    count++;
                }
            }
            for (int i = n.length - 1; i >= m.length; i--) {
                if (n[i] <= val) {
                    count++;
                }
            }
        }
        return count;
    }
}
