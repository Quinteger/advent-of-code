package dev.quinteger.aoc.solutions.y2022;

import dev.quinteger.aoc.solutions.Solution;

import java.util.List;

public class Day25 extends Solution {
    @Override
    public Object solvePart1(List<String> input, boolean example) {
        var decimalSum = input.stream().mapToLong(this::fromSNAFU).sum();
        return toSNAFU(decimalSum);
    }

    private long fromSNAFU(String input) {
        var chars = input.toCharArray();
        long result = 0;
        for (int i = 0; i < chars.length; i++) {
            char c = chars[chars.length - i - 1];
            int multiplier = switch (c) {
                case '2' -> 2;
                case '1' -> 1;
                case '0' -> 0;
                case '-' -> -1;
                case '=' -> -2;
                default -> throw new RuntimeException();
            };
            result += exactPower(5, i) * multiplier;
        }
        return result;
    }

    private static long exactPower(int base, int exponent) {
        long result = 1;
        for (int i = 1; i <= exponent; i++) {
            result *= base;
        }
        return result;
    }

    private String toSNAFU(long decimal) {
        long current = 1;
        long next = 5;
        var builder = new StringBuilder();
        while (decimal > 0) {
            long mod = decimal % next;

            int correctedMod;
            if (mod == current * 2) {
                correctedMod = 2;
            } else if (mod == current) {
                correctedMod = 1;
            } else if (mod == 0) {
                correctedMod = 0;
            } else if (mod == next - current) {
                correctedMod = -1;
            } else if (mod == next - current * 2) {
                correctedMod = -2;
            } else {
                throw new RuntimeException();
            }

            char c = switch (correctedMod) {
                case 2 -> '2';
                case 1 -> '1';
                case 0 -> '0';
                case -1 -> '-';
                case -2 -> '=';
                default -> throw new RuntimeException();
            };
            builder.append(c);

            decimal -= current * correctedMod;
            current *= 5;
            next *= 5;
        }
        return builder.reverse().toString();
    }

    @Override
    public Object solvePart2(List<String> input, boolean example) {
        return null;
    }
}
