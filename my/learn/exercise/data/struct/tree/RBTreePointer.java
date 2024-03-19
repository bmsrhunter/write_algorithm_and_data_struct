package com.my.learn.exercise.data.struct.tree;
/*
 * 创建人：baimiao
 * 创建时间：2023/8/20 20:28
 *
 */


import java.util.ArrayList;
import java.util.List;

/**
 * 红黑树
 * 性质：
 * 1.节点都是红色或者黑色
 * 2.根节点是黑色的
 * 3.空节点（叶子结点是黑色的）
 * 4.不能出现连续两个红色节点（红色节点的子节点是两个黑色的节点）
 * 5.任一节点到叶子结点的简单路径经过的黑色节点数量都相等
 * <p>
 * 红黑树 最大高度是 2log(n+1)
 * 红黑树两边最大高度差不超过2倍 既 1/2<=[h(left)/h(right)]<=2 ,根据性质5，将每个黑节点前都插入红节点高度变为2倍，依然符合红黑树的定义
 * <p>
 * 添加节点时候 新添加的都是红节点，因为如果是黑节点就不会出现红节点了，
 * <p>
 * 遇到不满足红黑树条件情况下：
 * 1.当前节点的父节点和叔叔节点是否为红色如果都为红色，直接变色（父节点和叔叔节点变为黑色，爷爷节点变为红色），然后以爷爷节点为【新插入节点】继续递归判断,
 * 2.如果当前节点没有叔叔节点或叔叔节点为黑色，那么判断当前节点是LL、LR、RR、RL，
 * 如果是LL或RR，则直接变色（父亲节点变为黑色，爷爷节点变为红色）并右旋或左旋。
 * 如果是LR或RL，则先把它们通过右旋或左旋变换为LL或RR，再进行变色旋转操作。
 * <p>
 * 将node(value=3)与node(value=2)互换， node(origin value=3).left = node(value=1) && node(origin value=3).right=node(origin value=2)
 * 因为一旦node(value=2).right=node(value=3) 需要 node(value=4).left=node(value=2),如果节点只有单向指针就没法实现，双向指针还需要判断是左右节点过于繁琐
 * 实际上上面那种“旋转” 并不需要颜色额外着色
 * 参考链接：
 * https://mp.weixin.qq.com/s?__biz=Mzg5NzMwOTczOQ==&mid=2247484571&idx=1&sn=cbb71572a34b551af4e3bbdd560abb29&chksm=c07289dff70500c9c03e6cd01a6e58256a037e251bcd0f94e64f44ccc8195f25bbad934d965f&scene=27
 */

public class RBTreePointer {

    public static void main(String[] args) {
        int[] values = {8, 1, 5, 9, 6, 4, 7, 2, 3, 11, 10, 12};
        Node root = build(values);
        System.out.println(toStringLot(root));
    }


    private static void dfs(Node node, List<String> list) {
        if (node != null) {
            list.add(node.getValue() + (node.isBlack() ? "b" : "r"));
            dfs(node.getLeft(), list);
            dfs(node.getRight(), list);
        }
    }


    public static String toStringLot(Node root) {
        List<List<String>> list = new ArrayList<>();
        List<Node> nodeList = new ArrayList<>();
        nodeList.add(root);
        do {
            List<String> levelList = bfs(nodeList);
            list.add(levelList);
        } while (nodeList.size() > 0);
        return com.wifiin.common.JSON.common().toJSON(list);
    }

    //层序遍历,用到了队列，先放根节点然后再放左右子节点删除前面的节点
    private static List<String> bfs(List<Node> nodeList) {
        List<String> list = new ArrayList<>();
        int size = nodeList.size();
        for (int i = 0; i < size; i++) {
            Node n = nodeList.get(0);
            list.add(n.getValue() + (n.isBlack() ? "b" : "r") + (n.getParent() == null ? "-root" : ("-parent-" + n.getParent().getValue() + (n.getParent().isBlack() ? "b" : "r"))));
            if (n.getLeft() != null) {
                nodeList.add(n.getLeft());
            }
            if (n.getRight() != null) {
                nodeList.add(n.getRight());
            }
            nodeList.remove(0);
        }
        return list;
    }


