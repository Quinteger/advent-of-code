package dev.quinteger.aoc.solutions.y2025;

import dev.quinteger.aoc.solutions.Solution;
import dev.quinteger.aoc.util.Point2D;
import dev.quinteger.aoc.util.ListExtensionsKt;

import java.util.*;

public class Day09 extends Solution {
    
    private final List<Point2D> points;

    public Day09(List<String> input) {
        this.points = ListExtensionsKt.splitLinesIntoTwoIntegers(input, ',', Point2D::new);
    }

    @Override
    public Object solvePart1(List<String> input, boolean example) {
        long maxArea = 0;
        for (int i = 0; i < points.size(); i++) {
            Point2D p1 = points.get(i);
            for (int j = i + 1; j < points.size(); j++) {
                Point2D p2 = points.get(j);
                long area = p1.areaToInclusive(p2);
                if (area > maxArea) {
                    maxArea = area;
                }
            }
        }
        return maxArea;
    }

    @Override
    public Object solvePart2(List<String> input, boolean example) {
        for (int i = 0; i < points.size(); i++) {
            Point2D point = points.get(i);
            Point2D previousPoint = ListExtensionsKt.previousWrappingAround(points, i);
            Point2D nextPoint = ListExtensionsKt.nextWrappingAround(points, i);
        }
        return null;
    }
}
