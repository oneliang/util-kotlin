package com.oneliang.ktx.util.test.common;

public class Test {

    public static void main(String[] args) {
        int[] data = new int[7];
        data[0] = 8;
        data[1] = 2;
        data[2] = 5;
        data[3] = 9;
        data[4] = 1;
        data[5] = 4;
        data[6] = 10;
        int max = Integer.MIN_VALUE;
        int maxStartPosition = 0;
        int maxEndPosition = 0;

        for (int i = 0; i < data.length; i++) {
            int min = Integer.MAX_VALUE;
            for (int j = 0; j < i; j++) {
                if (data[j] < min) {
                    min = data[j];
                    maxStartPosition = j;
                }
            }
            int currentMax = data[i] - min;
            if (currentMax > max) {
                max = currentMax;
                maxEndPosition = i;
            }

        }
        System.out.printf("max:%s%n, start:%s, end:%s, checked:%s", max, maxStartPosition, maxEndPosition, data[maxEndPosition] - data[maxStartPosition]);
    }
}
