package com.mirkowu.mvm.leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Q605 {
    public static void main(String[] args) {

//        int[] a = new int[]{1, 0, 0, 0, 1};
//        boolean result = canlastFlowers(a, 3);
//        System.out.println("result = " + result);
        System.out.println("result = " + reverseInt(2147483646));
    }

    public static boolean canlastFlowers(int[] a, int n) {

        int count = 0;
        int len = a.length;
        for (int i = 0; i < len; i++) {
            if (a[i] == 0) {
                boolean can = false;
                if (i == 0) {
                    can = a[i] == 0 && a[i + 1] == 0;
                } else if (i == len - 1) {
                    can = a[i - 1] == 0 && a[i] == 0;
                } else if (a[i] != 1) {
                    can = a[i - 1] == 0 && a[i] == 0 && a[i + 1] == 0;
                }
                if (can) {
                    a[i] = 1;
                    count++;
                }
            }
        }
        return count >= n;

    }

    public static List<Integer> getList(String s) {
        List<Integer> list = new ArrayList<>();
        char[] chars = s.toCharArray();
        int[] last = new int[26];

        int len = s.length();

        int i = len - 1;

        while (i >= 0) {
            int index = chars[i] - 'a';
            if (last[index] == 0) {
                last[index] = i;
            }
            i--;
        }

        i = 0;
        int end = 0;

        while (i < len) {
            end = last[chars[i] - 'a'];
            for (int j = 0; j < end; j++) {
                if (last[chars[j] - 'a'] > end) end = last[chars[j] - 'a'];
            }
            list.add(end - i + 1);
            i = end + 1;
        }
        return list;
    }

    public static int getMaxProfit(int[] a) {
        int max = 0;
        for (int i = 1; i < a.length; i++) {
            int n = a[i] - a[i - 1];
            if (n > 0) {
                max += n;
            }
        }
        return max;
    }

    public int[][] reconstructQueue(int[][] people) {

        Arrays.sort(people, (o1, o2) -> {
            if (o1[0] != o2[0]) {
                return o2[0] - o1[0];
            } else {
                return o1[1] - o2[1];
            }
        });

        List<int[]> result = new ArrayList<>();
        for (int[] p : people) {
            result.add(p[1], p);
        }
        return result.toArray(new int[people.length][]);

    }

    public boolean checkPossibility(int[] nums) {
        boolean isFirst = false;
        for (int i = 0; i < nums.length - 1; i++) {
            if (nums[i] > nums[i + 1]) {
                if (isFirst) {
                    return false;
                }
                if (i == 0) {
                    nums[i] = nums[i + 1];
                } else if (nums[i - 1] > nums[i + 1]) {
                    nums[i + 1] = nums[i];
                } else {
                    nums[i] = nums[i - 1];
                    i--;
                }
                isFirst = true;
            }
        }
        return true;
    }


    public void merge(int[] nums1, int m, int[] nums2, int n) {
        int pos = m-- + n-- - 1;
        while (n >= 0) {
            if (m >= 0) {
                nums1[pos--] = nums1[m] > nums2[n] ? nums1[m--] : nums2[n--];
            } else {
                nums1[pos--] = nums2[n--];
            }
        }
    }


//    public ListNode detectCycle(ListNode head) {
//        if (head == null) return null;
//        ListNode fast = head, slow = head;
//        while (fast != null) {
//            if (fast.next != null) {
//                fast = fast.next.next;
//            } else {
//                return null;
//            }
//            slow = slow.next;
//            //第一次相遇
//            if (fast == slow) {
//                fast = head;
//                while (fast != slow) {
//                    slow = slow.next;
//                    fast = fast.next;
//                }
//                return slow;
//            }
//        }
//        return null;
//    }

    public static void testHashTable() {
        Hashtable<String, String> hashtable = new Hashtable<>();
        hashtable.put("", "");
        hashtable.get("");

        HashMap map = new HashMap();
        Collections.synchronizedMap(map);

        ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
        concurrentHashMap.get("");
        concurrentHashMap.put("", "");
    }

    public static int reverseInt(int num) {
        int result = 0;
        while (num > 0) {
            int y = num % 10;
            num = num / 10;

            result = result * 10 + y;
        }

        return result;
    }

//    public List<List<Integer>> levelOrder(TreeNode root) {
//        List<List<Integer>> result=new ArrayList();
//        if(root==null){
//            return result;
//        }
//
//        LinkedList<TreeNode> nodes=new LinkedList<>();
//        nodes.add(root);
//
//        while(!nodes.isEmpty()){
//            List<Integer> list=new ArrayList();
//            int size=nodes.size();
//            for(int i=0;i<size;i++){
//                TreeNode remove=nodes.remove(0);
//                list.add(remove.val);
//                if(remove.left!=null){
//                    nodes.add(remove.left);
//                }
//                if(remove.right!=null){
//                    nodes.add(remove.right);
//                }
//
//                list.clear();
//            }
//            result.add(list);
//        }
//
//        return result;
//    }
}
