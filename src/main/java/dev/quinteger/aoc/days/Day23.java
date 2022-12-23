package dev.quinteger.aoc.days;

import dev.quinteger.aoc.Solution;

import java.util.*;
import java.util.function.Function;

public class Day23 extends Solution {

    private record Point(int row, int column) {
        public Set<Point> get8PointsAround() {
            return Set.of(
                    new Point(row - 1, column - 1),
                    new Point(row - 1, column),
                    new Point(row - 1, column + 1),
                    new Point(row, column - 1),
                    new Point(row, column + 1),
                    new Point(row + 1, column - 1),
                    new Point(row + 1, column),
                    new Point(row + 1, column + 1)
            );
        }

        public Set<Point> get3NorthPoints() {
            return Set.of(
                    new Point(row - 1, column - 1),
                    new Point(row - 1, column),
                    new Point(row - 1, column + 1)
            );
        }

        public Set<Point> get3SouthPoints() {
            return Set.of(
                    new Point(row + 1, column - 1),
                    new Point(row + 1, column),
                    new Point(row + 1, column + 1)
            );
        }

        public Set<Point> get3WestPoints() {
            return Set.of(
                    new Point(row - 1, column - 1),
                    new Point(row, column - 1),
                    new Point(row + 1, column - 1)
            );
        }

        public Set<Point> get3EastPoints() {
            return Set.of(
                    new Point(row - 1, column + 1),
                    new Point(row, column + 1),
                    new Point(row + 1, column + 1)
            );
        }

        public Point north() {
            return new Point(row - 1, column);
        }

        public Point south() {
            return new Point(row + 1, column);
        }

        public Point west() {
            return new Point(row, column - 1);
        }

        public Point east() {
            return new Point(row, column + 1);
        }
    }
    private final Set<Point> positions = new HashSet<>();

    private void parseInput(List<String> input) {
        for (int i = 0; i < input.size(); i++) {
            var chars = input.get(i).toCharArray();
            for (int j = 0; j < chars.length; j++) {
               char c = chars[j];
               if (c == '#') {
                   positions.add(new Point(i, j));
               }
            }
        }
//        System.out.println(positions);
    }

    private void printPositions() {
        printPositions(positions);
    }

