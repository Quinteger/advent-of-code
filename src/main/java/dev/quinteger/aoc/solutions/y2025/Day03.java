package dev.quinteger.aoc.solutions.y2025;

import dev.quinteger.aoc.solutions.Solution;

import java.util.List;
import java.util.stream.LongStream;

public class Day03 extends Solution {

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
        int sum = 0;
        for (String bank : input) {
            int maxFirstDigit = 0;
            int maxFirstDigitIndex = -1;
            for (int i = 0; i < bank.length() - 1; i++) {
                int digit = Character.digit(bank.charAt(i), 10);
                if (digit > maxFirstDigit) {
                    maxFirstDigit = digit;
                    maxFirstDigitIndex = i;
                }
                if (maxFirstDigit == 9) {
                    break;
                }
            }
            int maxSecondDigit = 0;
            for (int i = maxFirstDigitIndex + 1; i < bank.length(); i++) {
                int digit = Character.digit(bank.charAt(i), 10);
                if (digit > maxSecondDigit) {
                    maxSecondDigit = digit;
                }
                if (maxSecondDigit == 9) {
                    break;
                }
            }
            int power = 10 * maxFirstDigit + maxSecondDigit;
            sum += power;
        }
        return sum;
    }

    @Override
    public Object solvePart2(List<String> input, boolean example) {
        long sum = 0;
        for (String bank : input) {
            int previousDigitIndex = -1;
            long power = 0;
            for (int digitPowerOfTen = 11; digitPowerOfTen >= 0; digitPowerOfTen--) {
                int maxDigit = 0;
                int maxDigitIndex = -1;
                for (int i = bank.length() - digitPowerOfTen - 1; i >= previousDigitIndex + 1; i--) {
                    int digit = Character.digit(bank.charAt(i), 10);
                    if (digit >= maxDigit) {
                        maxDigit = digit;
                        maxDigitIndex = i;
                    }
                }
                long powerOfTen = getPowerOfTen(digitPowerOfTen);
                power += maxDigit * powerOfTen;
                previousDigitIndex = maxDigitIndex;
            }
            sum += power;
        }
        return sum;
    }

    private static long getPowerOfTen(int numberOfDigits) {
        return powersOfTen[numberOfDigits];
    }
}
