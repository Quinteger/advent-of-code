package dev.quinteger.aoc.solutions.y2025;

import dev.quinteger.aoc.solutions.Solution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Day01 extends Solution {
    @Override
    public Object solvePart1(List<String> input, boolean example) {
        int position = 50;
        int hits = 0;
        for (String line : input) {
            boolean negative = switch(line.charAt(0)) {
                case 'R' -> false;
                case 'L' -> true;
                default -> throw new IllegalStateException();
            };
            int amount = Integer.parseInt(line.substring(1));
            if (negative) {
                position -= amount;
            } else {
                position += amount;
            }
            if (position % 100 == 0) {
                hits++;
            }
        }
        return hits;
    }

    @Override
    public Object solvePart2(List<String> input, boolean example) {
        int position = 50;
        int hits = 0;
        for (String line : input) {
            boolean negative = switch(line.charAt(0)) {
                case 'R' -> false;
                case 'L' -> true;
                default -> throw new IllegalStateException();
            };
            int amount = Integer.parseInt(line.substring(1));
            int rawNextPosition;
            if (negative) {
                rawNextPosition = position - amount;
            } else {
                rawNextPosition = position + amount;
            }
            int hitsToAdd;
            int nextPosition;
            if (rawNextPosition >= 100) {
                hitsToAdd = rawNextPosition / 100;
                nextPosition = rawNextPosition % 100;
            } else if (rawNextPosition <= 0) {
                hitsToAdd = rawNextPosition / -100;
                if (position > 0) {
                    hitsToAdd++;
                }
                nextPosition = 100 + (rawNextPosition % 100);
                // for negative numbers that divide by -100 this will turn into 100, need to transform it into 0
                nextPosition %= 100;
            } else {
                hitsToAdd = 0;
                nextPosition = rawNextPosition;
            }
            hits += hitsToAdd;
            position = nextPosition;
        }
        return hits;
    }
}
