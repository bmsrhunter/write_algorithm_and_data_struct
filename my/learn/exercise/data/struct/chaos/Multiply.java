package com.my.learn.exercise.data.struct.chaos;
/*
 * 创建人：baimiao
 * 创建时间：2024/1/30 17:54
 *
 */

import com.google.api.client.util.Lists;

import java.util.List;

public class Multiply {
    public static void main(String[] args) {
        System.out.println(power("2", "32"));
    }

    private static String power(String a, String b) {//2^10000    [1<a<10]
        int base = Integer.valueOf(a);
        int times = Integer.valueOf(b);
        List<Integer> store = Lists.newArrayList();
        store.add(base);
        int tribute;
        for (int i = 1; i < times; i++) {
            tribute = 0;
            int length = store.size();
            for (int j = length - 1; j >= 0; j--) {
                int tmp = base * store.get(j);
                if (tmp >= 10) {
                    int mod = tmp / 10;
                    int margin = tmp % 10;
                    int val = margin + tribute;
                    if (val >= 10) {
                        tribute = mod + (val / 10);
                    } else {
                        tribute = mod;
                    }
                    store.set(j, val);
                } else {
                    int val = tmp + tribute;
                    if (val >= 10) {
                        tribute = val / 10;
                        store.set(j, val % 10);
                    } else {
                        tribute=0;
                        store.set(j, val);
                    }
                }
            }
            if (tribute > 0) {
                store.add(0, tribute);
            }
        }
        StringBuilder sb = new StringBuilder();
        store.stream().forEachOrdered(sb::append);
        return sb.toString();
    }

}
