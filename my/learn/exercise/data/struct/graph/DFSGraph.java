package com.my.learn.exercise.data.struct.graph;
/*
 * 创建人：baimiao
 * 创建时间：2023/11/2 10:08
 * 深度优先搜索图,深度优先核心使用的数据结构就是栈，对未访问的节点压栈操作，访问过的就弹出栈
 *
 *
 */

import com.google.api.client.util.Lists;
import com.google.common.collect.Maps;
import com.wifiin.common.JSON;
import com.wifiin.util.Help;

import java.util.*;
import java.util.stream.Collectors;

public class DFSGraph {

    public static void main(String[] args) {
        DirectedGraph directedGraph = new DirectedGraph();
        directedGraph.build("a", "b");
        directedGraph.build("a", "d");
        directedGraph.build("a", "e");
        directedGraph.build("b", "c");
        directedGraph.build("b", "e");
        directedGraph.build("c", "e");
        directedGraph.build("c", "g");
        directedGraph.build("d", "h");
        directedGraph.build("e", "d");
        directedGraph.build("e", "f");
        directedGraph.build("e", "h");
        directedGraph.build("f", "g");
        directedGraph.build("f", "i");
        directedGraph.build("g", "i");
        directedGraph.build("h", "f");
        directedGraph.build("h", "i");
        /*directedGraph.build("a", "b");
        directedGraph.build("b", "c");
        directedGraph.build("c", "d");
        directedGraph.build("d", "e");
        directedGraph.build("b", "g");
        directedGraph.build("g", "h");
        directedGraph.build("b", "f");
        directedGraph.build("f", "i");
        directedGraph.build("i", "d");*/
        List<Integer> result = searchDFS(directedGraph, "a");
        System.out.println(JSON.common().toJSON(result));
    }
    // 0 1 2 3 4 5 6 7 8
    // a b c d e f g h i
    //     g->h
    //     |
    //  a->b->c->d->e
    //     |     |
    //     f->   i

    // a e h i f g d b c
    //回溯算法：将 顶点的相邻顶点分别压入栈中，取出栈顶元素继续遍历，直到遇见没有相邻元素的回退到栈顶元素
    private static List<Integer> searchDFS(DirectedGraph directedGraph, String begin) {
        int[][] matrix = directedGraph.getMatrix();
        int size = matrix.length;
        int pos = directedGraph.index(begin);
        Stack<Integer> stack = new Stack<>();
        List<Integer> visited = Lists.newArrayList();
        visited.add(pos);
        int index = pos;
        boolean hasNext = false;
        while (visited.size() < size) {
            int[] row = matrix[index];
            for (int i = 0; i < size; i++) {
                if (row[i] > 0 && !visited.contains(i)) {
                    hasNext = true;
                    stack.push(i);
                }
            }
            if (!visited.contains(index)) {
                visited.add(index);
            }
            if (!hasNext) {
                if (!stack.isEmpty()) {
                    index = stack.pop();
                }
            } else {
                index = stack.peek();
            }
            hasNext = false;
        }
        return visited;
    }


    private static class DirectedGraph {
        private List<Edge> edges;
        private List<String> vertexes;
        private int[][] matrix;

        private Map<String, Integer> position = Maps.newHashMap();


        public void build(String start, String end) {
            if (edges == null) {
                edges = Lists.newArrayList();
            }
            edges.add(new Edge(start, end));
        }


        public List<String> getVertexes() {
            if (Help.isEmpty(vertexes)) {
                vertexes = allVertex();
            }
            return vertexes;
        }

        private List<String> allVertex() {
            Set<String> set = new HashSet<>();
            if (Help.isNotEmpty(edges)) {
                edges.forEach(e -> {
                    set.add(e.getStart());
                    set.add(e.getEnd());
                });
            }
            List<String> sorted = set.stream().sorted().collect(Collectors.toList());
            for (int i = 0; i < sorted.size(); i++) {
                String val = sorted.get(i);
                position.put(val, i);
            }
            return sorted;
        }

        public int[][] getMatrix() {
            if (matrix == null) {
                buildMatrix();
            }
            return matrix;
        }

        public int index(String vertex) {
            return position.get(vertex);
        }


        private void buildMatrix() {
            List<String> vxs = getVertexes();
            int size = vxs.size();
            int[][] localMatrix = new int[size][size];
            int count = edges.size();
            for (int i = 0; i < count; i++) {
                Edge edge = edges.get(i);
                String start = edge.getStart();
                String end = edge.getEnd();
                int sp = position.get(start);
                int ep = position.get(end);
                localMatrix[sp][ep] = 1;
            }
            matrix = localMatrix;
        }
    }


    private static class Edge {
        private String start;
        private String end;
        private int weight;

        public Edge(String start, String end) {
            this.start = start;
            this.end = end;
        }

        public Edge(String start, String end, int weight) {
            this.start = start;
            this.end = end;
            this.weight = weight;
        }


        public String getStart() {
            return start;
        }

        public String getEnd() {
            return end;
        }

        public int getWeight() {
            return weight;
        }
    }
}
