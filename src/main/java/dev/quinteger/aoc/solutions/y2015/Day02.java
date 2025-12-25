package dev.quinteger.aoc.solutions.y2015;

import dev.quinteger.aoc.solutions.Solution;
import dev.quinteger.aoc.util.MathUtilsKt;
import dev.quinteger.aoc.util.ListExtensionsKt;

import java.util.ArrayList;
import java.util.List;

public class Day02 extends Solution {
    @Override
    public Object solvePart1(List<String> input, boolean example) {
        List<Box> boxes = ListExtensionsKt.splitLinesIntoThreeIntegers(input, 'x', Box::new);
        long paper = 0;
        for (Box box : boxes) {
            long area1 = box.x() * box.y();
            long area2 = box.y() * box.z();
            long area3 = box.z() * box.x();
            long minArea = MathUtilsKt.min(area1, area2, area3);
            paper += area1 * 2 + area2 * 2 + area3 * 2 + minArea;
        }
        return paper;
    }

    @Override
    public Object solvePart2(List<String> input, boolean example) {
        List<Box> boxes = ListExtensionsKt.splitLinesIntoThreeIntegers(input, 'x', Box::new);
        long ribbon = 0;
        List<Long> sides = new ArrayList<>();
        for (Box box : boxes) {
            sides.add(box.x());
            sides.add(box.y());
            sides.add(box.z());
            sides.sort(null);
            long shortestSide1 = sides.get(0);
            long shortestSide2 = sides.get(1);
            ribbon += shortestSide1 * 2 + shortestSide2 * 2 + box.x() * box.y() * box.z();
            sides.clear();
        }
        return ribbon;
    }
    
    private record Box(long x, long y, long z) {}
}
