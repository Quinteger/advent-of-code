package dev.quinteger.aoc.solutions.y2022;

import dev.quinteger.aoc.solutions.Solution;

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
        var boundaries = getBoundaries(positions);

        for (int i = boundaries.minRow(); i <= boundaries.maxRow(); i++) {
            for (int j = boundaries.minColumn(); j <= boundaries.maxColumn(); j++) {
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
        var result = simulate(10).points();
        var boundaries = getBoundaries(result);
        return (boundaries.maxRow() - boundaries.minRow() + 1) * (boundaries.maxColumn() - boundaries.minColumn() + 1) - result.size();
    }

    private Boundaries getBoundaries(Collection<Point> points) {
        return new Boundaries(
                points.stream().mapToInt(Point::row).min().orElseThrow(),
                points.stream().mapToInt(Point::row).max().orElseThrow(),
                points.stream().mapToInt(Point::column).min().orElseThrow(),
                points.stream().mapToInt(Point::column).max().orElseThrow()
        );
    }

    private record Boundaries(int minRow, int maxRow, int minColumn, int maxColumn) {}

    private record Direction(
            Function<? super Point, ? extends Set<? extends Point>> checkExtractor,
            Function<? super Point, ? extends Point> moveExtractor
    ) {}

    private Deque<Direction> getDirections() {
        var deque = new ArrayDeque<Direction>(4);
        deque.add(new Direction(Point::get3NorthPoints, Point::north));
        deque.add(new Direction(Point::get3SouthPoints, Point::south));
        deque.add(new Direction(Point::get3WestPoints, Point::west));
        deque.add(new Direction(Point::get3EastPoints, Point::east));
        return deque;
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
        return simulate().turns();
    }

    private record SimulationResult(Set<Point> points, int turns) {}

    private SimulationResult simulate() {
        return simulate(Integer.MAX_VALUE);
    }

    private SimulationResult simulate(int turns) {
        Set<Point> positions = new HashSet<>(this.positions);
        var directions = getDirections();
        for (int turn = 1; turn <= turns; turn++) {
            Set<Point> currentPositions = new HashSet<>(positions);
            Map<Point, List<Point>> targets = HashMap.newHashMap(currentPositions.size());

            for (Point point : positions) {
                if (Collections.disjoint(positions, point.get8PointsAround())) {
                    continue;
                }
                for (var direction : directions) {
                    if (Collections.disjoint(positions, direction.checkExtractor().apply(point))) {
                        addTarget(targets, point, direction.moveExtractor().apply(point));
                        currentPositions.remove(point);
                        break;
                    }
                }
            }

            if (targets.isEmpty()) {
                return new SimulationResult(positions, turn);
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
        return new SimulationResult(positions, turns);
    }
}
