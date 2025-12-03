package dev.quinteger.aoc.solutions.y2022;

import dev.quinteger.aoc.solutions.Solution;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.ToIntFunction;

public class Day12 extends Solution {
    private int[][] map;
    private int startRow;
    private int startColumn;
    private int endRow;
    private int endColumn;

    private record Point(int row, int column) {
        public Point up() {
            return new Point(row - 1, column);
        }
        public Point down() {
            return new Point(row + 1, column);
        }
        public Point left() {
            return new Point(row, column - 1);
        }
        public Point right() {
            return new Point(row, column + 1);
        }
    }

    private void createMap(List<String> input) {
        map = new int[input.size()][input.get(0).length()];
        for (int i = 0; i < input.size(); i++) {
            String line = input.get(i);
            var chars = line.toCharArray();
            for (int j = 0; j < chars.length; j++) {
                char c = chars[j];
                if (c == 'S') {
                    map[i][j] = 0;
                    startRow = i;
                    startColumn = j;
                } else if (c == 'E') {
                    map[i][j] = 'z' - 'a';
                    endRow = i;
                    endColumn = j;
                } else {
                    map[i][j] = chars[j] - 'a';
                }
            }
        }
    }

    @Override
    public Object solvePart1(List<String> input, boolean example) {
        createMap(input);
        return find(new Point(startRow, startColumn), new Point(endRow, endColumn), this::isGoodElevationChange, null);
    }

    private boolean isGoodElevationChange(Point from, Point to) {
        int elevationFrom = map[from.row()][from.column()];
        int elevationTo = map[to.row()][to.column()];
        return elevationTo <= elevationFrom + 1;
    }

    @Override
    public Object solvePart2(List<String> input, boolean example) {
        return find(new Point(endRow, endColumn), null, this::isGoodElevationChangeReversed, tentative ->
                tentative.entrySet().stream()
                        .filter(e -> {
                            var point = e.getKey();
                            int row = point.row();
                            int column = point.column();
                            return map[row][column] == 0;
                        })
                        .min(Comparator.comparingInt(Map.Entry::getValue))
                        .map(Map.Entry::getValue)
                        .orElseThrow(RuntimeException::new));
    }

    private boolean isGoodElevationChangeReversed(Point from, Point to) {
        return isGoodElevationChange(to, from);
    }

    private int find(Point from, Point to, BiPredicate<Point, Point> elevationChecker, ToIntFunction<Map<Point, Integer>> mapper) {
        Set<Point> visited = new HashSet<>(map.length * map[0].length);
        Map<Point, Integer> values = new HashMap<>(map.length * map[0].length);
        Deque<Point> queue = new ArrayDeque<>();

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                var point = new Point(i, j);
                if (point.equals(from)) {
                    values.put(point, 0);
                } else {
                    values.put(point, Integer.MAX_VALUE);
                }
            }
        }

        queue.add(from);
        while (!queue.isEmpty()) {
            Point currentPoint = queue.removeFirst();
            if (!visited.add(currentPoint)) {
                continue;
            }
            Point newPoint;
            for (int i = 0; i < 4; i++) {
                newPoint = switch (i) {
                    case 0 -> currentPoint.up();
                    case 1 -> currentPoint.down();
                    case 2 -> currentPoint.left();
                    case 3 -> currentPoint.right();
                    default -> throw new RuntimeException();
                };
                if (isInsideMap(newPoint) && elevationChecker.test(currentPoint, newPoint) && !visited.contains(newPoint)) {
                    int oldValue = values.get(newPoint);
                    int newValue = values.get(currentPoint) + 1;
                    if (newValue < oldValue) {
                        values.put(newPoint, newValue);
                        queue.addLast(newPoint);
                    }
                }
            }
        }

        if (to != null) {
            return values.get(to);
        } else if (mapper != null){
            return mapper.applyAsInt(values);
        } else {
            throw new RuntimeException();
        }
    }

    private boolean isInsideMap(Point point) {
        int row = point.row();
        int column = point.column();
        return row >= 0 && row < map.length && column >= 0 && column < map[row].length;
    }
}
