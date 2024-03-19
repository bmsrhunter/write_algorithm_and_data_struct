package com.my.learn.exercise.data.struct.tree;
/*
 * 创建人：baimiao
 * 创建时间：2023/7/24 10:35
 *
 */

/**
 * 二叉搜索树，需要动态平衡
 * 需要理解左旋，右旋
 * 参考：https://blog.csdn.net/jarvan5/article/details/112428036
 * https://zhuanlan.zhihu.com/p/477341905?utm_id=0
 */

import com.google.common.collect.Lists;
import com.wifiin.common.JSON;

import java.util.ArrayList;
import java.util.List;

/**
 * 二叉平衡树的构建，节点尽可能使用少的数据，最主要的是添加节点前后需要注意平衡因子的变化，以及旋转后平衡因子的变化
 */
public class AVLTree {


    private Node root;

    public static class Node {
        private int value;

        private int diff;//节点的平衡因子
        private Node left;
        private Node right;

        public Node(int value) {
            this.value = value;
        }

        public int getDiff() {
            return diff;
        }

        public void setDiff(int diff) {
            this.diff = diff;
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
    }

    public AVLTree(int[] array) {
        init(array);
    }

    public void init(int[] values) {
        for (int val : values) {
            if (root == null) {
                root = new Node(val);//添加根节点
            } else {
                insert(root, val); //插入节点
            }
        }
    }


    //java是引用传递，所以node节点不能指向其他，需要通过交换值来实现旋转，右旋包括左节点有右分支的情况 4,2,1,3 右旋后就是 2,1,4,3
    private Node rRotate(Node node) {
        Node left = node.getLeft();
        Node leftright = left.getRight();
        Node left2 = left.getLeft();
        Node right = node.getRight();
        int nv = node.getValue();
        int lv = left.getValue();
        node.setValue(lv);
        Node newRight = new Node(nv);
        newRight.setLeft(leftright);
        newRight.setRight(right);
        node.setRight(newRight);
        node.setLeft(left2);
        if (left2 == null) {
            node.setDiff(-1);
        } else {
            node.setDiff(0);
        }
        return node;
    }

    private Node lRotate(Node node) {
        Node right = node.getRight();
        Node rightleft = right.getLeft();
        Node right2 = right.getRight();
        Node left = node.getLeft();
        int nv = node.getValue();
        int rv = right.getValue();
        node.setValue(rv);
        Node newLeft = new Node(nv);
        newLeft.setRight(rightleft);
        newLeft.setLeft(left);
        node.setLeft(newLeft);
        node.setRight(right2);
        if (right2 == null) {
            node.setDiff(1);
        } else {
            node.setDiff(0);
        }
        return node;
    }

    private Node lRRotate(Node node) {
        Node left = node.getLeft();
        lRotate(left);
        rRotate(node);
        return node;
    }

    private Node rLRotate(Node node) {
        Node right = node.getRight();
        rRotate(right);
        lRotate(node);
        return node;
    }

    public boolean insert(Node node, int val) {
        boolean left = true;
        if (node.getValue() > val) { //左边插入
            Node ln = node.getLeft();
            if (ln == null) {
                node.setLeft(new Node(val));
                node.setDiff(node.getDiff() + 1);
            } else {
                int ldf = ln.getDiff();
                left = insert(ln, val);
                if (ln.getDiff() == 0 || ldf == ln.getDiff()) {//添加节点后造成子节点不平衡然后旋转了或者添加节点不影响高度

                } else {
                    node.setDiff(node.getDiff() + 1);
                }
            }
        } else {    //右边插入
            Node rn = node.getRight();
            if (rn == null) {
                node.setRight(new Node(val));
                node.setDiff(node.getDiff() - 1);
                left = false;
            } else {
                int rdf = rn.getDiff();
                left = insert(rn, val);
                if (rn.getDiff() == 0 || rdf == rn.getDiff()) {//添加节点后造成子节点不平衡然后旋转了或者添加节点不影响高度

                } else {
                    node.setDiff(node.getDiff() - 1);
                }
            }
        }
        rotate(node, left);
        return left;
    }

    //                    5
    //             3                8
    //          2     4      6          9
    //       1                   7
    //
    //
    //
    //
    //

    private static void delete(Node node, int value) {

    }

    public void rotate(Node node, boolean left) {//旋转
        int diff = node.getDiff();
        if (diff > 1) {
            if (left) {
                rRotate(node);
            } else {
                lRRotate(node);
            }
        } else if (diff < -1) {
            if (!left) {
                lRotate(node);
            } else {
                rLRotate(node);
            }
        }
    }


    @Override
    public String toString() {
        List<Integer> list = Lists.newArrayList();
        print(list, root);
        return JSON.common().toJSON(list);
    }

    public String toStringLot() {
        List<List<Integer>> list = new ArrayList<>();
        List<Node> nodeList = new ArrayList<>();
        nodeList.add(root);
        do {
            List<Integer> levelList = bfs(nodeList);
            list.add(levelList);
        } while (nodeList.size() > 0);
        return JSON.common().toJSON(list);
    }


    //深度，先序遍历
    private void print(List<Integer> list, Node n) {
        if (n != null) {
            list.add(n.getValue());
            print(list, n.getLeft());
            print(list, n.getRight());
        }
    }

    private boolean find(int val) {
        return compare(root, val);
    }

    private boolean compare(Node node, int val) {
        if (node == null) {
            return false;
        } else {
            int nv = node.getValue();
            if (nv > val) {
                return compare(node.getLeft(), val);
            } else if (nv < val) {
                return compare(node.getRight(), val);
            } else {
                return true;
            }
        }
    }


    //层序遍历
    private List<Integer> bfs(List<Node> nodeList) {
        List<Integer> list = new ArrayList<>();
        int size = nodeList.size();
        for (int i = 0; i < size; i++) {
            Node n = nodeList.get(0);
            list.add(n.getValue());
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


    public static void main(String[] args) {
        int[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
//        int[] numbers = {10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0};
//        int[] numbers = {6, 9, 1, 3, 10, 8, 2, 7, 5, 4, 11};
//        int[] numbers = {7, 9, 1, 3, 2};
//        int[] numbers = {1, 2, 3, 4, 5,6,7,8,9,10};
//        int[] numbers = {1, 3, 2};

        AVLTree tree = new AVLTree(numbers);
        System.out.println(tree.toStringLot());
//        System.out.println(tree.find(11));
    }
    //
    //               7
    //
    //      3               9
    //   1     5       8       11
    // 0  2  4   6
    //
    //              5
    //       3              7
    //    2     4        6     9
    // 1                     8  10

    //               3
    //       1              5
    //   0       2      4        7
    //                        6    9
    //                           8   10
    //
    //

    //                     7
    //
    //             5                      9
    //
    //       3         6             8         10
    //
    //  2        4

    //                        4
    //           2                               8
    //    1            3                 6            9
    //                              5         7          10
    //
}
