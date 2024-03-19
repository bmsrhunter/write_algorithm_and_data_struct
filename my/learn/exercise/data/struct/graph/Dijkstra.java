package com.my.learn.exercise.data.struct.graph;
/*
 * 创建人：baimiao
 * 创建时间：2023/10/17 21:31
 *
 *https://zhuanlan.zhihu.com/p/478202932
 *
 * * reference:  https://blog.csdn.net/weixin_43682721/article/details/87867946
 */

import com.google.api.client.util.Lists;
import com.google.common.collect.Maps;
import com.wifiin.common.JSON;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Dijkstra {


    public static void main(String[] args) {
        Graph graph = new Graph();
        graph.draw("a", "b", 6);
        graph.draw("a", "c", 3);
        graph.draw("b", "c", 2);
        graph.draw("b", "d", 5);
        graph.draw("c", "d", 3);
        graph.draw("d", "f", 3);
        graph.draw("d", "e", 2);
        graph.draw("c", "e", 4);
        graph.draw("e", "f", 5);
        graph.setVertex("b");
        List<Route> result = calculate(graph);
        result.forEach(System.out::println);
    }



    /***
     * a.初始时，S只包含源点，即S＝{v}，v到源点的距离为0。U包含除v外的其他顶点，即:U={其余顶点}，若v与U中顶点u为邻接结点，则<u,v>有权值，若u不是v的邻接结点，则<u,v>权值为∞。
     *
     * b.从U中选取一个距离源点v最小的顶点k，把k加入S中（该选定的距离就是v到k的最短路径长度）。
     *
     * c.以k为新考虑的中间点，修改U中各顶点的距离：若从源点v 经过顶点k 到顶点u的距离 比 原来标记的距离短，则修改顶点u的距离值为顶点k上标记的距离加上<k,v>的权。
     *
     * d.重复步骤b和c直到所有顶点都包含在S中。
     *
     *时间复杂度 O(n^2)
     */
    private static List<Route> calculate(Graph graph) {
        List<Route> source = Lists.newArrayList();
        List<Route> unprocessed = graph.getUnprocessed();
        int[][] matrix = graph.matrix();
        //position
        Route first = shortestInUnprocessed(unprocessed);
        source.add(first);
        unprocessed.remove(first);

        for (int j = 0; j < source.size(); j++) {
            Route r = source.get(j);
            for (int i = 0; i < unprocessed.size(); i++) {
                Route route = unprocessed.get(i);
                int oriDis = route.getDistance();
                int dis = matrix[r.getIndex()][route.getIndex()];
                if (dis > 0) {//否则不可达
                    int sum = r.getDistance() + dis;
                    if (sum < oriDis) {
                        route.setDistance(sum);
                        route.attach(r.getPath());
                    }
                }
            }
            Route shortest = shortestInUnprocessed(unprocessed);
            if (shortest != null) {
                source.add(shortest);
                unprocessed.remove(shortest);
            }
        }
        return source;
    }


    private static Route shortestInUnprocessed(List<Route> unprocessed) {
        int min = Integer.MAX_VALUE;
        Route tmp = null;
        for (int i = 0; i < unprocessed.size(); i++) {
            Route route = unprocessed.get(i);
            int dis = route.getDistance();
            if (dis < min) {
                min = dis;
                tmp = route;
            }
        }
        return tmp;
    }


    private static class Route {
        private String oriVertex;//起始点
        private String vertex;//顶点名称
        private int index;//在顶点列表中的位置
        private int distance;//距离起始点距离
        private List<String> path;//走过的路径

        private List<String> attach = Lists.newArrayList();//中间路径


        public void setOriVertex(String oriVertex) {
            this.oriVertex = oriVertex;
        }

        public Route(int index, int distance) {
            this.index = index;
            this.distance = distance;
        }

        public int getIndex() {
            return index;
        }

        public int getDistance() {
            return distance;
        }

        public void setDistance(int distance) {
            this.distance = distance;
        }

        public List<String> getPath() {
            if (path != null) {
                return path;
            } else {
                path = Lists.newArrayList();
                if (attach.isEmpty()) {
                    path.add(oriVertex);
                } else {
                    path.addAll(attach);
                }
                path.add(vertex);
            }
            return path;
        }

        public void setVertex(String vertex) {
            this.vertex = vertex;
        }

        public void attach(List<String> list) {
            attach.clear();
            attach.addAll(list);
        }

        @Override
        public String toString() {
            return "Route:" + JSON.common().toJSON(getPath()) + ":" + getDistance();
        }
    }


    private static class Graph {
        private List<Edge> edges;
        private String vertex;//顶点
        private String[] allVertex;
        private List<Route> unprocessed;

        private int[][] matrix;


        public List<Route> getUnprocessed() {
            if (unprocessed == null) {
                String chosen = getVertex();
                String[] vertexes = vertexList();
                int len = vertexes.length;
                int vertexPos = 0;
                for (int i = 0; i < len; i++) {
                    if (vertexes[i].equals(chosen)) {
                        vertexPos = i;
                        break;
                    }
                }
                unprocessed = Lists.newArrayList();
                int[][] matrix = matrix();
                for (int i = 0; i < vertexes.length; i++) {
                    if (i != vertexPos) {
                        int weight = matrix[vertexPos][i];
                        if (weight == 0) {
                            weight = Integer.MAX_VALUE;
                        }
                        Route route = new Route(i, weight);
                        route.setOriVertex(chosen);
                        route.setVertex(vertexes[i]);
                        unprocessed.add(route);
                    }
                }
            }
            return unprocessed;
        }

        public void setVertex(String vertex) {
            this.vertex = vertex;
        }

        public String getVertex() {
            return vertex;
        }

        public void draw(String begin, String end, int weight) {
            if (edges == null) {
                edges = Lists.newArrayList();
            }
            edges.add(new Edge(begin, end, weight));
        }

        public String[] vertexList() {
            if (allVertex == null) {
                Set<String> set = new HashSet<>();
                edges.forEach(e -> {
                    set.add(e.getBegin());
                    set.add(e.getEnd());
                });
                allVertex = set.stream().sorted().toArray(String[]::new);
            }
            return allVertex;
        }

        public int[][] matrix() {
            if (matrix == null) {
                Map<String, Integer> position = Maps.newHashMap();
                String[] vertexes = vertexList();
                int size = vertexes.length;
                for (int i = 0; i < size; i++) {
                    position.put(vertexes[i], i);
                }
                matrix = new int[size][size];
                for (Edge edge : edges) {
                    int beginPos = position.get(edge.getBegin());
                    int endPos = position.get(edge.getEnd());
                    int weight = edge.getWeight();
                    matrix[beginPos][endPos] = weight;
                    matrix[endPos][beginPos] = weight;
                }
            }
            return matrix;
        }
    }


    private static class Edge {
        private String begin;
        private String end;
        private int weight;

        public Edge(String begin, String end, int weight) {
            this.begin = begin;
            this.end = end;
            this.weight = weight;
        }

        public String getBegin() {
            return begin;
        }

        public String getEnd() {
            return end;
        }

        public int getWeight() {
            return weight;
        }
    }
}
