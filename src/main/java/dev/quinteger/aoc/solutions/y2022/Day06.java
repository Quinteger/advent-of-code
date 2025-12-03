package dev.quinteger.aoc.solutions.y2022;

import dev.quinteger.aoc.solutions.Solution;

import java.util.List;

public class Day06 extends Solution {
    @Override
    public Object solvePart1(List<String> input, boolean example) {
        return solveForLength(input, 4);
    }

    @Override
    public Object solvePart2(List<String> input, boolean example) {
        return solveForLength(input, 14);
    }

    private Object solveForLength(List<String> input, int length) {
        var line = input.get(0);
        for (int i = 0; i < line.length() - length + 1; i++) {
            var substr = line.substring(i, i + length);
            if (substr.chars().distinct().count() == length) {
                return i + length;
            }
        }
        throw new RuntimeException("Bad input or parsing error");
    }
}
