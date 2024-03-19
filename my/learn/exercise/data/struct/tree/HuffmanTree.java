package com.my.learn.exercise.data.struct.tree;
/*
 * 创建人：baimiao
 * 创建时间：2023/8/3 17:32
 *
 */


import com.google.api.client.util.Lists;
import com.google.common.collect.Maps;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 哈夫曼树是编码和压缩，解压缩领域的重要算法，思想是假设 有叶子结点 T={t1,t2...tn} 他们的权重分别为 W={w1,w2..wn}
 * 叶子节点到根的路径为 L= {l1,l2...ln} ,求 所有叶子节点和路径的乘机的和的最小值
 * 从叶子节点中取出权重最小的两个叶子，将他们相加 得到新的节点作为父节点， 将新节点放到叶子结点中，并且删除前面的两个叶子结点，直到最后剩下一个叶子节点
 * 最典型的应用是压缩与解压缩
 */

public class HuffmanTree {

    public static void main(String[] args) {
        String letters = "ABCDADACBAEAFGBGDCEAECDABABEGCACACDEGFACA";
        List<String> list = Lists.newArrayList();
        for (Character c : letters.toCharArray()) {
            list.add(String.valueOf(c));
        }
        List<Node> nodeList = parseLetters(list);
        if (nodeList != null) {
            Node node = buildTree(nodeList);
            StringBuilder sb = new StringBuilder();
            Map<String, String> mapping = collect(node);
            list.forEach(c -> sb.append(mapping.get(c)));
            System.out.println("source:");
            System.out.println(letters);
            System.out.println("encode:");
            System.out.println(sb);
            System.out.println("decode:");
            String decode = decode(sb.toString(), mapping);
            System.out.println(decode);
        }
    }


    private static String decode(String encodedStr, Map<String, String> mapping) {
        Map<String, String> reverse = Maps.newHashMap();
        mapping.forEach((k, v) -> reverse.put(v, k));
        List<Integer> sortedSteps = mapping.values().stream().map(a -> a.length()).distinct().sorted().collect(Collectors.toList());
        int min = sortedSteps.get(0);
        int max = sortedSteps.get(sortedSteps.size() - 1);
        int index = 0;
        String tmp;
        String val;
        StringBuilder sb = new StringBuilder();
        int length = encodedStr.length();
        while (index < length - 1) {
            for (int i = min; i <= max; i++) {
                tmp = encodedStr.substring(index, index + i);
                val = reverse.get(tmp);
                if (val != null) {
                    sb.append(val);
                    index = index + i;
                    break;
                }
            }
        }
        return sb.toString();
    }

    /**
     * 每个字符出现的次数当做权重，构成叶子结点
     */
    private static List<Node> parseLetters(List<String> letters) {
        if (letters == null || letters.isEmpty()) {
            return null;
        }
        Map<String, Integer> map = Maps.newHashMap();
        for (String c : letters) {
            map.compute(c, (a, b) -> {
                if (b == null) {
                    return 1;
                } else {
                    return b + 1;
                }
            });
        }
        List<Node> nodeList = Lists.newArrayList();
        map.forEach((k, v) -> {
            Node node = new Node(v);
            node.setElement(k);
            nodeList.add(node);
        });
        return nodeList;
    }


    private static Node buildTree(List<Node> nodeList) {
        while (nodeList.size() > 1) {
            Node left = nodeList.stream().min(Comparator.comparing(Node::getWeight)).get();
            nodeList.remove(left);
            Node right = nodeList.stream().min(Comparator.comparing(Node::getWeight)).get();
            nodeList.remove(right);
            int sum = left.getWeight() + right.getWeight();
            Node father = new Node(sum);
            father.setLeft(left);
            father.setRight(right);
            nodeList.add(father);
        }
        return nodeList.get(0);
    }

    private static void encode(Node node, String pc, String val) {
        if (node == null) {
            return;
        }
        Node left = node.getLeft();
        Node right = node.getRight();
        if (pc == null) {
            left.setCode("0");
            right.setCode("1");
        } else {
            node.setCode(pc + val);
        }
        String fatherCode = node.getCode();
        encode(left, fatherCode, "0");
        encode(right, fatherCode, "1");
    }

    private static Map<String, String> collect(Node node) {
        encode(node, null, null);
        Map<String, String> map = Maps.newHashMap();
        write(node, map);
        return map;
    }

    private static void write(Node node, Map<String, String> map) {
        if (node != null) {
            if (node.getElement() != null) {//叶子结点才是需要的
                map.put(node.getElement(), node.getCode());
            }
            write(node.getLeft(), map);
            write(node.getRight(), map);
        }
    }


    public static class Node {
        private int weight;//权重

        private String code;//编码

        private String element;//源值  A,B,C...

        private Node left;
        private Node right;

        public Node(int weight) {
            this.weight = weight;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getElement() {
            return element;
        }

        public void setElement(String element) {
            this.element = element;
        }

        public Node getLeft() {
            return left;
        }

        public void setLeft(Node left) {
            this.left = left;
        }

        public Node getRight() {
            return right;
        }

        public void setRight(Node right) {
            this.right = right;
        }
    }

}