    private static Node build(int[] values) {
        Node root = new Node();
        root.setBlack(true);//根节点为黑色
        root.setValue(values[0]);
        root.setParent(null);
        for (int i = 1; i < values.length; i++) {
            Node newNode = insert(root, values[i]);
            adjust(newNode);
        }
        return root;
    }

    private static void adjust(Node node) {//添加节点后就进行调整，插入的新节点都是红节点
        Node parent = node.getParent();
        if (parent.isBlack()) {//父节点黑则不用操作
            return;
        }
        Node grandPa = parent.getParent();
        Node uncle = null;
        if (grandPa.getLeft() != null && grandPa.getRight() != null) {//有叔叔节点
            uncle = (grandPa.getLeft().getValue() == parent.getValue()) ? grandPa.getRight() : grandPa.getLeft();
        }
        if (uncle != null && !uncle.isBlack()) {//红叔
            parent.setBlack(true);
            uncle.setBlack(true);
            if (grandPa.getParent() == null) {//根节点不能为红色
                grandPa.setBlack(true);
            } else {
                grandPa.setBlack(false);
                adjust(grandPa);
            }
        } else {
            int gval = grandPa.getValue();
            int pval = parent.getValue();
            int val = node.getValue();
            if (gval > pval) {
                if (pval > val) {
                    rRotate(grandPa);
                } else {
                    lRotate(parent);
                    rRotate(grandPa);
                }
            } else {
                if (pval < val) {
                    lRotate(grandPa);
                } else {
                    rRotate(parent);
                    lRotate(grandPa);
                }
            }
        }
    }

    //           black
    //       red
    //  red
    private static void rRotate(Node node) {
        Node son = node.getLeft();
        Node grandSon = son.getLeft();
        Node grandSonRight = son.getRight();
        int sval = son.getValue();
        int nodeVal = node.getValue();
        node.setValue(sval);
        node.setLeft(grandSon);
        son.setValue(nodeVal);
        son.setLeft(grandSonRight);
        node.setRight(son);
        if (grandSon != null) {
            grandSon.setParent(node);
        }
    }

    //                    black                                      red
    //               red          black                       black      black
    //           red                                   black
    //    black        black                    black         black
    //red                                  red
    //
    //


    //           black
    //                   red
    //                        red
    private static void lRotate(Node node) {
        Node son = node.getRight();
        Node grandSon = son.getRight();
        Node grandSonLeft = son.getLeft();
        int sval = son.getValue();
        int nodeVal = node.getValue();
        node.setValue(sval);
        node.setRight(grandSon);
        son.setValue(nodeVal);
        son.setRight(grandSonLeft);
        node.setLeft(son);
        if (grandSon != null) {
            grandSon.setParent(node);
        }
    }

    //            black
    //       red
    //             red


    private static Node insert(Node node, int value) {
        int val = node.getValue();
        if (val > value) {//左分支
            Node left = node.getLeft();
            if (left != null) {
                return insert(left, value);
            } else {
                left = new Node(value, false);
                left.setParent(node);
                node.setLeft(left);
                return left;
            }
        } else {
            Node right = node.getRight();
            if (right != null) {
                return insert(right, value);
            } else {
                right = new Node(value, false);
                right.setParent(node);
                node.setRight(right);
                return right;
            }
        }
    }


    public static class Node {
        private int value;
        private boolean black;//red or black
        private Node left;
        private Node right;
        private Node parent;

        public Node() {
        }

        public Node(int val, boolean black) {
            this.value = val;
            this.black = black;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
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

        public Node getParent() {
            return parent;
        }

        public void setParent(Node parent) {
            this.parent = parent;
        }

        public boolean isBlack() {
            return black;
        }

        public void setBlack(boolean black) {
            this.black = black;
        }
    }

    /***
     *                      5b
     *              2b                8b
     *         1b       4b      6b          10r
     *               3r            7r    9b       11b
     *                                                  12r
     *
     *
     *
     * */
}
