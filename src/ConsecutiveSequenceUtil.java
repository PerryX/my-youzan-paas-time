package com.github.perry.xiao.kata;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 《计算之魂》找出最长子连续子序列练习
 */
public class ConsecutiveSequenceUtil {

    /**
     * 找出最长连续的子序列
     * 子序列指将原序列删除一部分元素后剩余的序列
     */
    public int[] getLongestConsecutiveSubSequence(int[] nums) {
        Map<Integer, Integer> subSeqIndex = genSubSeqIndex(nums);
        return getLongestSeq(subSeqIndex);
    }

    /**
     * 在序列的集合中找出最长的序列
     */
    public int[] getLongestConsecutiveSequenceByBook(int[] nums) {
        // 开始书没看仔细，以为是描述错误，想做个验证，后来发现其实是细节没get到：关键在去重，否则对于一些测试用例无法通过
        // e.g. 5, 6, 7, 8, 3, 4, 5, 6
        Set<Integer> uniqNums = Arrays.stream(nums).boxed().collect(Collectors.toSet());
        int[] uniqNumArr = uniqNums.stream().mapToInt(Integer::intValue).toArray();
        Map<Integer, Integer> subSeqIndex = genSubSeqIndex(uniqNumArr);

        Set<Integer> indexSet = subSeqIndex.keySet();
        for (Integer index : indexSet) {
            Integer count = subSeqIndex.get(index);
            Integer pre = index - count;
            while (subSeqIndex.containsKey(pre)) {
                Integer preCount = subSeqIndex.get(pre);
                count += preCount;
                subSeqIndex.put(index, count);
                pre = pre - preCount;
            }
        }

        return getLongestSeq(subSeqIndex);
    }

    private int[] getLongestSeq(Map<Integer, Integer> subSeqIndex) {
        Integer maxCount = 0;
        Integer index = -1;
        for (Map.Entry<Integer, Integer> entry : subSeqIndex.entrySet()) {
            if (entry.getValue() > maxCount) {
                index = entry.getKey();
                maxCount = entry.getValue();
            }
        }
        return genSeq(index, maxCount);
    }

    private int[] genSeq(int index, int count) {
        int[] ret = new int[count];
        for (int i = 0, v = index - count + 1; i < count; i++, v++) {
            ret[i] = v;
        }
        return ret;
    }

    private Map<Integer, Integer> genSubSeqIndex(int[] nums) {
        HashMap<Integer, Integer> subSeqIndex = new HashMap<>();
        for (int num : nums) {
            int pre = num - 1;
            Integer preCount = subSeqIndex.get(pre);
            if (preCount != null) {
                int count = preCount + 1;
                subSeqIndex.compute(num, (index, seqCount) -> {
                    if (seqCount == null || seqCount < count) {
                        return count;
                    }
                    return seqCount;
                });
                subSeqIndex.remove(pre);
            } else {
                subSeqIndex.putIfAbsent(num, 1);
            }
        }
        return subSeqIndex;
    }

    public static void main(String[] args) {
        ConsecutiveSequenceUtil solution = new ConsecutiveSequenceUtil();
        int[] nums = {7, 1, 4, 3, 5, 5, 9, 4, 10, 25, 11, 12, 33, 2, 13, 6};

        // test get Longest Consecutive Sub-Sequence
        int[] seq1 = solution.getLongestConsecutiveSubSequence(nums);
        int[] expectedSeq1 = {9, 10, 11, 12, 13};
        assertArraysEquals("get Longest Consecutive Sub-Sequence: ", nums, expectedSeq1, seq1);

        // test get Longest Consecutive Sequence
        int[] seq2 = solution.getLongestConsecutiveSequenceByBook(nums);
        int[] expectedSeq2 = {1, 2, 3, 4, 5, 6, 7};
        assertArraysEquals("get Longest Consecutive Sub-Sequence: ", nums, expectedSeq2, seq2);

        int[] nums2 = {5, 6, 7, 8, 3, 4, 5, 6};
        int[] seq3 = solution.getLongestConsecutiveSequenceByBook(nums2);
        int[] expectedSeq3 = {3, 4, 5, 6, 7, 8};
        assertArraysEquals("get Longest Consecutive Sub-Sequence: ", nums2, expectedSeq3, seq3);
    }

    static void assertArraysEquals(String msg, int[] input, int[] expected, int[] ret) {
        if (Arrays.equals(expected, ret)) {
            return;
        }

        System.out.println("Meet problem when " + msg +
                "\ninputs: " + Arrays.toString(input) +
                "\nexpected: " + Arrays.toString(expected) +
                "\nresult: " + Arrays.toString(ret));
    }
}
