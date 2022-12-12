package dev.quinteger.aoc.days;

import dev.quinteger.aoc.Solution;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.ToIntFunction;

public class Day12 extends Solution {
    public Day12(List<String> input) {
        super(input);
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

    private final int[][] map;
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

    @Override
    public Object solvePart1() {
        return find(new Point(startRow, startColumn), new Point(endRow, endColumn), this::isGoodElevationChange, null);
    }

    private boolean isGoodElevationChange(Point from, Point to) {
        int elevationFrom = map[from.row()][from.column()];
        int elevationTo = map[to.row()][to.column()];
        return elevationTo <= elevationFrom + 1;
    }

    @Override
    public Object solvePart2() {
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
        var unvisited = new HashSet<Point>(map.length * map[0].length);
        var values = new HashMap<Point, Integer>(map.length * map[0].length);

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                var point = new Point(i, j);
                if (point.equals(from)) {
                    values.put(point, 0);
                } else {
                    values.put(point, Integer.MAX_VALUE);
                }
                unvisited.add(point);
            }
        }

        var currentPoint = from;
        Point newPoint;
        while (currentPoint != null) {
            for (int i = 0; i < 4; i++) {
                newPoint = switch (i) {
                    case 0 -> currentPoint.up();
                    case 1 -> currentPoint.down();
                    case 2 -> currentPoint.left();
                    case 3 -> currentPoint.right();
                    default -> throw new RuntimeException();
                };
                if (isInsideMap(newPoint) && elevationChecker.test(currentPoint, newPoint) && unvisited.contains(newPoint)) {
                    int oldValue = values.get(newPoint);
                    int newValue = values.get(currentPoint) + 1;
                    if (newValue < oldValue) {
                        values.put(newPoint, newValue);
                    }
                }
            }
            unvisited.remove(currentPoint);
            currentPoint = unvisited.stream().filter(p -> values.get(p) != Integer.MAX_VALUE).min(Comparator.comparingInt(values::get)).orElse(null);
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
