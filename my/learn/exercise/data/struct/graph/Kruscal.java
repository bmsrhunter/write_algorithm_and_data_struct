package com.my.learn.exercise.data.struct.graph;
/*
 * 创建人：baimiao
 * 创建时间：2023/10/30 19:25
 *
 *
 * kruscal思想：
 * 1.将所有的边按照权重小顶堆排序
 * 2.依次选出最小的边添加进边集合中
 * 3.如果新加入的边构成回路则舍弃，执行步骤2，直到  边数量=顶点数量-1
 *
 * find union 算法：
 * 新加入顶点（u,v）
 * 从集合{S}中遍历 每个点集合 S中查找顶点 u/v 如果（u,v）不存任一S中则向{S}中添加新的集合S'
 * 如果某一S中包含（u,v）则有环存在
 * 如果S包含一个点u 其他集合{S} 不包含v 将点添加进S中
 * 如果S包含u,另一S'包含v 则将 S和S'合并成新的点集S，并将S'从{S}中移除
 * 有上述条件得到的{S}可知，如果{S}里只有一个元素则图是全连通，{S}集合元素数量等于联通分量
 *
 *
 *
 * 将 边按照权重进行小顶堆排序，（克鲁斯卡尔的时间复杂度都体现在排序上）
 * 边的两个顶点判断是否在一个访问集合中，List<Set(visited)>
  1.集合中的某个set包含了两个顶点则有边，
  2.集合中所有set不包含 新加入边的两个顶点 则给集合添加新的set(包含新的边的顶点)
  3.集合中某个set包含一个顶点，去后面的set中继续寻找，如果包含另一个节点则合并两个set,遍历完所有集合后没有找到包含另外节点的set则加入到包含第一个节点的set
 *
 * https://zhuanlan.zhihu.com/p/86642615
 *
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


/***
 *            v1      v4      (12)
 *
 *            v2      v3       (17)
 *
 *            v0      v5      (19)
 *
 *            v5      v2       (25)
 *
 *            v5      v4        (25)
 *
 *
 *
 * */

public class Kruscal {


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
        Path path = calculate(graph);
        System.out.println(JSON.common().toJSON(path));
    }


    private static Path calculate(Graph graph) {
        List<Edge> edges = graph.sortedEdge();
        List<String> vertexes = graph.getVertexes();

        Map<Integer, Integer> vDegree = Maps.newHashMap();//for 根据节点的度来判断是否有环

        List<Set<String>> visited = Lists.newArrayList();
        int originEdges = edges.size();
        int vertexSize = vertexes.size();
        //n 个节点n-1条边
        int treeEdge = 0;
        Path path = new Path();
        for (int i = 0; i < originEdges; i++) {
            if (treeEdge < vertexSize - 1) {
                Edge edge = edges.get(i);
                String start = edge.getStart();
                String end = edge.getEnd();
                boolean hasCircle = circle(visited, start, end);
                if (!hasCircle) {
                    treeEdge++;
                    path.addEdge(edge);
                    path.calculate(edge.getWeight());
                }
            } else {
                break;
            }
        }
        return path;
    }


    //find/union
    private static boolean circle(List<Set<String>> visited, String start, String end) {
        if (visited.isEmpty()) {
            Set<String> set = new HashSet<>();
            set.add(start);
            set.add(end);
            visited.add(set);
        } else {
            int flag = 0;
            int index = -1;
            for (int i = 0; i < visited.size(); i++) {
                Set<String> item = visited.get(i);
                if (item.contains(start) && item.contains(end)) {
                    return true;
                } else if (!item.contains(start) && !item.contains(end)) {
                    if (flag == 0 && i == visited.size() - 1) {
                        Set<String> set = new HashSet<>();
                        set.add(start);
                        set.add(end);
                        visited.add(set);
                        return false;
                    }
                } else {//combine
                    flag++;
                    if (flag > 1) {
                        item.addAll(visited.remove(index));
                        return false;
                    }
                    index = i;
                }
            }

            Set<String> s = visited.get(index);
            s.add(start);
            s.add(end);
        }
        return false;
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

        public List<Edge> sortedEdge() {
            heapSort(edges);
            return edges;
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


    private static void heapSort(List<Edge> array) {
        int length = array.size();
        for (int i = length - 1; i >= 0; i--) {
            int nodePos = (i + 1) / 2; //非叶子节点坐标
            while (nodePos >= 0) {//每个非叶子结点都要比较
                buildHeap(array, nodePos, i);
                nodePos--;
            }
            compareAndSwap(array, 0, i);//将第一个数据与最后的做交换
        }
    }

    private static void buildHeap(List<Edge> array, int nodePos, int length) {
        int leftPos = 2 * nodePos + 1;
        int rightPos = 2 * nodePos + 2;
        if (leftPos < length) {//防止数组溢出
            compareAndSwap(array, leftPos, nodePos);
        }
        if (rightPos < length) {//防止数组溢出
            compareAndSwap(array, rightPos, nodePos);
        }
    }

    private static void compareAndSwap(List<Edge> array, int pos1, int pos2) {//控制大顶堆还是小顶堆
        Edge temp;
        if (array.get(pos1).getWeight() > array.get(pos2).getWeight()) {
            temp = array.get(pos2);
            array.set(pos2, array.get(pos1));
            array.set(pos1, temp);
        }
        /**
         * if (array[pos1] < array[pos2]) {  大顶堆
         *             temp = array[pos2];
         *             array[pos2] = array[pos1];
         *             array[pos1] = temp;
         *         }
         * */
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
