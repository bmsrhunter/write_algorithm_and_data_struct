package com.my.learn.exercise.data.struct.tree;
/*
 * 创建人：baimiao
 * 创建时间：2023/9/27 17:55
 *
 */

import com.google.common.collect.Lists;
import com.wifiin.common.JSON;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/***
 * https://zhuanlan.zhihu.com/p/149287061
 * 一棵 m 阶的 B+ 树需要满足下列条件：
 *
 *             1）、每个分支结点最多有 m 棵子树（子结点）
 *             2）、非叶根结点至少有两棵子树，其他每个分支结点至少有 ceil(m/2)棵子树
 *             3）、结点的子树个数与关键字个数相等
 *             4）、所有叶结点包含全部关键字及指向相应记录的指针，而且叶结点中将关键字按大小顺序排列，并且相邻叶结点按大小顺序相互链接起来。
 *             5）、所有分支结点（可看成是索引的索引）中仅包含它的各个子结点（即下一级的索引块）中关键字的最大值及指向其子结点的指针
 *
 *             所有的插入都是插在了叶子结点，当不满足上述条件时进行分裂，依次向上递归
 * */

public class BPlusTree {


    private static final int scale = 3;//阶数，关键字数，每个节点的最大孩子数

    private static Node root;

    public static void main(String[] args) {
        build(new int[]{20, 30, 40, 50, 14, 67, 6, 1, 4, 23, 54, 89, 10, 78, 35, 41, 28, 75, 99, 58});
        System.out.println(toStringLot(root));
    }


    private static String toStringLot(Node root) {
        List<List<String>> list = new ArrayList<>();
        List<Node> nodeList = new ArrayList<>();
        nodeList.add(root);
        do {
            List<String> levelList = bfs(nodeList);
            list.add(levelList);
        } while (nodeList.size() > 0);
        return JSON.common().toJSON(list);
    }

    private static List<String> bfs(List<Node> nodeList) {
        List<String> list = new ArrayList<>();
        int size = nodeList.size();
        for (int i = 0; i < size; i++) {
            Node n = nodeList.get(0);
            list.add("level:" + (i + 1) + "-values:" + StringUtils.arrayToCommaDelimitedString(n.getKey().toArray(new Integer[0])));
            if (n.getChild() != null) {
                n.getChild().forEach(nodeList::add);
            }
            nodeList.remove(0);
        }
        return list;
    }

    private static void build(int[] values) {
        for (int val : values) {
            if (root == null) {
                Node indexNode = new Node();
                indexNode.addStably(0, val);
                root = indexNode;
            } else {
                insert(val);
            }
        }
    }


    //因为是有序的数组，所以用二分查找最快
    private static void insert(int val) {
        NodePosition pos = findIndex(val);
        Node node = pos.getNode();
        int flag = node.addStably(pos.getPos(), val);
        if (flag == 1) {
            modifyMaxValue(node, val);
        } else if (flag == -1) {
            adjust(pos.getNode());
        }
    }

    private static void modifyMaxValue(Node node, int replacement) {
        NodePosition pos = node.getParent();
        if (pos != null) {
            Node n = pos.getNode();
            n.setKeyValue(pos.getPos(), replacement);
            modifyMaxValue(n, replacement);
        }
    }


    private static void adjust(Node origin) {
        Node right = new Node();
        int size = origin.size();
        int from = scale % 2 == 0 ? scale / 2 : (scale / 2) + 1;
        List<Integer> originKey = origin.getKey();
        origin.setKey(Lists.newArrayList(originKey.subList(0, from).toArray(new Integer[0])));
        right.setKey(Lists.newArrayList(originKey.subList(from, size).toArray(new Integer[0])));
        List<Node> child = origin.getChild();
        if (child != null) {
            for (int i = from; i < size; i++) {
                Node n = child.get(i);
                right.addChild(n);
                n.setParent(new NodePosition(right, i));
            }
        } else {
            origin.setNext(right);
            NodePosition np = origin.getParent();
            if (np != null) {
                Node parent = np.getNode();
                int nPos = np.getPos();
                boolean newMax = right.getMax() > parent.getKey().get(nPos);
                int flag;
                if (newMax) {
                    parent.setKeyValue(nPos+1, right.getMax());
                    parent.addChild(nPos + 1, right);
                    flag = parent.addStably(nPos + 1, right.getMax());
                } else {
                    parent.setKeyValue(nPos,origin.getMax());
                    parent.addChild(nPos+1, right);
                    flag = parent.addStably(nPos, origin.getMax());
                }
                right.setParent(new NodePosition(parent, nPos + 1));
                //todo right 的右侧都需要加一
                if (flag == 1) {
                    modifyMaxValue(parent, right.getMax());
                } else if (flag == -1) {
                    adjust(parent);
                }
            } else {
                Node parent = new Node();
                parent.setChild(Lists.newArrayList(origin, right));
                parent.setKey(Lists.newArrayList(origin.getMax(), right.getMax()));
                origin.setParent(new NodePosition(parent, 0));
                right.setParent(new NodePosition(parent, 1));
                root = parent;
            }
        }
    }


