package dev.quinteger.aoc.days;

import dev.quinteger.aoc.Solution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Day01 extends Solution {
    @Override
    public Object solvePart1(List<String> input, boolean example) {
        int maxSum = 0;
        int currentSum = 0;
        for (int i = 0; i < input.size(); i++) {
            var line = input.get(i);
            boolean isEmpty = line.isEmpty();
            if (!isEmpty) {
                int cals = Integer.parseInt(line);
                currentSum += cals;
            }
            if (isEmpty || i == input.size() - 1) {
                if (currentSum > maxSum) {
                    maxSum = currentSum;
                }
                currentSum = 0;
            }
        }
        return maxSum;
    }

    @Override
    public Object solvePart2(List<String> input, boolean example) {
        List<Integer> list = new ArrayList<>();
        int currentSum = 0;
        for (int i = 0; i < input.size(); i++) {
            var line = input.get(i);
            boolean isEmpty = line.isEmpty();
            if (!isEmpty) {
                int cals = Integer.parseInt(line);
                currentSum += cals;
            }
            if (isEmpty || i == input.size() - 1) {
                list.add(currentSum);
                currentSum = 0;
            }
        }
        return list.stream()
                .sorted(Comparator.reverseOrder())
                .limit(3)
                .mapToInt(Integer::intValue)
                .sum();
    }
}
