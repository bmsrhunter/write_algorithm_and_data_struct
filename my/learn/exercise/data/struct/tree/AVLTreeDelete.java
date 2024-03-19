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
 * <p>
 * 不考虑删除情况下，avl只用单向的就可以实现，有删除情况下，需要知道父节点，逐级向上修改平衡因子
 */
public class AVLTreeDelete {


    private Node root;

    public static class Node {
        private int value;
        private int diff;//节点的平衡因子
        private Node parent;
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

        public Node getParent() {
            return parent;
        }

        public void setParent(Node parent) {
            this.parent = parent;
        }
    }

    public AVLTreeDelete(int[] array) {
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


    //
    //                                 4
    //                       2                          6
    //                  1         3            5                7
    //                                            5.5      6.5          8
    //                                                              7.5
    //

    private Node rRotate(Node node) {
        Node leftSon = node.getLeft();
        Node grandRight = leftSon.getRight();
        Node parent = node.getParent();
        leftSon.setDiff(leftSon.getDiff() - 1);
        if (parent != null) {
            if (parent.getValue() > leftSon.getValue()) {
                parent.setLeft(leftSon);
            } else {
                parent.setRight(leftSon);
            }
        } else {
            root = leftSon;
        }
        leftSon.setRight(node);
        leftSon.setParent(parent);
        node.setParent(leftSon);
        node.setLeft(grandRight);
        if (grandRight != null) {
            grandRight.setParent(node);
            node.setDiff(-1);
        } else {
            node.setDiff(0);
        }
        return node;
    }


    //
    //                                 4
    //                       2                          6
    //                           3            5                        7
    //                                            5.5            6.5          8
    //                                                       6.4
    //
    private Node lRotate(Node node) {
        Node rightSon = node.getRight();
        Node grandLeft = rightSon.getLeft();
        Node parent = node.getParent();
        rightSon.setDiff(rightSon.getDiff() + 1);
        if (parent != null) {
            if (parent.getValue() > rightSon.getValue()) {
                parent.setLeft(rightSon);
            } else {
                parent.setRight(rightSon);
            }
        } else {
            root = rightSon;
        }
        rightSon.setLeft(node);
        rightSon.setParent(parent);
        node.setParent(rightSon);
        node.setRight(grandLeft);
        if (grandLeft != null) {
            grandLeft.setParent(node);
        }
        node.setDiff(0);
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
                Node newNode = new Node(val);
                newNode.setParent(node);
                node.setLeft(newNode);
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
                Node newNode = new Node(val);
                newNode.setParent(node);
                node.setRight(newNode);
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

    //                          5
    //               3                   8
    //          2         4         6          9
    //                4.1   4.2        7
    //

    /**
     * 删除算法，当父节点平衡因子为0时候，删除一个叶子结点，整棵树 不用动，父节点的平衡因子改为 ±1
     * 1.删除叶子结点，直接删除然后调整平衡因子
     * 2.删除非叶子结点[将非叶子节点转换成删除叶子结点]
     * 2.1 只有左子树，将节点的左子树节点值给节点然后删除左子树【因为avl的平衡因子是 0或者±1，所以只有一个分支的情况下分支不会再有子分支，否则破坏平衡】
     * 2.2 只有右子树，将节点的右子树节点值给节点然后删除右子树【因为avl的平衡因子是 0或者±1，所以只有一个分支的情况下分支不会再有子分支，否则破坏平衡】
     * 2.3 同时有左右子树，选取前驱【中序遍历，前驱是左节点】，将前驱值给待删除节点，递归删除前驱节点，直至叶子结点
     */


    private void delete(int val) {
        delete(root, val, 0);
    }

    //                        4
    //           2                               8
    //    1            3                 6            9
    //                              5         7          10
    //
    private void delete(Node n, int value, int direction) {
        PositionNode pnode = find(n, value, direction);
        if (pnode != null) {
            int index = pnode.getIndex();//父节点的左中右位置 1,0,-1
            Node node = pnode.getNode();
            Node parent = node.getParent();
            boolean leaf = (node.getLeft() == null && node.getRight() == null);
            if (leaf) {//删除叶子节点
                if (index == 0) {
                    root = null;//只有根节点一个节点
                } else {
                    if (index == 1) {
                        parent.setLeft(null);
                    } else {
                        parent.setRight(null);
                    }
                    parent.setDiff(parent.getDiff() - index);
                    recursionRotate(parent, index == 1);//按照添加的思路，左边是1，右边是-1，删除一个左边的相当于添加了一个右边的
                }
            } else {//删除非叶子节点
                Node left = node.getLeft();
                Node right = node.getRight();
                if (right == null) {
                    int lv = left.getValue();
                    node.setValue(lv);
                    node.setLeft(null);
                    node.setDiff(node.getDiff() - 1);
                    recursionRotate(parent, true);
                } else if (left == null) {
                    int rv = right.getValue();
                    node.setValue(rv);
                    node.setRight(null);
                    node.setDiff(node.getDiff() + 1);
                    recursionRotate(parent, false);
                } else {
                    PositionNode pn = findReplace(node);
                    Node nd = pn.getNode();
                    int val = nd.getValue();
                    node.setValue(val);
                    delete(nd, val, pn.getIndex());
                }
            }
        }
    }

    private PositionNode findReplace(Node node) {
        if (node == null) {
            return null;
        }
        Node left = node.getLeft();
        if (left == null) {
            Node right = node.getRight();
            Node rightLeft = right.getLeft();
            if (rightLeft == null) {
                return new PositionNode(right, -1);
            } else {
                return new PositionNode(findMin(rightLeft), -1);
            }
        } else {
            Node leftRight = left.getRight();
            if (leftRight == null) {
                return new PositionNode(left, 1);
            } else {
                return new PositionNode(findMax(leftRight), -1);
            }
        }
    }

    private Node findMax(Node node) {
        Node right = node.getRight();
        if (right != null) {
            return findMax(right);
        } else {
            return node;
        }
    }

    private Node findMin(Node node) {
        Node left = node.getLeft();
        if (left != null) {
            return findMin(left);
        } else {
            return node;
        }
    }


    private void recursionRotate(Node node, boolean removeLeft) {
        if (node != null) {
            int diff = node.getDiff();
            int delta;
            if (diff > 1) {
                Node son = node.getLeft();
                if (son.getLeft() != null) {
                    rotate(node, true);
                } else {
                    rotate(node, false);
                }
                delta = -1;
            } else if (diff < -1) {
                Node son = node.getRight();
                if (son.getRight() != null) {
                    rotate(node, false);
                } else {
                    rotate(node, true);
                }
                delta = 1;
            } else if (diff == 0) {
                delta = removeLeft ? -1 : 1;
            } else {
                return;
            }
            Node parent = node.getParent();
            if (parent != null) {
                parent.setDiff(parent.getDiff() + delta);
                recursionRotate(parent, removeLeft);
            }
        }
    }

    public void rotate(Node node, boolean left) {//旋转,boolean 不是旋转方向，而是是否是左子树需要旋转,是否在左侧添加的节点
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

    //树的广度遍历
    public String toStringLot() {
        List<List<String>> list = new ArrayList<>();
        List<Node> nodeList = new ArrayList<>();
        nodeList.add(root);
        do {
            List<String> levelList = bfs(nodeList);
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

    private PositionNode find(Node node, int val, int index) {//index=0是根节点，index=1父节点的左孩子，index=2父节点的右孩子
        if (node == null) {
            return null;
        } else {
            int nv = node.getValue();
            if (nv > val) {
                return find(node.getLeft(), val, 1);
            } else if (nv < val) {
                return find(node.getRight(), val, -1);
            } else {
                return new PositionNode(node, index);
            }
        }
    }

    private static class PositionNode {
        private Node node;
        private int index;

        public PositionNode(Node node, int index) {
            this.node = node;
            this.index = index;
        }

        public Node getNode() {
            return node;
        }

        public int getIndex() {
            return index;
        }
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


    //层序遍历,用到了队列，先放根节点然后再放左右子节点删除前面的节点
    private List<String> bfs(List<Node> nodeList) {
        List<String> list = new ArrayList<>();
        int size = nodeList.size();
        for (int i = 0; i < size; i++) {
            Node n = nodeList.get(0);
            list.add(n.getValue() + "-parent-" + (n.getParent() == null ? "null" : n.getParent().getValue()));
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
//        int[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
//        int[] numbers = {1, 2, 3};
//        int[] numbers = {10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0};
        int[] numbers = {6, 9, 1, 3, 10, 8, 2, 7, 5, 4, 11};
//        int[] numbers = {7, 9, 1, 3, 2};
//        int[] numbers = {1, 2, 3, 4, 5,6,7,8,9,10};
//        int[] numbers = {1, 3, 2};

        AVLTreeDelete tree = new AVLTreeDelete(numbers);
        System.out.println(tree.toStringLot());
        tree.delete(6);
        System.out.println(tree.toStringLot());
//        tree.delete(8);
//        System.out.println(tree.toStringLot());

    }
    //
    //               7
    //
    //      3               9
    //   1     5       8       11
    // 0  2  4   6
    //
    //               6
    //       2               9
    //    1      4         8    10
    //        3    5     7        11

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
