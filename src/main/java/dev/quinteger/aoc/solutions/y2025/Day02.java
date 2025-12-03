package dev.quinteger.aoc.solutions.y2025;

import dev.quinteger.aoc.solutions.Solution;

import java.util.Arrays;
import java.util.List;
import java.util.stream.LongStream;

public class Day02 extends Solution {

    private static final long[] powersOfTen = LongStream.rangeClosed(0, 15).map(n -> {
        long result = 1;
        while (n > 0) {
            result *= 10;
            n--;
        }
        return result;
    }).toArray();

    @Override
    public Object solvePart1(List<String> input, boolean example) {
        List<Range> ranges = transformInputIntoRanges(input);
        long sum = 0;
        for (Range range : ranges) {
            long left = range.left();
            long right = range.right();
            for (long number = left; number <= right; number++) {
                int digits = countDigitsInNonNegativeNumber(number);
                if (digits % 2 != 0) {
                    continue;
                }
                int halfDigits = digits / 2;
                long divisor = getPowerOfTen(halfDigits);
                long small = number % divisor;
                long big = (number - small) / divisor;
                if (big == small) {
                    sum += number;
                }
            }
        }
        return sum;
    }

    @Override
    public Object solvePart2(List<String> input, boolean example) {
        List<Range> ranges = transformInputIntoRanges(input);
        long sum = 0;
        for (Range range : ranges) {
            long left = range.left();
            long right = range.right();
            for (long number = left; number <= right; number++) {
                int digits = countDigitsInNonNegativeNumber(number);
                long previousPart = -1;
                boolean isInvalid = false;
                for (int digitDivisor = 1; digitDivisor <= digits / 2; digitDivisor++) {
                    if (digits % digitDivisor == 0) {
                        long numberCopy = number;
                        while (numberCopy > 0) {
                            long divisor = getPowerOfTen(digitDivisor);
                            long nextPart = numberCopy % divisor;
                            numberCopy = (numberCopy - nextPart) / divisor;
                            if (previousPart >= 0 && nextPart != previousPart) {
                                isInvalid = false;
                                break;
                            } else {
                                previousPart = nextPart;
                                isInvalid = true;
                            }
                        }
                    }
                    if (isInvalid) {
                        // no need to check other divisors
                        sum += number;
                        break;
                    } else {
                        previousPart = -1;
                    }
                }
            }
        }
        return sum;
    }

    private static List<Range> transformInputIntoRanges(List<String> input) {
        return Arrays.stream(input.getFirst()
                        .split(","))
                .map(rangeString -> rangeString.split("-"))
                .peek(rangeArray -> {
                    if (rangeArray.length != 2) {
                        throw new IllegalStateException();
                    }
                })
                .map(rangeArray -> new Range(Long.parseLong(rangeArray[0]), Long.parseLong(rangeArray[1])))
                .toList();
    }

    private static boolean hasEvenNumberOfDigits(long number) {
        return countDigitsInNonNegativeNumber(number) % 2 == 0;
    }

    private static int countDigitsInNonNegativeNumber(long number) {
        if (number < 0) {
            throw new IllegalArgumentException("Number is negative");
        } else if (number == 0) {
            return 1;
        }
        int count = 0;
        while (number > 0) {
            number /= 10;
            count++;
        }
        return count;
    }

    private static long getPowerOfTen(int numberOfDigits) {
        return powersOfTen[numberOfDigits];
    }

    private record Range(long left, long right) {}
}
