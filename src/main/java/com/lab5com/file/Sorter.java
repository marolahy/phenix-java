package com.lab5com.file;

import java.util.LinkedList;

public class Sorter {

    public LinkedList<String> sort(LinkedList<String> queue) {
        return compare(queue);
    }

    /**
     * Gets an unsorted list and sort it using merge sort algorithm
     *
     * @param list a list to be sorted
     * @return a sorted list
     */
    protected LinkedList<String> compare(LinkedList<String> list) {

        if (list.size() == 1) {
            return list;
        }
        int listSize = list.size();
        int middle = listSize / 2;
        LinkedList<String> left = new LinkedList<>();
        left.addAll(list.subList(0, middle));
        LinkedList<String> right = new LinkedList<>();
        right.addAll(list.subList(middle, listSize));

        left = compare(left);
        right = compare(right);
        return merge(left, right, list);
    }

    protected LinkedList<String> merge(LinkedList<String> left, LinkedList<String> right, LinkedList<String> result) {
        result.clear();
        int totalLength = left.size() + right.size();

        for (int i = 0; i < totalLength; i++) {
            if (right.isEmpty() || (!left.isEmpty() && isLeftPrecedent(left.peekFirst(), right.peekFirst()))) {
                result.offerLast(left.pollFirst());
            } else {
                result.offerLast(right.pollFirst());
            }
        }

        return result;
    }


    public boolean isLeftPrecedent(String left, String right) {
        int leftIndex = 0;
        int rightIndex = 0;

        boolean isLeftPrecedent = true;
        while (leftIndex < left.length() && rightIndex < right.length()) {
            char leftChar = Character.toLowerCase(left.charAt(leftIndex));
            char rightChar = Character.toLowerCase(right.charAt(rightIndex));
            if (leftChar < rightChar) {
                break;
            } else if (leftChar > rightChar) {
                isLeftPrecedent = false;
                break;
            }
            leftIndex++;
            rightIndex++;
        }
        return isLeftPrecedent;
    }
}
