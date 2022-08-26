package com.mirkowu.mvm.leetcode;

import java.util.Arrays;

public class SortTest {
    public static void main(String[] args) {

        int[] a = new int[]{10, 0, 2, 5, 3, 2, 7, 9};
        bubbleSort(a);
        System.out.println("result = " + Arrays.toString(a));

        System.out.println("find = " + binarySearch(a,0,a.length,5));
    }

    public static void bubbleSort(int[] a) {
        for (int i = 0; i < a.length - 1; i++) {
            for (int j = i + 1; j < a.length; j++) {
                if (a[i] > a[j]) {
                    int temp = a[i];
                    a[i] = a[j];
                    a[j] = temp;
                }
            }
        }
    }

    public static int binarySearch(int[] a, int left, int right, int num) {
        int mid = (left + right) / 2;
        if (left > right) {
            return -1;
        }
        if (num > a[mid]) {
            return binarySearch(a, mid + 1, right, num);
        } else if (num > a[mid]) {
            return binarySearch(a, left, mid - 1, num);
        } else {
            return mid;
        }


    }
}
