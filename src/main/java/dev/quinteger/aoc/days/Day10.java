package dev.quinteger.aoc.days;

import dev.quinteger.aoc.Solution;

import java.util.List;

public class Day10 extends Solution {
    public Day10(List<String> input) {
        super(input);
    }

    @Override
    public Object solvePart1() {
        int cycle = 0;
        int register = 1;
        int sum = 0;
        for (String line : input) {
            var split = line.split(" ");
            var op = split[0];
            if (op.equals("noop")) {
                cycle++;
                sum += add(cycle, register);
            } else if (op.equals("addx")) {
                int toAdd = Integer.parseInt(split[1]);
                for (int i = 0; i < 2; i++) {
                    cycle++;
                    sum += add(cycle, register);
                    if (i == 1) {
                        register += toAdd;
                    }
                }
            } else {
                throw new RuntimeException();
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
    public Object solvePart2() {
        int cycle = 0;
        int register = 1;
        int sum = 0;
        for (String line : input) {
            var split = line.split(" ");
            var op = split[0];
            if (op.equals("noop")) {
                cycle++;
                sum += add(cycle, register);
                draw(register, cycle);
            } else if (op.equals("addx")) {
                int toAdd = Integer.parseInt(split[1]);
                for (int i = 0; i < 2; i++) {
                    cycle++;
                    sum += add(cycle, register);
                    draw(register, cycle);
                    if (i == 1) {
                        register += toAdd;
                    }
                }
            } else {
                throw new RuntimeException();
            }
        }
        return sum;
    }

    private static void draw(int register, int cycle) {
        int rowPosition = (cycle - 1) % 40;
        if (register >= rowPosition - 1 && register <= rowPosition + 1) {
            System.out.print("#");
        } else {
            System.out.print(".");
        }
        if (cycle % 40 == 0) {
            System.out.println();
        }
    }
}
