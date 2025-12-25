package dev.quinteger.aoc.solutions.y2015;

import dev.quinteger.aoc.solutions.Solution;

import java.util.List;

public class Day01 extends Solution {
    @Override
    public Object solvePart1(List<String> input, boolean example) {
        int floor = 0;
        String line = input.getFirst();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            floor = adjustFloor(floor, c);
        }
        return floor;
    }

    @Override
    public Object solvePart2(List<String> input, boolean example) {
        int floor = 0;
        String line = input.getFirst();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            floor = adjustFloor(floor, c);
            if (floor < 0) {
                return i + 1;
            }
        }
        throw new IllegalStateException();
    }

    private static int adjustFloor(int floor, char c) {
        return switch (c) {
            case '(' -> floor + 1;
            case ')' -> floor - 1;
            default -> throw new IllegalStateException();
        };
    }
}
