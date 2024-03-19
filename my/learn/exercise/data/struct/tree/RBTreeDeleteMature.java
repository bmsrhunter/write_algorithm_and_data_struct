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
 * 5.任一节点到叶子(Null 节点)结点的简单路径经过的黑色节点数量都相等
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

public class RBTreeDeleteMature {

    private static Node root;

    public static void main(String[] args) {
        int[] values = {8, 1, 5, 9, 6, 4, 7, 18, 17, 2, 21, 16, 25, 3, 10, 36, 11, 13, 15, 14, 19, 12};
        build(values);
        System.out.println(toStringLot(root));
        delete(8);
        System.out.println(toStringLot(root));
        delete(6);
        System.out.println(toStringLot(root));
        delete(21);
        System.out.println(toStringLot(root));
        delete(19);
        System.out.println(toStringLot(root));
        delete(25);
        System.out.println(toStringLot(root));
        delete(17);
        System.out.println(toStringLot(root));
        delete(5);
        System.out.println(toStringLot(root));
        delete(4);
        System.out.println(toStringLot(root));
        delete(10);
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
        root = new Node();
        root.setBlack(true);//根节点为黑色
        root.setValue(values[0]);
        root.setParent(null);
        for (int i = 2; i < values.length; i++) {
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
                    parent.setBlack(true);
                } else {
                    lRotate(parent);
                    node.setBlack(true);//node 和parent 换位置了
                }
                grandPa.setBlack(false);
                rRotate(grandPa);
            } else {
                if (pval < val) {
                    parent.setBlack(true);
                } else {
                    rRotate(parent);
                    node.setBlack(true);
                }
                grandPa.setBlack(false);
                lRotate(grandPa);
            }
        }
    }

    //                  pa
    //           black
    //       red
    //  red       blck
    private static void rRotate(Node node) {
        Node parent = node.getParent();
        Node son = node.getLeft();
        if (son == null) {
            return;
        }
        Node grandRight = son.getRight();
        if (parent != null) {
            if (parent.getValue() > node.getValue()) {
                parent.setLeft(son);
            } else {
                parent.setRight(son);
            }
        } else {
            root = son;
        }
        son.setParent(parent);
        son.setRight(node);
        node.setParent(son);
        node.setLeft(grandRight);
        if (grandRight != null) {
            grandRight.setParent(node);
        }
    }

    //                    black                                      red
    //               red          black                       black      black
    //           red                                   black
    //    black        black                    black         black
    //red                                  red
    //
    //

    //
    //                 b
    //          b             b
    //                     r     r
    private static void lRotate(Node node) {
        Node parent = node.getParent();
        Node son = node.getRight();
        if (son == null) {
            return;
        }
        Node grandSonLeft = son.getLeft();
        if (parent != null) {
            if (parent.getValue() < node.getValue()) {
                parent.setRight(son);
            } else {
                parent.setLeft(son);
            }
        } else {
            root = son;
        }
        son.setParent(parent);
        node.setParent(son);
        node.setRight(grandSonLeft);
        son.setLeft(node);
        if (grandSonLeft != null) {
            grandSonLeft.setParent(node);
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
     *                        8b
     *               5b                                   17r
     *         3b       6b                 11b                      21b
     *      2r             7r         9b         16b         r               b
     *                                                   b     b
     *
     *
     *
     * */

    /**
     * 参考链接：https://zhuanlan.zhihu.com/p/145006031
     * 删除思路：最终转化成对叶子结点或者对只有一个节点的删除，将红黑树看做完全二叉树，空的节点用黑色补齐
     * 1.删除以后也需要满足bst（二叉搜索树）
     * 2 删除节点 u 替换节点为 v（空节点也是黑色）,u的兄弟是s ,父亲是p
     * 2.1 u，v有一个为红色节点
     * 2.2 u,v都是黑色节点（双黑问题）
     * 2.2.1 u是根节点，转换成删除左子树的最右节点
     * 2.2.2 u是非根节点
     * 2.2.2.1 s是黑色且有最少一个孩子节点是红色（LL,LR,RL,RR）
     * 2.2.2.2 s是黑色且两个儿子都是黑色
     * 2.2.2.3 s是红色(L,R)
     *
     * 先看在本节点内能不能达到平衡，如果不可以再向上和兄弟分支做平衡
     *
     *
     */


    private static void reBalance(Node node, boolean leftPosition) {
        Node parent = node.getParent();
        //平衡的子节点
        Node brother = leftPosition ? node.getLeft() : node.getRight();
        Node brotherLeft = brother.getLeft();
        Node brotherRight = brother.getRight();
        if (brother.isBlack()) {
            if (brotherLeft != null && brotherRight != null) {
                if (brotherLeft.isBlack() && brotherRight.isBlack()) {
                    if (node.isBlack()) {
                        brother.setBlack(false);
                    } else {
                        brother.setBlack(false);
                        node.setBlack(true);
                    }
                } else if (brotherLeft.isBlack()) {
                    if (leftPosition) {
                        if (!node.isBlack()) {
                            node.setBlack(true);
                        }
                        brotherLeft.setBlack(true);
                        lRotate(brother);
                        rRotate(node);
                    } else {
                        if (node.isBlack()) {
                            brotherLeft.setBlack(true);
                            lRotate(node);
                        } else {
                            lRotate(node);
                        }
                    }
                } else if (brotherRight.isBlack()) {
                    if (leftPosition) {
                        if (node.isBlack()) {
                            brotherRight.setBlack(true);
                            rRotate(node);
                        } else {
                            rRotate(node);
                        }

                    } else {
                        if (!node.isBlack()) {
                            node.setBlack(true);
                        }
                        brotherRight.setBlack(true);
                        rRotate(brother);
                        lRotate(node);
                    }
                } else {
                    if (leftPosition) {
                        if (!node.isBlack()) {
                            brotherLeft.setBlack(true);
                            brother.setBlack(false);
                            node.setBlack(true);
                            rRotate(node);
                        } else {
                            brotherLeft.setBlack(true);
                            rRotate(node);
                        }
                    } else {
                        if (!node.isBlack()) {
                            brotherRight.setBlack(true);
                            brother.setBlack(false);
                            node.setBlack(true);
                            lRotate(node);
                        } else {
                            brotherRight.setBlack(true);
                            lRotate(node);
                        }
                    }
                }
            } else if (brotherLeft != null) {
                if (node.isBlack()) {
                    brotherLeft.setBlack(true);
                    if (leftPosition) {
                        rRotate(node);
                    } else {
                        lRotate(brother);
                        rRotate(node);
                    }
                } else {
                    if (leftPosition) {
                        rRotate(node);
                    } else {
                        brotherLeft.setBlack(true);
                        brother.setBlack(false);
                        lRotate(node);
                    }
                }
            } else if (brotherRight != null) {
                if (node.isBlack()) {
                    brotherRight.setBlack(true);
                    if (leftPosition) {
                        lRotate(brother);
                        rRotate(node);
                    } else {
                        lRotate(node);
                    }
                } else {
                    if (leftPosition) {
                        brotherRight.setBlack(true);
                        brother.setBlack(false);
                        rRotate(node);
                    } else {
                        lRotate(node);
                    }
                }
            } else {
                brother.setBlack(false);
                if (node.isBlack()) {
                    if (parent != null) {
                        reBalance(parent, node.getValue() > parent.getValue());//向上追溯
                    }
                } else {
                    node.setBlack(true);
                }
            }
        } else {//brother 红，父亲是黑的，儿子也是黑的
            if (leftPosition) {
                brother.setBlack(true);
                rRotate(node);
                reBalance(node, true);
            } else {
                brother.setBlack(true);
                lRotate(node);
                reBalance(node, false);
            }
        }
    }
    //                     b                                 b                                 b
    //             r             b                     b          r                      b           b
    //         b        b             r             b    b      b    b                 b   b      r      r
    //     b     b    r    r                                  r   r      r                      b   b   r    b
    //              b  b  b  b                              b  b b  b                                  b b
    //
    //


    //                              b                                                  b                        b
    //                  b                      r                                 b          b               r       b
    //                                       b          b                            b                   b      b
    //                                  r      r    r     r                       r   r                    r
    private static void delete(int val) {
        Node dn = find(val);
        if (dn != null) {
            delete(dn);
        }
    }

    private static void delete(Node dn) {
        if (dn.getLeft() == null && dn.getRight() == null) {//删除没有孩子的节点
            if (dn.getParent() == null) {//只有一个节点，根节点
                root = null;
            } else {//非根叶子
                Node parent = dn.getParent();
                boolean left = dn.getValue() > parent.getValue() ? false : true;
                if (left) {
                    parent.setLeft(null);
                } else {
                    parent.setRight(null);
                }
                if (dn.isBlack()) {//黑色节点需要再平衡
                    reBalance(parent, !left);
                }
            }
        } else if (dn.getLeft() == null || dn.getRight() == null) {//删除有一个孩子的节点，case: b-r
            Node child = dn.getLeft() == null ? dn.getRight() : dn.getLeft();
            dn.setValue(child.getValue());
            dn.setRight(null);
            dn.setLeft(null);
        } else {//删除有两个孩子的节点,变成删除左孩子的最右节点（if null 就是左孩子）
            Node replace = findReplace(dn);
            dn.setValue(replace.getValue());
            delete(replace);
        }
    }


    //查找左孩子的最右节点【如果为空就是左孩子本身】或者右孩子的最左节点【如果为空就是右孩子本身】

    private static Node findReplace(Node node) {
        if (node == null) {
            return null;
        }
        Node left = node.getLeft();
        if (left == null) {
            Node right = node.getRight();
            Node rightLeft = right.getLeft();
            if (rightLeft == null) {
                return right;
            } else {
                return findMin(rightLeft);
            }
        } else {
            Node leftRight = left.getRight();
            if (leftRight == null) {
                return left;
            } else {
                return findMax(leftRight);
            }
        }
    }

    private static Node findMax(Node node) {
        Node right = node.getRight();
        if (right != null) {
            return findMax(right);
        } else {
            return node;
        }
    }

    private static Node findMin(Node node) {
        Node left = node.getLeft();
        if (left != null) {
            return findMin(left);
        } else {
            return node;
        }
    }

    private static Node find(int val) {
        return find(root, val);
    }

    private static Node find(Node node, int val) {
        if (node == null) {
            return null;
        }
        int nv = node.getValue();
        if (nv == val) {
            return node;
        } else if (nv > val) {
            return find(node.getLeft(), val);
        } else {
            return find(node.getRight(), val);
        }
    }
}
