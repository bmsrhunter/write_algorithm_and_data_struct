package com.my.learn.exercise.data.struct.graph;
/*
 * 创建人：baimiao
 * 创建时间：2023/10/30 10:37
 *
 *
 * https://zhuanlan.zhihu.com/p/139377783
 *
 * prim 算法思路：
 * 1. 初始将所有的顶点 放到未访问的集合中 List(unvisited),先选出任意一个点V0放到List(visited)中，并从List(unvisited)中删除
 * 2.从邻接矩阵中找到V0到List(unvisited)中的所有的距离最小的点V1,将V1放到List(visited) 并从List(unvisited)移除，
 * 3.从邻接矩阵中找到List(visited)中的每个节点到List(unvisited) 中最小的值V2,将V2放到List(visited) 并从List(unvisited)移除，
 * 4.重复步骤3，直到List(visited)包含所有的顶点
 * 因为每次都是未访问的节点到已经访问的节点的距离，所以不会产生环路
 * 没用到边，所以适合稠密图 e(edge)<vertex*(log vertex)
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

public class Prim {
    public static void main(String[] args) {
        Graph graph = new Graph();
        graph.build("v0", "v1", 34);
        graph.build("v0", "v2", 46);
        graph.build("v0", "v5", 19);
        graph.build("v1", "v4", 12);
        graph.build("v5", "v4", 26);
        graph.build("v5", "v2", 25);
        graph.build("v5", "v3", 25);
        graph.build("v2", "v3", 17);
        graph.build("v4", "v3", 38);
        Path path = calculate(graph, "v0");
        System.out.println(JSON.common().toJSON(path));
    }


    private static Path calculate(Graph graph, String chosen) {
        Path path = new Path();
        int[][] matrix = graph.getMatrix();
        List<String> vertexes = graph.getVertexes();
        List<String> visited = Lists.newArrayList();
        visited.add(chosen);
        List<String> uv = Lists.newArrayList(vertexes);
        uv.remove(chosen);
        int count = uv.size();
        for (int i = 0; i < count; i++) {
            Edge edge = new Edge();
            int tmp = Integer.MAX_VALUE;
            String point = null;
            int vs = visited.size();
            for (int j = 0; j < vs; j++) {
                String vp = visited.get(j);
                int uvs = uv.size();
                for (int k = 0; k < uvs; k++) {
                    String uvp = uv.get(k);
                    int val = matrix[graph.index(vp)][graph.index(uvp)];
                    if (val > 0 && val < tmp) {
                        tmp = val;
                        point = uvp;
                        edge.setStart(vp);
                        edge.setEnd(uvp);
                        edge.setWeight(tmp);
                    }
                }
            }
            visited.add(point);
            uv.remove(point);
            path.calculate(tmp);
            path.addEdge(edge);
        }
        path.setVertexes(visited);
        return path;
    }

    private static class Path {
        private List<String> vertexes;
        private int distance;

        private List<Edge> edges;

        public List<Edge> getEdges() {
            return edges;
        }

        public void addEdge(Edge edge) {
            if (edges == null) {
                edges = Lists.newArrayList();
            }
            edges.add(edge);
        }

        public void setVertexes(List<String> vertexes) {
            this.vertexes = vertexes;
        }

        public List<String> getVertexes() {
            return vertexes;
        }

        public int getDistance() {
            return distance;
        }

        public void calculate(int weight) {
            distance += weight;
        }

        public void addPath(String vertex) {
            if (vertexes == null) {
                vertexes = Lists.newArrayList();
            }
            vertexes.add(vertex);
        }
    }


    private static class Graph {
        private List<Edge> edges;
        private List<String> vertexes;

        private int[][] matrix;

        private Map<String, Integer> position = Maps.newHashMap();


        public void build(String start, String end, int weight) {
            if (edges == null) {
                edges = Lists.newArrayList();
            }
            edges.add(new Edge(start, end, weight));
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
                localMatrix[sp][ep] = edge.getWeight();
                localMatrix[ep][sp] = edge.getWeight();
            }
            matrix = localMatrix;
        }
    }


    private static class Edge {
        private String start;
        private String end;
        private int weight;

        public Edge() {
        }

        public Edge(String start, String end, int weight) {
            this.start = start;
            this.end = end;
            this.weight = weight;
        }

        public void setStart(String start) {
            this.start = start;
        }

        public void setEnd(String end) {
            this.end = end;
        }

        public void setWeight(int weight) {
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
