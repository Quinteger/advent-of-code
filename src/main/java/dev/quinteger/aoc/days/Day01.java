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
        int max = 0;
        int current = 0;
        for (String line : input) {
            if (line.isEmpty()) {
                if (current > max) {
                    max = current;
                }
                current = 0;
            } else {
                int cals = Integer.parseInt(line);
                current += cals;
            }
        }
        System.out.println(max);
    }

    @Override
    public void solvePart2() {
        List<Integer> list = new ArrayList<>();
        int current = 0;
        for (String line : input) {
            if (line.isEmpty()) {
                list.add(current);
                current = 0;
            } else {
                int cals = Integer.parseInt(line);
                current += cals;
            }
        }
        var result = list.stream().sorted(Comparator.reverseOrder()).limit(3).toList();
        System.out.println(result);
    }
}
