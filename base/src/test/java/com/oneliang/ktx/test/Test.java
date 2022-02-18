package com.oneliang.ktx.test;

import java.util.Iterator;
import java.util.TreeSet;

class Test {
    public static void main(String[] args) {
        int n = 7;
        System.out.println("------begin------");
        TreeSet<Integer> results = new TreeSet<Integer>();
        calculate(n, 0, results);
        Iterator<Integer> iterator = results.iterator();
        while (iterator.hasNext()) {
            System.out.println("count:" + iterator.next());
        }
    }

    private static void calculate(int n, int count, TreeSet<Integer> results) {
        if (n == 1) {
            results.add(count);
            return;
        }
        if (n % 2 == 0) {
            calculate(n / 2, count + 1, results);
        } else {
            calculate(n + 1, count + 1, results);
            calculate(n - 1, count + 1, results);
        }
    }
}

