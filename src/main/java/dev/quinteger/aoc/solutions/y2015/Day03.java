package dev.quinteger.aoc.solutions.y2015;

import dev.quinteger.aoc.solutions.Solution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Day03 extends Solution {
    @Override
    public Object solvePart1(List<String> input, boolean example) {
        int x = 0;
        int y = 0;
        Map<Integer, Map<Integer, Integer>> presents = new HashMap<>();
        addPresent(presents, x, y);
        String line = input.getFirst();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            switch (c) {
                case '^' -> y++;
                case 'v' -> y--;
                case '>' -> x++;
                case '<' -> x--;
                default -> throw new IllegalStateException();
            }
            addPresent(presents, x, y);
        }
        return presents.values()
                .stream()
                .mapToInt(Map::size)
                .sum();
    }

    @Override
    public Object solvePart2(List<String> input, boolean example) {
        MutablePosition p1 = new MutablePosition();
        MutablePosition p2 = new MutablePosition();
        Map<Integer, Map<Integer, Integer>> presents = new HashMap<>();
        addPresent(presents, 0, 0);
        String line = input.getFirst();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            MutablePosition currentPosition;
            if (i % 2 == 0) {
                currentPosition = p1;
            } else {
                currentPosition = p2;
            }
            switch (c) {
                case '^' -> currentPosition.y++;
                case 'v' -> currentPosition.y--;
                case '>' -> currentPosition.x++;
                case '<' -> currentPosition.x--;
                default -> throw new IllegalStateException();
            }
            addPresent(presents, currentPosition);
        }
        return presents.values()
                .stream()
                .mapToInt(Map::size)
                .sum();
    }

    private static void addPresent(Map<Integer, Map<Integer, Integer>> presents, MutablePosition mutablePosition) {
        addPresent(presents, mutablePosition.x, mutablePosition.y);
    }

    private static void addPresent(Map<Integer, Map<Integer, Integer>> presents, int x, int y) {
        presents.computeIfAbsent(x, k -> new HashMap<>()).merge(y, 1, Integer::sum);
    }
    
    private static class MutablePosition {
        private int x;
        private int y;
    }
}
