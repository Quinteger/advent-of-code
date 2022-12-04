package dev.quinteger.aoc.days;

import dev.quinteger.aoc.Solution;

import java.util.List;
import java.util.regex.Pattern;

public class Day04 extends Solution {
    public Day04(List<String> input) {
        super(input);
    }

    private static final Pattern PATTERN = Pattern.compile("^(\\d+)-(\\d+),(\\d+)-(\\d+)$");

    @FunctionalInterface
    private interface PairRangesPredicate {
        boolean test(int pair1start, int pair1end, int pair2start, int pair2end);
    }

    @Override
    public Object solvePart1() {
        return solveWithPredicate((p1s, p1e, p2s, p2e) -> (p2s <= p1s && p1e <= p2e) || (p1s <= p2s && p2e <= p1e));
    }

    @Override
    public Object solvePart2() {
        return solveWithPredicate((p1s, p1e, p2s, p2e) -> (p2s <= p1s && p1s <= p2e) || (p1s <= p2s && p2s <= p1e));
    }

    private Object solveWithPredicate(PairRangesPredicate predicate) {
        int count = 0;
        for (String pair : input) {
            var matcher = PATTERN.matcher(pair);
            if (matcher.matches() && matcher.groupCount() >= 4) {
                int pair1start = Integer.parseInt(matcher.group(1));
                int pair1end = Integer.parseInt(matcher.group(2));
                int pair2start = Integer.parseInt(matcher.group(3));
                int pair2end = Integer.parseInt(matcher.group(4));
                if (predicate.test(pair1start, pair1end, pair2start, pair2end)) {
                    count++;
                }
            } else {
                throw new RuntimeException();
            }
        }
        return count;
    }
}
