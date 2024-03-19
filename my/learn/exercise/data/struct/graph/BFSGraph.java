package com.my.learn.exercise.data.struct.graph;
/*
 * 创建人：baimiao
 * 创建时间：2023/11/3 11:47
 *
 * 类似树的广度遍历，图的广度遍历也需要使用队列数据结构，按照进入队列的顺序FIFO
 */

import com.google.api.client.util.Lists;
import com.google.common.collect.Maps;
import com.wifiin.common.JSON;
import com.wifiin.util.Help;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BFSGraph {
    public static void main(String[] args) {
        DirectedGraph directedGraph = new DirectedGraph();
        /*directedGraph.build("a", "b");
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
        directedGraph.build("h", "i");*/

        directedGraph.build("a", "b");
        directedGraph.build("b", "c");
        directedGraph.build("c", "d");
        directedGraph.build("d", "e");
        directedGraph.build("b", "g");
        directedGraph.build("g", "h");
        directedGraph.build("b", "f");
        directedGraph.build("f", "i");
        directedGraph.build("i", "d");
        List<Integer> result = searchBFS(directedGraph, "a");
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
    // a b d e c h f g i
    // a b c f g d i h e
    private static List<Integer> searchBFS(DirectedGraph directedGraph, String begin) {
        int[][] matrix = directedGraph.getMatrix();
        int size = matrix.length;
        int pos = directedGraph.index(begin);
        List<Integer> queue = Lists.newArrayList();
        List<Integer> visited = Lists.newArrayList();
        visited.add(pos);
        int index = pos;
        while (visited.size() < size) {
            int[] row = matrix[index];
            for (int i = 0; i < size; i++) {
                if (row[i] > 0 && !visited.contains(i)) {
                    queue.add(i);
                }
            }
            if (!queue.isEmpty()) {
                index = queue.remove(0);
            }
            if (!visited.contains(index)) {
                visited.add(index);
            }
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
