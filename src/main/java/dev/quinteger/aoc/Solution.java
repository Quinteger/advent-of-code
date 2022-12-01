package dev.quinteger.aoc;

import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public abstract class Solution {
    private static final Pattern DAY_PATTERN = Pattern.compile("^Day(\\d{2})$");
    protected final List<String> input;
    private final int dayNumber;

    public Solution(List<String> input) {
        this.input = input;
        var classname = this.getClass().getSimpleName();
        var matcher = DAY_PATTERN.matcher(classname);
        if (matcher.matches() && matcher.groupCount() > 0) {
            var dayNumberString = matcher.group(1);
            this.dayNumber = Integer.parseInt(dayNumberString);
        } else {
            this.dayNumber = 0;
        }
    }

    public void solve() {
        if (dayNumber > 0) {
            System.out.println("Firing solution for day " + dayNumber);
        }

        measureAndLog(this::solvePart1, 1);
        measureAndLog(this::solvePart2, 2);
    }

    public abstract Object solvePart1();

    public abstract Object solvePart2();

    private static void measureAndLog(Supplier<?> supplier, int part) {
        long start = System.nanoTime();
        Object result = supplier.get();
        long end = System.nanoTime();

        System.out.printf("Part %d answer: %s, solved in %s%n", part, result, formatNanos(end - start));
    }

    private static String formatNanos(long nanos) {
        if (nanos > 1_000_000_000) {
            return "%.3fs".formatted(nanos / 1e9D);
        } else {
            return "%.3fms".formatted(nanos / 1e6D);
        }
    }
}
