package dev.quinteger.aoc.days;

import dev.quinteger.aoc.Solution;

import java.util.*;

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
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
//                System.out.print("%02d  ".formatted(map[i][j]));
                var point = new Point(i, j);
                if (i == startRow && j == startColumn) {
                    tentative.put(point, 0);
                } else {
                    tentative.put(point, Integer.MAX_VALUE);
                }
                unvisited.add(point);
            }
//            System.out.println();
        }

        var currentPoint = new Point(startRow, startColumn);
        var destination = new Point(endRow, endColumn);
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
                if (isInsideMap(newPoint) && isGoodElevationChange(currentPoint, newPoint) && unvisited.contains(newPoint)) {
                    int oldValue = tentative.get(newPoint);
                    int newValue = tentative.get(currentPoint) + 1;
                    if (newValue < oldValue) {
                        tentative.put(newPoint, newValue);
                    }
                }
            }
            unvisited.remove(currentPoint);
            currentPoint = unvisited.stream().min(Comparator.comparingInt(tentative::get)).orElse(null);
        }
        return tentative.get(destination);
    }
    private final Set<Point> unvisited = new HashSet<>();
    private final Map<Point, Integer> tentative = new HashMap<>();

    private boolean isInsideMap(Point point) {
        int row = point.row();
        int column = point.column();
        return row >= 0 && row < map.length && column >= 0 && column < map[row].length;
    }

    private boolean isGoodElevationChange(Point from, Point to) {
        int elevationFrom = map[from.row()][from.column()];
        int elevationTo = map[to.row()][to.column()];
        return elevationTo <= elevationFrom + 1;
    }

    @Override
    public Object solvePart2() {
        unvisited.clear();
        tentative.clear();
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
//                System.out.print("%02d  ".formatted(map[i][j]));
                var point = new Point(i, j);
                if (i == endRow && j == endColumn) {
                    tentative.put(point, 0);
                } else {
                    tentative.put(point, Integer.MAX_VALUE);
                }
                unvisited.add(point);
            }
//            System.out.println();
        }

        var currentPoint = new Point(endRow, endColumn);
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
                if (isInsideMap(newPoint) && isGoodElevationChange2(currentPoint, newPoint) && unvisited.contains(newPoint)) {
                    int oldValue = tentative.get(newPoint);
                    System.out.printf("Current point %s has value %d%n", currentPoint, tentative.get(currentPoint));
                    int newValue = tentative.get(currentPoint) + 1;
                    if (newValue < oldValue) {
                        tentative.put(newPoint, newValue);
                    }
                }
            }
            unvisited.remove(currentPoint);
            System.out.printf("Unvisited has %d points%n", unvisited.size());
            currentPoint = unvisited.stream().filter(p -> tentative.get(p) != Integer.MAX_VALUE).min(Comparator.comparingInt(tentative::get)).orElse(null);
        }
        return tentative.entrySet().stream()
                .filter(e -> {
                    var point = e.getKey();
                    int row = point.row();
                    int column = point.column();
                    return map[row][column] == 0;
                })
                .min(Comparator.comparingInt(Map.Entry::getValue))
                .orElseThrow(RuntimeException::new);
    }

    private boolean isGoodElevationChange2(Point from, Point to) {
        int elevationFrom = map[from.row()][from.column()];
        int elevationTo = map[to.row()][to.column()];
        return elevationTo >= elevationFrom - 1;
    }
}