    private void printPositions(Set<Point> positions) {
        int minRow = positions.stream().mapToInt(Point::row).min().orElseThrow();
        int maxRow = positions.stream().mapToInt(Point::row).max().orElseThrow();
        int minCol = positions.stream().mapToInt(Point::column).min().orElseThrow();
        int maxCol = positions.stream().mapToInt(Point::column).max().orElseThrow();

        for (int i = minRow; i <= maxRow; i++) {
            for (int j = minCol; j <= maxCol; j++) {
                if (positions.contains(new Point(i, j))) {
                    System.out.print('#');
                } else {
                    System.out.print('.');
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    @Override
    public Object solvePart1(List<String> input, boolean example) {
        parseInput(input);
        printPositions();
        var result = simulate(10);
        int minRow = result.stream().mapToInt(Point::row).min().orElseThrow();
        int maxRow = result.stream().mapToInt(Point::row).max().orElseThrow();
        int minCol = result.stream().mapToInt(Point::column).min().orElseThrow();
        int maxCol = result.stream().mapToInt(Point::column).max().orElseThrow();
        return (maxRow - minRow + 1) * (maxCol - minCol + 1) - result.size();
    }

    private record Direction(Function<Point, Set<Point>> checkExtractor, Function<Point, Point> moveExtractor) {}

    private Deque<Direction> getDirections() {
        var deque = new ArrayDeque<Direction>(4);
        deque.add(new Direction(Point::get3NorthPoints, Point::north));
        deque.add(new Direction(Point::get3SouthPoints, Point::south));
        deque.add(new Direction(Point::get3WestPoints, Point::west));
        deque.add(new Direction(Point::get3EastPoints, Point::east));
        return deque;
    }

    private Set<Point> simulate(int turns) {
        Set<Point> positions = new HashSet<>(this.positions);
        var directions = getDirections();
        for (int turn = 1; turn <= turns; turn++) {
            Set<Point> currentPositions = new HashSet<>(positions);
            Map<Point, List<Point>> targets = HashMap.newHashMap(currentPositions.size());

            for (Point point : positions) {
                if (Collections.disjoint(positions, point.get8PointsAround())) {
                    continue;
                }
//                else if (Collections.disjoint(positions, point.get3NorthPoints())) {
//                    addTarget(targets, point, point.north());
//                    currentPositions.remove(point);
//                } else if (Collections.disjoint(positions, point.get3SouthPoints())) {
//                    addTarget(targets, point, point.south());
//                    currentPositions.remove(point);
//                } else if (Collections.disjoint(positions, point.get3WestPoints())) {
//                    addTarget(targets, point, point.west());
//                    currentPositions.remove(point);
//                } else if (Collections.disjoint(positions, point.get3EastPoints())) {
//                    addTarget(targets, point, point.east());
//                    currentPositions.remove(point);
//                }
                for (var direction : directions) {
                    if (Collections.disjoint(positions, direction.checkExtractor().apply(point))) {
                        addTarget(targets, point, direction.moveExtractor().apply(point));
                        currentPositions.remove(point);
                        break;
                    }
                }
            }

            positions.clear();
            positions.addAll(currentPositions);
            for (var entry : targets.entrySet()) {
                var to = entry.getKey();
                var fromList = entry.getValue();
                if (fromList == null || fromList.size() == 0) {
                    throw new RuntimeException();
                } else if (fromList.size() == 1) {
                    positions.add(to);
                } else {
                    positions.addAll(fromList);
                }
            }
//            printPositions(positions);
            directions.addLast(directions.removeFirst());
        }
        return positions;
    }

    private void addTarget(Map<Point, List<Point>> targets, Point from, Point to) {
        targets.compute(to, (p, l) -> {
            if (l == null) {
                var list = new ArrayList<Point>();
                list.add(from);
                return list;
            } else {
                l.add(from);
                return l;
            }
        });
    }

    @Override
    public Object solvePart2(List<String> input, boolean example) {
        return simulateUntilStop();
    }

    private int simulateUntilStop() {
        Set<Point> positions = new HashSet<>(this.positions);
        var directions = getDirections();
        for (int turn = 1; ; turn++) {
            Set<Point> currentPositions = new HashSet<>(positions);
            Map<Point, List<Point>> targets = HashMap.newHashMap(currentPositions.size());

            for (Point point : positions) {
                if (Collections.disjoint(positions, point.get8PointsAround())) {
                    continue;
                }
//                else if (Collections.disjoint(positions, point.get3NorthPoints())) {
//                    addTarget(targets, point, point.north());
//                    currentPositions.remove(point);
//                } else if (Collections.disjoint(positions, point.get3SouthPoints())) {
//                    addTarget(targets, point, point.south());
//                    currentPositions.remove(point);
//                } else if (Collections.disjoint(positions, point.get3WestPoints())) {
//                    addTarget(targets, point, point.west());
//                    currentPositions.remove(point);
//                } else if (Collections.disjoint(positions, point.get3EastPoints())) {
//                    addTarget(targets, point, point.east());
//                    currentPositions.remove(point);
//                }
                for (var direction : directions) {
                    if (Collections.disjoint(positions, direction.checkExtractor().apply(point))) {
                        addTarget(targets, point, direction.moveExtractor().apply(point));
                        currentPositions.remove(point);
                        break;
                    }
                }
            }

            if (targets.isEmpty()) {
                return turn;
            }

            positions.clear();
            positions.addAll(currentPositions);
            for (var entry : targets.entrySet()) {
                var to = entry.getKey();
                var fromList = entry.getValue();
                if (fromList == null || fromList.size() == 0) {
                    throw new RuntimeException();
                } else if (fromList.size() == 1) {
                    positions.add(to);
                } else {
                    positions.addAll(fromList);
                }
            }
//            printPositions(positions);
            directions.addLast(directions.removeFirst());
        }
    }
}
