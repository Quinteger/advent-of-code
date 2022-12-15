package dev.quinteger.aoc.days;

import dev.quinteger.aoc.Solution;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Day15 extends Solution {
    public Day15(List<String> input) {
        super(input);
    }

    private static final Pattern pattern = Pattern.compile("^Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)$");

    private void fillMap() {
        for (String line : input) {
            var matcher = pattern.matcher(line);
            if (matcher.find() && matcher.groupCount() == 4) {
                int sensorX = Integer.parseInt(matcher.group(1));
                int sensorY = Integer.parseInt(matcher.group(2));
                int beaconX = Integer.parseInt(matcher.group(3));
                int beaconY = Integer.parseInt(matcher.group(4));
                int manhattan = Math.abs(sensorX - beaconX) + Math.abs(sensorY - beaconY);
                sensors.put(new Point(sensorX, sensorY), manhattan);
                beacons.add(new Point(beaconX, beaconY));
            } else {
                throw new RuntimeException();
            }
        }
        minX = sensors.entrySet().stream().mapToInt(e -> e.getKey().x() - e.getValue()).min().orElseThrow(RuntimeException::new);
        maxX = sensors.entrySet().stream().mapToInt(e -> e.getKey().x() + e.getValue()).max().orElseThrow(RuntimeException::new);
    }

    private final Map<Point, Integer> sensors = new HashMap<>();
    private final Set<Point> beacons = new HashSet<>();

    private int minX;
    private int maxX;

    @Override
    public Object solvePart1() {
        fillMap();
        int row = 2000000;
        int count = 0;
        for (int i = minX; i <= maxX; i++) {
            var point = new Point(i, row);
            char c = getPosition(point);
            if (c == '.') {
                count++;
            }
        }
        return count;
    }

    private char getPosition(Point point) {
        if (beacons.contains(point)) {
            return 'B';
        }
        if (sensors.containsKey(point)) {
            return 'S';
        }
        return sensors.entrySet().stream().filter(e -> {
            var sensor = e.getKey();
            int distance = e.getValue();
            return Math.abs(point.x() - sensor.x()) + Math.abs(point.y() - sensor.y()) <= distance;
        }).findFirst().map(e -> '.').orElse('?');
    }

    @Override
    public Object solvePart2() {
        final int min = 0;
        final int max = 4000000;
        return IntStream.rangeClosed(min, max)
                .mapToObj(i -> {
                    var previousPoint = new Point(min, i);
                    var nextPoint = previousPoint;
                    do {
                        previousPoint = nextPoint;
                        nextPoint = skipScanned(nextPoint);
                    } while (!nextPoint.equals(previousPoint) && nextPoint.x() <= max);
                    if (nextPoint.x() <= max) {
                        return nextPoint;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .findFirst()
                .map(p -> p.x() * 4000000L + p.y())
                .orElse(null);
    }

    private Point skipScanned(Point point) {
        for (var entry : sensors.entrySet()) {
            var sensor = entry.getKey();
            int manhattan = entry.getValue();
            int distanceY = Math.abs(sensor.y() - point.y());
            if (distanceY > manhattan) {
                continue;
            }
            int manhattanX = manhattan - distanceY;
            int distanceX = Math.abs(sensor.x() - point.x());
            if (distanceX > manhattanX) {
                continue;
            }
            return new Point(sensor.x() + manhattanX + 1, point.y());
        }
        return point;
    }

    private record Point(int x, int y) {}
}
