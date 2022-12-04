package dev.quinteger.aoc.days;

import dev.quinteger.aoc.Solution;

import java.util.List;
import java.util.regex.Pattern;

public class Day04 extends Solution {
    public Day04(List<String> input) {
        super(input);
    }

    private static final Pattern PATTERN = Pattern.compile("(\\d++)-(\\d++),(\\d++)-(\\d++)");

    @Override
    public Object solvePart1() {
        int sum = 0;
        for (String pair : input) {
            var matcher = PATTERN.matcher(pair);
            if (matcher.matches() && matcher.groupCount() >= 4) {
                int pair1start = Integer.parseInt(matcher.group(1));
                int pair1end = Integer.parseInt(matcher.group(2));
                int pair2start = Integer.parseInt(matcher.group(3));
                int pair2end = Integer.parseInt(matcher.group(4));
                if ((pair1start >= pair2start && pair1end <= pair2end) || (pair2start >= pair1start && pair2end <= pair1end)) {
                    sum++;
                }
            } else {
                throw new RuntimeException();
            }
        }
        return sum;
    }

    @Override
    public Object solvePart2() {
        int sum = 0;
        for (String pair : input) {
            var matcher = PATTERN.matcher(pair);
            if (matcher.matches() && matcher.groupCount() >= 4) {
                int pair1start = Integer.parseInt(matcher.group(1));
                int pair1end = Integer.parseInt(matcher.group(2));
                int pair2start = Integer.parseInt(matcher.group(3));
                int pair2end = Integer.parseInt(matcher.group(4));
                if ((pair1start >= pair2start && pair1start <= pair2end)
                        || (pair1end >= pair2start && pair1end <= pair2end)
                        || (pair2start >= pair1start && pair2start <= pair1end)
                        || (pair2end >= pair1start && pair2end <= pair1end)) {
                    sum++;
                }
            } else {
                throw new RuntimeException();
            }
        }
        return sum;
    }
}
