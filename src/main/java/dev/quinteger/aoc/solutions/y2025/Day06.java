package dev.quinteger.aoc.solutions.y2025;

import dev.quinteger.aoc.solutions.Solution;

import java.util.*;

public class Day06 extends Solution {
    @Override
    public Object solvePart1(List<String> input, boolean example) {
        List<long[]> args = input.stream()
                .takeWhile(line -> Character.isDigit(line.trim().charAt(0)))
                .map(line -> Arrays.stream(line.split("\\s+")).filter(string -> !string.isBlank()).mapToLong(Long::parseLong).toArray())
                .toList();
        int[] actions = input.stream()
                .dropWhile(line -> Character.isDigit(line.trim().charAt(0)))
                .findFirst()
                .map(line -> Arrays.stream(line.split("\\s+")).filter(string -> !string.isBlank()).mapToInt(string -> string.charAt(0)).toArray())
                .orElseThrow();
        long sum = 0;
        for (int i = 0; i < actions.length; i++) {
            int finalI = i;
            long[] argsToCompute = args.stream()
                    .mapToLong(list -> list[finalI])
                    .toArray();
            char action = (char) actions[i];
            long result = switch (action) {
                case '+' -> Arrays.stream(argsToCompute).reduce(0L, Long::sum);
                case '*' -> Arrays.stream(argsToCompute).reduce(1L, (l1, l2) -> l1 * l2);
                default -> throw new IllegalStateException();
            };
            sum += result;
        }
        return sum;
    }

    @Override
    public Object solvePart2(List<String> input, boolean example) {
        List<String> argsInput = input.stream()
                .takeWhile(line -> Character.isDigit(line.trim().charAt(0)))
                .toList();
        String actions = input.stream()
                .dropWhile(line -> Character.isDigit(line.trim().charAt(0)))
                .findFirst()
                .orElseThrow();
        long sum = 0;
        List<Long> currentNumbers = new ArrayList<>();
        char lastSeenAction = 0;
        for (int i = argsInput.getFirst().length() - 1; i >= 0; i--) {
            char action = actions.charAt(i);
            if (!Character.isWhitespace(action)) {
                lastSeenAction = action;
            }
            int finalI = i;
            int[] currentDigits = argsInput.stream()
                    .mapToInt(args -> args.charAt(finalI))
                    .filter(c -> !Character.isWhitespace(c))
                    .map(c -> Character.digit(c, 10))
                    .toArray();
            if (currentDigits.length == 0) {
                long[] argsToCompute = currentNumbers.stream()
                        .mapToLong(l -> l)
                        .toArray();
                long result = switch (lastSeenAction) {
                    case '+' -> Arrays.stream(argsToCompute).reduce(0L, Long::sum);
                    case '*' -> Arrays.stream(argsToCompute).reduce(1L, (l1, l2) -> l1 * l2);
                    default -> throw new IllegalStateException();
                };
                sum += result;
                currentNumbers.clear();
            } else {
                long number = 0;
                long multiplier = 1;
                for (int j = currentDigits.length - 1; j >= 0; j--) {
                    int digit = currentDigits[j];
                    number += digit * multiplier;
                    multiplier *= 10;
                }
                currentNumbers.add(number);
            }
        }
        if (!currentNumbers.isEmpty()) {
            long[] argsToCompute = currentNumbers.stream()
                    .mapToLong(l -> l)
                    .toArray();
            long result = switch (lastSeenAction) {
                case '+' -> Arrays.stream(argsToCompute).reduce(0L, Long::sum);
                case '*' -> Arrays.stream(argsToCompute).reduce(1L, (l1, l2) -> l1 * l2);
                default -> throw new IllegalStateException();
            };
            sum += result;
            currentNumbers.clear();
        }
        return sum;
    }

}
