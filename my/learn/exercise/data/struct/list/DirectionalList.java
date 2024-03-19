package com.my.learn.exercise.data.struct.list;
/*
 * 创建人：baimiao
 * 创建时间：2023/7/20 11:05
 *
 */
/**
 * 单项链表 翻转思路 将尾的next指向它的前驱，将前驱赋值成尾，以此类推知道head=tail
 * */

public class DirectionalList {

    private int size;

    private Node tail;

    private Node head;

    public int getSize() {
        return size;
    }


    public static class Node {
        private int value;
        private Node next;


        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }
    }


    public DirectionalList add(int value) {
        Node node = new Node();
        node.setValue(value);
        if (tail != null) {
            tail.next = node;
        } else {
            head = node;
        }
        tail = node;
        size++;
        return this;
    }

    // 1->2->3->4->5
    public void rotate() {
        if (head != null && tail != head) {
            Node tn = tail;
            trans();
            head = tn;
        }
    }


    private void trans() {
        Node tmp = head;
        while (tmp.next != tail) {
            tmp = tmp.next;
        }
        tail.next = tmp;
        tail = tmp;
        if (head != tail) {
            trans();
        } else {
            head.next = null;
        }
    }

    @Override
    public String toString() {
        if (head == null) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        Node n = head;
        while (n != null) {
            sb.append(n.value).append(",");
            n = n.next;
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        return sb.toString();
    }

    public static void main(String[] args) {
        DirectionalList list = new DirectionalList();
        list.add(1).add(4).add(5).add(3).add(7).add(2);
        System.out.println(list);
        list.rotate();
        System.out.println(list);
    }

}
