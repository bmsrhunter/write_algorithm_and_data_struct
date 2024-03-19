package com.my.learn.exercise.data.struct.chaos;
/*
 * 创建人：baimiao
 * 创建时间：2023/7/28 17:00
 *
 */


public class Recursion {

    public static void main(String[] args) {
//        int val = fibonacci(9);
//        System.out.println(val);
        hanoi(3, "源", "目标", "辅助");
    }


    /**
     * 最经典的递归，需要注意的是递归需要有出口，不能造成无限递归
     */
    private static int fibonacci(int val) {
        if (val < 1) {
            return 0;
        }
        if (val == 1) {
            return 1;
        } else if (val == 2) {
            return 1;
        } else {
            return fibonacci(val - 1) + fibonacci(val - 2);
        }
    }

    /**
     * hanoi 塔问题也是经典递归，中心思想是分治，将问题拆分，本质上是 n个盘子，先将 n-1个移动到 辅助盘子上，然后将第n个移动到目标上，最后将n-1个移动到目标柱子上
     */

    private static int count = 0;

    private static void hanoi(int number, String source, String target, String auxiliary) {
        if (number == 1) {
            count++;//实际移动时候操作加1
            System.out.println("第" + count + "次：" + source + "-->" + target);
        } else {
            hanoi(number - 1, source, auxiliary, target);
            count++;
            System.out.println("第" + count + "次：" + source + "-->" + target);
            hanoi(number - 1, auxiliary, target, source);
        }
    }

}
