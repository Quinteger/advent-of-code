package dev.quinteger.aoc.days;

import dev.quinteger.aoc.Solution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Day01 extends Solution {
    public Day01(List<String> input) {
        super(input);
    }

    @Override
    public void solvePart1() {
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
        System.out.println(maxSum);
    }

    @Override
    public void solvePart2() {
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
        var result = list.stream()
                .sorted(Comparator.reverseOrder())
                .limit(3)
                .mapToInt(Integer::intValue)
                .sum();
        System.out.println(result);
    }
}
