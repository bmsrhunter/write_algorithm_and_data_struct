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
 *
 *
 *             删除操作：
 *         1.    删除该关键字，如果不破坏 B+树本身的性质，直接完成删除操作（情况 1）；
 *         2.  如果删除操作导致其该结点中最大（或最小）值改变，则应相应改动其父结点中的索引值（情况 2）；
 *         3. 在删除关键字后，如果导致其结点中关键字个数不足，
 *              有两种方法：一种是向兄弟结点去借，另外一种是同兄弟结点合并（情况 3、4 和 5）。（注意这两种方式有时需要更改其父结点中的索引值。）
 * */

public class BPlusTreeImmature {
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
            List<Integer> values = Lists.newArrayList();
            n.getCells().forEach(c -> values.add(c.getKey()));
            list.add(StringUtils.arrayToCommaDelimitedString(values.toArray(values.toArray(new Integer[0]))));
            n.getCells().stream().filter(a -> a.getChild() != null).forEach(c -> nodeList.add(c.getChild()));
            nodeList.remove(0);
        }
        return list;
    }

    private static void build(int[] values) {
        for (int val : values) {
            if (root == null) {
                Node node = new Node();
                Cell cell = new Cell(val);
                node.insert(0, cell);
                cell.setMaster(node);
                root = node;
            } else {
                insert(val);
            }
        }
    }

    private static void insert(int val) {
        Node node = searchNode(root, val);
        int index = searchIndex(node, val);
        Cell cell = new Cell(val);
        cell.setMaster(node);
        int flag = node.insert(index, cell);
        if (flag == 1) {
            modifyUpperValue(node, val);
        } else if (flag == -1) {
            adjust(node, true);
        }
    }


    private static void modifyUpperValue(Node node, int replacement) {
        Cell parent = node.getParent();
        if (parent != null) {
            if (parent.getKey() < replacement) {
                parent.setKey(replacement);
                Node master = parent.getMaster();
                int max = master.max();
                if (replacement == max) {
                    modifyUpperValue(master, replacement);
                }
            }
        }
    }

    private static void adjust(Node origin, boolean leaf) {
        Cell parent = origin.getParent();
        Node right = origin.divide(leaf);
        int maxVal = right.max();
        if (parent != null) {
            parent.setKey(origin.max());
            Node pMaster = parent.getMaster();
            Cell cell = new Cell(maxVal);
            cell.setMaster(pMaster);
            cell.setChild(right);
            right.setParent(cell);
            int pos = searchIndex(pMaster, maxVal);
            int flag = pMaster.insert(pos, cell);
            if (flag == -1) {
                adjust(pMaster, false);
            } else if (flag == 1) {
                modifyUpperValue(pMaster, maxVal);
            }
        } else {
            Node upper = new Node();
            List<Cell> cells = Lists.newArrayList();
            Cell rc = new Cell(maxVal);
            rc.setMaster(upper);
            rc.setChild(right);
            right.setParent(rc);
            Cell lc = new Cell(origin.max());
            lc.setMaster(upper);
            lc.setChild(origin);
            origin.setParent(lc);
            cells.add(lc);
            cells.add(rc);
            upper.setCells(cells);
            root = upper;
        }
    }

    //todo 两次二分查找，一次找node,一次找position

    private static Node searchNode(Node node, int val) {//1
        List<Cell> cells = node.getCells();
        int size = cells.size();
        int position = searchIndex(node, val);
        Cell cell;
        if (position >= size) {
            cell = cells.get(size - 1);
        } else {
            cell = cells.get(position);
        }
        Node child = cell.getChild();
        if (child == null) {
            return cell.getMaster();
        } else {
            return searchNode(child, val);
        }
    }


    //二分法查找 val between cells(index) and cells(index+1)
    // if val<min return cells(0); if val>max return cells(size()-1)

    private static int searchIndex(Node node, int val) {
        List<Cell> cells = node.getCells();
        int start = 0;
        Cell beginPoint = cells.get(start);
        if (val < beginPoint.getKey()) {
            return start;
        }
        int end = cells.size() - 1;
        Cell endPoint = cells.get(end);
        if (val > endPoint.getKey()) {
            return end + 1;
        }
        int target = -1;
        while (start < end) {
            int half = (start + end) / 2;
            Cell middle = cells.get(half);
            if (val > middle.getKey()) {
                Cell tmp = cells.get(half + 1);
                if (val < tmp.getKey()) {
                    target = half + 1;
                    break;
                } else {
                    start = half;
                }
            } else {
                Cell tmp = cells.get(half - 1);
                if (val > tmp.getKey()) {
                    target = half;
                    break;
                } else {
                    end = half;
                }
            }
        }
        return target;
    }


    private static class Node {
        private List<Cell> cells;
        private Cell parent;
        private Node next;

        public List<Cell> getCells() {
            return cells;
        }

        public void setCells(List<Cell> cells) {
            this.cells = cells;
        }

        public Cell getParent() {
            return parent;
        }

        public void setParent(Cell parent) {
            this.parent = parent;
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }

        public int size() {
            return cells.size();
        }

        public int max() {
            return cells.get(size() - 1).getKey();
        }

        public void increase(Cell cell) {
            if (cells == null) {
                cells = Lists.newArrayList(cell);
            } else {
                cells.add(cell);
            }
        }

        // 0不变
        //1 节点最大值变化，不需要分裂
        //-1 需要分裂
        public int insert(int pos, Cell cell) {
            if (cells == null) {
                cells = Lists.newArrayList(cell);
                return 0;
            } else {
                cells.add(pos, cell);
                int size = size();
                if (size > scale) {
                    return -1;//分裂
                } else {
                    return cell.getKey() == max() ? 1 : 0;
                }
            }
        }

        //分裂
        public Node divide(boolean leaf) {
            int size = size();
            int from = size % 2 == 0 ? size / 2 : (size / 2) + 1;
            Node right = new Node();
            for (int i = from; i < size; i++) {
                Cell part = cells.get(from);
                right.increase(part);
                part.setMaster(right);
                cells.remove(from);
            }
            if (leaf) {
                setNext(right);
            }
            return right;
        }
    }

    private static class Cell {
        private int key;
        private Node child;
        private Node master;//所属的node
        private Object data;

        public Cell() {
        }

        public Cell(int key) {
            this.key = key;
        }

        public int getKey() {
            return key;
        }

        public void setKey(int key) {
            this.key = key;
        }

        public Node getChild() {
            return child;
        }

        public void setChild(Node child) {
            this.child = child;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public Node getMaster() {
            return master;
        }

        public void setMaster(Node master) {
            this.master = master;
        }
    }

}
