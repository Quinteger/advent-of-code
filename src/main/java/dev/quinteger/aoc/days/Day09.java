package dev.quinteger.aoc.days;

import dev.quinteger.aoc.Solution;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day09 extends Solution {
    public Day09(List<String> input) {
        super(input);
    }

    private record Vector2i(int x, int y) {

        public Vector2i move(String direction) {
            return switch (direction) {
                case "U" -> new Vector2i(x, y + 1);
                case "D" -> new Vector2i(x, y - 1);
                case "R" -> new Vector2i(x + 1, y);
                case "L" -> new Vector2i(x - 1, y);
                default -> throw new IllegalArgumentException();
            };
        }

        public Vector2i moveTo(Vector2i other) {
            var diff = other.subtract(this);

            if (diff.maxAbs() == 2) {
                return new Vector2i(x + clampTo1(diff.x), y + clampTo1(diff.y));
            }

            return this;
        }

        public Vector2i subtract(Vector2i other) {
            return new Vector2i(x - other.x, y - other.y);
        }

        public int maxAbs() {
            return Math.max(Math.abs(x), Math.abs(y));
        }

        private static int clampTo1(int value) {
            if (value < -1) {
                return -1;
            } else if (value > 1) {
                return 1;
            }
            return value;
        }
    }

    @Override
    public Object solvePart1() {
        Set<Vector2i> tailPositions = new HashSet<>();
        var headPosition = new Vector2i(0, 0);
        var tailPosition = new Vector2i(0, 0);
        tailPositions.add(tailPosition);
        for (String line : input) {
            var parts = line.split(" ");
            String direction = parts[0];
            int count = Integer.parseInt(parts[1]);
            for (int i = 0; i < count; i++) {
                headPosition = headPosition.move(direction);
                tailPosition = tailPosition.moveTo(headPosition);
                tailPositions.add(tailPosition);
            }
        }

        return tailPositions.size();
    }

    @Override
    public Object solvePart2() {
        Set<Vector2i> tailPositions = new HashSet<>();

        List<Vector2i> positions = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            positions.add(new Vector2i(0, 0));
        }

        tailPositions.add(new Vector2i(0, 0));
        for (int lineIndex = 0; lineIndex < input.size(); lineIndex++) {
            String line = input.get(lineIndex);
            var parts = line.split(" ");
            String direction = parts[0];
            int count = Integer.parseInt(parts[1]);
            for (int i = 0; i < count; i++) {
                positions.set(0, positions.get(0).move(direction));

                for (int j = 1; j < positions.size(); j++) {
                    positions.set(j, positions.get(j).moveTo(positions.get(j - 1)));
                }

                tailPositions.add(positions.get(positions.size() - 1));
            }
        }

        return tailPositions.size();
    }
}
