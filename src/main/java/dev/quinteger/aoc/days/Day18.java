package dev.quinteger.aoc.days;

import dev.quinteger.aoc.Solution;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

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
            faces += 6 - getLavaNeighbourCount(point);
        }
        return faces;
    }

    private record Point(int x, int y, int z) {}

    private List<Point> getAllNeighbours(Point point) {
        List<Point> neighbours =  new ArrayList<>(6);
        for (int i = 0; i < 6; i++) {
            var neighbourPoint = switch (i) {
                case 0 -> new Point(point.x() - 1, point.y(), point.z());
                case 1 -> new Point(point.x() + 1, point.y(), point.z());
                case 2 -> new Point(point.x(), point.y() - 1, point.z());
                case 3 -> new Point(point.x(), point.y() + 1, point.z());
                case 4 -> new Point(point.x(), point.y(), point.z() - 1);
                case 5 -> new Point(point.x(), point.y(), point.z() + 1);
                default -> throw new RuntimeException();
            };
            neighbours.add(neighbourPoint);
        }
        return neighbours;
    }

    private List<Point> getAirNeighbours(Point point) {
//        return getAllNeighbours(point).stream().filter(p -> !points.contains(point)).collect(Collectors.toCollection(() -> new ArrayList<>(6)));
        var neighbours = getAllNeighbours(point);
        var airNeighbours = new ArrayList<Point>(6);
        for (Point neighbour : neighbours) {
            if (!points.contains(neighbour)) {
                airNeighbours.add(neighbour);
            }
        }
        return airNeighbours;
    }

    private int getLavaNeighbourCount(Point point) {
        int lavaNeighbours = 0;
        for (Point neighbour : getAllNeighbours(point)) {
            if (points.contains(neighbour)) {
                lavaNeighbours++;
            }
        }
        return lavaNeighbours;
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
