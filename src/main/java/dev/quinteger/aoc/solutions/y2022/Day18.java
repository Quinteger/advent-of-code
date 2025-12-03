package dev.quinteger.aoc.solutions.y2022;

import dev.quinteger.aoc.solutions.Solution;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Day18 extends Solution {
    private final Set<Point> points = new HashSet<>();
    private int minX;
    private int maxX;
    private int minY;
    private int maxY;
    private int minZ;
    private int maxZ;

    private void createPoints(List<String> input) {
        for (String line : input) {
            String[] split = line.split(",");
            var point = new Point(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
            points.add(point);
        }
        minX = points.stream().mapToInt(Point::x).min().orElseThrow(RuntimeException::new);
        maxX = points.stream().mapToInt(Point::x).max().orElseThrow(RuntimeException::new);
        minY = points.stream().mapToInt(Point::y).min().orElseThrow(RuntimeException::new);
        maxY = points.stream().mapToInt(Point::y).max().orElseThrow(RuntimeException::new);
        minZ = points.stream().mapToInt(Point::z).min().orElseThrow(RuntimeException::new);
        maxZ = points.stream().mapToInt(Point::z).max().orElseThrow(RuntimeException::new);
        System.out.println(points);
        System.out.printf("X: {%d, %d}, Y: {%d, %d}, Z: {%d, %d}%n", minX, maxX, minY, maxY, minZ, maxZ);
    }

    @Override
    public Object solvePart1(List<String> input, boolean example) {
        createPoints(input);
        int faces = 0;
        for (Point point : points) {
            faces += getAirNeighbourCount(point);
        }
        return faces;
    }

    private record Point(int x, int y, int z) {
        private List<Point> getAllNeighbours() {
            List<Point> neighbours =  new ArrayList<>(6);
            for (int i = 0; i < 6; i++) {
                var neighbourPoint = switch (i) {
                    case 0 -> new Point(x - 1, y, z);
                    case 1 -> new Point(x + 1, y, z);
                    case 2 -> new Point(x, y - 1, z);
                    case 3 -> new Point(x, y + 1, z);
                    case 4 -> new Point(x, y, z - 1);
                    case 5 -> new Point(x, y, z + 1);
                    default -> throw new RuntimeException();
                };
                neighbours.add(neighbourPoint);
            }
            return neighbours;
        }

        private List<Point> getNeighbours(Predicate<? super Point> predicate) {
            return getAllNeighbours().stream().filter(predicate).collect(Collectors.toCollection(() -> new ArrayList<>(6)));
        }
    }

    private List<Point> getAirNeighbours(Point point) {
        return point.getNeighbours(p -> !points.contains(p));
    }

    private int getAirNeighbourCount(Point point) {
        return getAirNeighbours(point).size();
    }

    private List<Point> getLavaNeighbours(Point point) {
        return point.getNeighbours(points::contains);
    }

    private int getLavaNeighbourCount(Point point) {
        return getLavaNeighbours(point).size();
    }

    private boolean isOutside(Point point) {
        return point.x() < minX || point.x() > maxX
                || point.y() < minY || point.y() > maxY
                || point.z() < minZ || point.z() > maxZ;
    }

    private void checkForAirPocket(Point point) {
        if (!airPockets.contains(point) && !notAirPockets.contains(point)) {
            CheckResult checkResult = isAirPocket(point);
            if (checkResult.resultType() == ResultType.POCkET) {
                airPockets.addAll(checkResult.points());
            } else if (checkResult.resultType() == ResultType.OUTSIDE) {
                notAirPockets.addAll(checkResult.points());
            }
        }
    }

    private CheckResult isAirPocket(Point origin) {
        Set<Point> visited = new HashSet<>();
        Queue<Point> queue = new ArrayDeque<>();
        queue.add(origin);
        visited.add(origin);
        while (!queue.isEmpty()) {
            var point = queue.remove();
            if (isOutside(point)) {
                visited.addAll(queue);
                return new CheckResult(ResultType.OUTSIDE, visited);
            }
            var adjacent = getAirNeighbours(point);
            for (Point neighbour : adjacent) {
                if (!visited.contains(neighbour)) {
                    visited.add(neighbour);
                    queue.add(neighbour);
                }
            }
        }
        return new CheckResult(ResultType.POCkET, visited);
    }

    private record CheckResult(ResultType resultType, Set<Point> points) {}

    private enum ResultType {
        POCkET,
        OUTSIDE
    }

    private final Set<Point> airPockets = new HashSet<>();
    private final Set<Point> notAirPockets = new HashSet<>();

    @Override
    public Object solvePart2(List<String> input, boolean example) {
        int faces = 0;
        for (Point point : points) {
            var neighbours = getAirNeighbours(point);
            for (Point neighbour : neighbours) {
                checkForAirPocket(neighbour);
                if (notAirPockets.contains(neighbour)) {
                    faces++;
                }
            }
        }
        return faces;
    }
}
