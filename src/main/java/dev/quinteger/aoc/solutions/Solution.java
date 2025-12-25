package dev.quinteger.aoc.solutions;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class Solution {

    public void solve(
            List<String> part1Input,
            List<String> part2Input,
            boolean example,
            @Nullable String part1Answer,
            @Nullable String part2Answer
    ) {
        long start;
        long end;
        Object result;

        start = System.nanoTime();
        result = solvePart1(part1Input, example);
        end = System.nanoTime();
        if (part1Answer != null && !part1Answer.isEmpty() && !part1Answer.equals(String.valueOf(result))) {
            throw new RuntimeException("Wrong answer for part 1%s: expected %s, got %s".formatted(example ? " example" : "", part1Answer, result));
        } else {
            System.out.printf("Part 1 solved in %s, answer: %s%s%n", formatNanos(end - start), result, part1Answer == null || part1Answer.isEmpty() ? "" : " (correct)");
        }

        start = System.nanoTime();
        result = solvePart2(part2Input, example);
        end = System.nanoTime();
        if (part2Answer != null && !part2Answer.isEmpty() && !part2Answer.equals(String.valueOf(result))) {
            throw new RuntimeException("Wrong answer for part 2%s: expected %s, got %s".formatted(example ? " example" : "", part2Answer, result));
        } else {
            System.out.printf("Part 2 solved in %s, answer: %s%s%n", formatNanos(end - start), result, part2Answer == null || part2Answer.isEmpty() ? "" : " (correct)");
        }
    }

    public abstract Object solvePart1(List<String> input, boolean example);

    public abstract Object solvePart2(List<String> input, boolean example);

    private static String formatNanos(long nanos) {
        if (nanos > 1_000_000_000) {
            return "%.3fs".formatted(nanos / 1e9D);
        } else {
            return "%.3fms".formatted(nanos / 1e6D);
        }
    }
}
