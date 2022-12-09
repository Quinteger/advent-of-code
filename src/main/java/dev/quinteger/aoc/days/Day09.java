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
        public int taxicabDistance(Vector2i other) {
            return Math.max(Math.abs(x - other.x), Math.abs(y - other.y));
        }

        public Vector2i subtract(Vector2i other) {
            return new Vector2i(x - other.x, y - other.y);
        }

        public int maxAbs() {
            return Math.max(Math.abs(x), Math.abs(y));
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
                headPosition = move(headPosition, direction, 1);
                var diff = headPosition.subtract(tailPosition);

                if (diff.maxAbs() == 2) {
                    if (diff.x == 2) {
                        tailPosition = new Vector2i(tailPosition.x + 1, tailPosition.y + diff.y);
                    } else if (diff.x == -2) {
                        tailPosition = new Vector2i(tailPosition.x - 1, tailPosition.y + diff.y);
                    } else if (diff.y == 2) {
                        tailPosition = new Vector2i(tailPosition.x + diff.x, tailPosition.y + 1);
                    } else if (diff.y == -2) {
                        tailPosition = new Vector2i(tailPosition.x + diff.x, tailPosition.y - 1);
                    } else {
                        throw new RuntimeException();
                    }
                }

                tailPositions.add(tailPosition);
            }
        }

        return tailPositions.size();
    }

    private static Vector2i move(Vector2i position, String direction, int amount) {
        if (direction.equals("U")) {
            position = new Vector2i(position.x, position.y + amount);
        } else if (direction.equals("D")) {
            position = new Vector2i(position.x, position.y - amount);
        } else if (direction.equals("R")) {
            position = new Vector2i(position.x + amount, position.y);
        } else if (direction.equals("L")) {
            position = new Vector2i(position.x - amount, position.y);
        } else {
            throw new IllegalArgumentException();
        }
        return position;
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
                positions.set(0, move(positions.get(0), direction, 1));

                for (int j = 1; j < positions.size(); j++) {
                    positions.set(j, moveTailToHead(positions.get(j), positions.get(j - 1)));
                }

                tailPositions.add(positions.get(positions.size() - 1));
            }
        }

        return tailPositions.size();
    }

    private static Vector2i moveTailToHead(Vector2i tail, Vector2i head) {
        var diff = head.subtract(tail);

        if (diff.maxAbs() == 2) {
            if (diff.x == 2) {
                tail = new Vector2i(tail.x + 1, tail.y + clamp(-1, diff.y, 1));
            } else if (diff.x == -2) {
                tail = new Vector2i(tail.x - 1, tail.y + clamp(-1, diff.y, 1));
            } else if (diff.y == 2) {
                tail = new Vector2i(tail.x + clamp(-1, diff.x, 1), tail.y + 1);
            } else if (diff.y == -2) {
                tail = new Vector2i(tail.x + clamp(-1, diff.x, 1), tail.y - 1);
            } else {
                throw new RuntimeException();
            }
        }

        return tail;
    }

    private static int clamp(int min, int value, int max) {
        return value < min ? min : Math.min(value, max);
    }
}
