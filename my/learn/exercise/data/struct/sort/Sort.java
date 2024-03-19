package com.my.learn.exercise.data.struct.sort;
/*
 * 创建人：baimiao
 * 创建时间：2023/7/26 15:41
 *
 */

import com.wifiin.common.JSON;

/***
 * 排序法    平均时间  	最差情形	         稳定度        	额外空间	        备注
 * 冒泡	    O(n2)       O(n2)	        稳定	            O(1)        	n小时较好
 * 选择	    O(n2)	    O(n2)	        不稳定	        O(1)	        n小时较好
 * 插入   	O(n2)	    O(n2)	        稳定	            O(1)	        大部分已排序时较好
 * 基数	    O(logRB)	O(logRB)    	稳定	            O(n)	        * B是真数(0-9),R是基数(个十百)
 * Shell    O(nlogn)	O(ns) 1<s<2	    不稳定	        O(1)           	s是所选分组
 * 快速	    O(nlogn)	O(n2)	        不稳定	        O(nlogn)	    n大时较好
 * 归并	    O(nlogn)	O(nlogn)	    稳定	            O(1)	        n大时较好
 * 堆	    O(nlogn)	O(nlogn)	    不稳定	        O(1)	        n大时较好
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 * */

public class Sort {
    public static void main(String[] args) {
//        int[] arr = {5, 4, 11, 4, 7, 9, 8, 2, 1, 3, 3};
        int[] arr = {5, 20, 30, 40, 50, 14, 67, 6, 1, 4, 23, 54, 89, 10, 78, 53, 71, 88, 9, 16, 21, 35, 41, 28, 75, 99, 58, 7};
//        int[] arr = {10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
//        int[] arr = {5, 4};
//        heapSort(arr);
        quickSort(arr);
        System.out.println(JSON.common().toJSON(arr));
    }

    /**
     * 堆排序特点，将数组看做一个完全二叉树，最下面是叶子结点
     * 大顶堆：从最后一个【非叶子结点】层开始，用每一个【非叶子结点与其子节点】对比，得出较大的，依次递归，将每次最大的数据与数组最后的
     * 数据交换位置 , 节点的position 是 k 左右讲个子节点是 2k+1,2k+2,节点的父节点是 (int) (k+1)/2
     */

    //堆排序 start
    public static void heapSort(int[] array) {
        int length = array.length;
        for (int i = length - 1; i >= 0; i--) {
            int nodePos = (i + 1) / 2; //非叶子节点坐标
            while (nodePos >= 0) {//每个非叶子结点都要比较
                buildHeap(array, nodePos, i);
                nodePos--;
            }
            compareAndSwap(array, 0, i);//将第一个数据与最后的做交换
        }
    }

    private static void buildHeap(int[] array, int nodePos, int length) {
        int leftPos = 2 * nodePos + 1;
        int rightPos = 2 * nodePos + 2;
        if (leftPos < length) {//防止数组溢出
            compareAndSwap(array, leftPos, nodePos);
        }
        if (rightPos < length) {//防止数组溢出
            compareAndSwap(array, rightPos, nodePos);
        }
    }

    private static void compareAndSwap(int[] array, int pos1, int pos2) {//控制大顶堆还是小顶堆
        int temp;
        if (array[pos1] > array[pos2]) {
            temp = array[pos2];
            array[pos2] = array[pos1];
            array[pos1] = temp;
        }
        /**
         * if (array[pos1] < array[pos2]) {  大顶堆
         *             temp = array[pos2];
         *             array[pos2] = array[pos1];
         *             array[pos1] = temp;
         *         }
         * */
    }

    //堆排序 end

    //quick sort  start

    /***
     * 快排的本质是分治，先找出来一个基准数据，通常是数组第一个 key=array[0],然后用左右开始分别进行对比，小于key的放到
     * 左边大于key的放到右边，当右边有小于key的值出现，游标变换顺序，左边有大于key的值，游标变换顺序，直到left=right
     * 然后以middle为中点,左右两边递归
     *
     * */

    public static void quickSort(int[] array) {
        partition(array, 0, array.length - 1);
    }

    private static void partition(int[] array, int left, int right) {
        int key = array[left];
        int middle;
        boolean rightPart = true;
        int start = left;
        int end = right;
        while (start < end) {
            rightPart = compare(array, key, start, end, rightPart);
            if (rightPart) {
                end--;
            } else {
                start++;
            }
        }
        middle = (start + end) / 2;
        array[middle] = key;//用key填补空余位置，此时key左边都小于等于key,右边都大于key
        if (left < middle - 1) {
            partition(array, left, middle - 1);
        }
        if (right > middle + 1) {
            partition(array, middle + 1, right);
        }
    }

    private static boolean compare(int[] array, int key, int left, int right, boolean dir) {
        if (dir) {
            if (key >= array[right]) {
                array[left] = array[right];
                return false;
            }
            return true;
        } else {
            if (array[left] > key) {
                array[right] = array[left];
                return true;
            }
            return false;
        }


        //quick sort  end

    }
}
