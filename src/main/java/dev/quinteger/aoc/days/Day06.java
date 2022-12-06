package dev.quinteger.aoc.days;

import dev.quinteger.aoc.Solution;

import java.util.List;

public class Day06 extends Solution {
    public Day06(List<String> input) {
        super(input);
    }

    @Override
    public Object solvePart1() {
        return solveForLength(4);
    }

    @Override
    public Object solvePart2() {
        return solveForLength(14);
    }

    private Object solveForLength(int length) {
        var input = this.input.get(0);
        for (int i = 0; i < input.length() - length + 1; i++) {
            var substr = input.substring(i, i + length);
            if (substr.chars().distinct().count() == length) {
                return i + length;
            }
        }
        throw new RuntimeException("Bad input or parsing error");
    }
}
