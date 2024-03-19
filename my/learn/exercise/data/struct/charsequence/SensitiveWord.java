package com.my.learn.exercise.data.struct.charsequence;
/*
 * 创建人：baimiao
 * 创建时间：2023/8/14 10:18
 *
 */

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.nio.charset.StandardCharsets;

public class SensitiveWord {
    public static void main(String[] args) {
        build();
    }

    private static void load() {

    }

    private static void build() {
        BloomFilter<String> bloomFilter = BloomFilter.create(Funnels.stringFunnel(StandardCharsets.UTF_8), 1000, 0.001);
        int n = 1000;
        for (int i = 0; i < n; i++) {
            bloomFilter.put(String.valueOf(i));
        }
        int count = 0;
        for (int i = 0; i < 2 * n; i++) {
            if (bloomFilter.mightContain(String.valueOf(i))) {
                count++;
            }
        }
        System.out.println("过滤器误判率：" + (count - n) / Double.valueOf(n));
    }
}