    private static NodePosition findIndex(int val) {
        return binarySearch(root, val, 0, root.size() - 1);
    }

    private static NodePosition binarySearch(Node node, int val, int start, int end) {//1
        System.out.println(root);
        List<Integer> key = node.getKey();
        if (val > key.get(end)) {
            if (node.getChild() == null) {
                return new NodePosition(node, node.size());
            } else {
                List<Node> child = node.getChild();
                int size = child.size();
                return binarySearch(child.get(size - 1), val, start, end);
            }
        }
        if (val < key.get(start)) {
            if (node.getChild() == null) {
                return new NodePosition(node, 0);
            } else {
                List<Node> child = node.getChild();
                return binarySearch(child.get(0), val, start, end);
            }
        }
        int half = (start + end) / 2;
        if (val > key.get(half)) {
            start = start == half ? start + 1 : half;
            return binarySearch(node, val, start, end);
        } else {
            if (val > key.get(half - 1)) {
                return new NodePosition(node, half);
            } else {
                return binarySearch(node, val, start, half);
            }
        }
    }


    private static class Node {
        private List<Integer> key;//值（包含关键字）,由小到大排列
        private NodePosition parent;//分裂时候需要用到
        private List<Node> child;//指向孩子节点的指针
        private Object data;//数据指针.非叶子节点为空
        private Node next;//右边的节点，非叶子结点为空

        public void setKey(List<Integer> key) {
            this.key = key;
        }

        public void setKeyValue(int pos, int value) {
            key.set(pos, value);
        }

        public NodePosition getParent() {
            return parent;
        }

        public void setParent(NodePosition parent) {
            this.parent = parent;
        }

        public List<Integer> getKey() {
            return key;
        }

        public List<Node> getChild() {
            return child;
        }

        public void addChild(int pos, Node node) {
            if (child == null) {
                child = Lists.newArrayList(node);
            } else {
                child.add(pos, node);
            }
        }
        public void addChild(Node node) {
            if (child == null) {
                child = Lists.newArrayList(node);
            } else {
                child.add(node);
            }
        }

        public void clearChild(int pos) {
            if (child != null) {
                child.set(pos, null);
            }
        }

        public void setChild(List<Node> child) {
            this.child = child;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }

        public int size() {
            return key == null ? 0 : key.size();
        }

        // 0不变
        //1 节点最大值变化，不需要分裂
        //-1 需要分裂
        public int addStably(int pos, int val) {
            if (key == null) {
                key = new ArrayList<>();
                key.add(pos, val);
                return 0;
            } else {
                key.add(pos, val);
                int size = key.size();
                if (size > scale) {
                    return -1;//分裂
                } else {
                    return size - pos == 1 ? 1 : 0;
                }
            }
        }

        public int getMax() {
            return key.get(size() - 1);
        }
    }

    private static class NodePosition {
        private Node node;
        private int pos;

        public NodePosition() {
        }

        public NodePosition(Node node, int pos) {
            this.node = node;
            this.pos = pos;
        }

        public Node getNode() {
            return node;
        }

        public void setNode(Node node) {
            this.node = node;
        }

        public int getPos() {
            return pos;
        }

        public void setPos(int pos) {
            this.pos = pos;
        }
    }

    public static void main1(String[] args) {
        List<Integer> list = Lists.newArrayList(1, 2, 3, 4);
        list.add(4, 6);
        System.out.println(JSON.common().toJSON(list));
    }
}
