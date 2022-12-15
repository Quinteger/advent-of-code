package dev.quinteger.aoc.days;

import dev.quinteger.aoc.Solution;

import java.util.*;

public class Day10 extends Solution {
    private final Map<Integer, Integer> cycleToRegister = new TreeMap<>();

    @Override
    public Object solvePart1(List<String> input, boolean example) {
        int cycle = 0;
        int register = 1;
        int sum = 0;
        for (String line : input) {
            var split = line.split(" ");
            var op = split[0];
            int cycles;
            int toAdd;

            if (op.equals("noop")) {
                cycles = 1;
                toAdd = 0;
            } else if (op.equals("addx")) {
                cycles = 2;
                toAdd = Integer.parseInt(split[1]);
            } else {
                throw new RuntimeException();
            }

            for (int i = 0; i < cycles; i++) {
                cycle++;
                sum += add(cycle, register);
                cycleToRegister.put(cycle, register);
                if (i == 1) {
                    register += toAdd;
                }
            }
        }
        return sum;
    }

    private static int add(int cycle, int register) {
        if ((cycle - 20) % 40 == 0) {
            return cycle * register;
        }
        return 0;
    }

    @Override
    public Object solvePart2(List<String> input, boolean example) {
        cycleToRegister.forEach(Day10::draw);
        return "see above";
    }

    private static void draw(int cycle, int register) {
        int rowPosition = (cycle - 1) % 40;
        if (Math.abs(register - rowPosition) <= 1) {
            System.out.print("█");
        } else {
            System.out.print(" ");
        }
        if (cycle % 40 == 0) {
            System.out.println();
        }
    }
}
