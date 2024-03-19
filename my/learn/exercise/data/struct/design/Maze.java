package com.my.learn.exercise.data.struct.design;
/*
 * 创建人：baimiao
 * 创建时间：2023/11/1 17:23
 * 回溯算法：
 *
 *
 */

import com.google.api.client.util.Lists;

import java.util.List;

public class Maze {
    public static void main(String[] args) {
        //10行 12列 入口
        //0 可达，1不可达
        //入口(0,0) 出口(9,11)
        //要求：求所有的路径中最短的一条
        int[][] maze =
                {
                        {0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0},
                        {0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0},
                        {0, 1, 0, 1, 1, 1, 1, 0, 1, 0, 1, 0},
                        {0, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0},
                        {0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0},
                        {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
                        {0, 0, 0, 1, 1, 1, 0, 1, 0, 1, 1, 0},
                        {0, 1, 0, 1, 0, 1, 1, 0, 1, 0, 0, 0},
                        {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
                        {0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0}
                };

        explore(maze, new int[]{0, 0}, new int[]{9, 11});

    }

    private static List<String> explore(int[][] maze, int[] entrance, int[] exit) {
        List<String> list = Lists.newArrayList();

        //todo

        return list;
    }
}
