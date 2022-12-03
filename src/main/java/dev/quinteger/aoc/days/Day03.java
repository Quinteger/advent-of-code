package dev.quinteger.aoc.days;

import dev.quinteger.aoc.Solution;

import java.util.List;
import java.util.stream.Collectors;

public class Day03 extends Solution {
    public Day03(List<String> input) {
        super(input);
    }

    @Override
    public Object solvePart1() {
        int sum = 0;
        for (String backpack : input) {
            var compartment1 = backpack.substring(0, backpack.length() / 2);
            var compartment2 = backpack.substring(backpack.length() / 2);
            var compartment1Set = compartment1.chars().boxed().collect(Collectors.toSet());
            int match = compartment2.chars()
                    .filter(compartment1Set::contains)
                    .findFirst()
                    .orElseThrow(RuntimeException::new);
            sum += getPriority(match);
        }
        return sum;
    }

    @Override
    public Object solvePart2() {
        int sum = 0;
        for (int i = 0; i < input.size(); i+=3) {
            var elf1Set = input.get(i).chars().boxed().collect(Collectors.toSet());
            var elf2Set = input.get(i + 1).chars().boxed().collect(Collectors.toSet());
            int match = input.get(i + 2).chars()
                    .filter(c -> elf1Set.contains(c) && elf2Set.contains(c))
                    .findFirst()
                    .orElseThrow(RuntimeException::new);
            sum += getPriority(match);
        }
        return sum;
    }

    private static int getPriority(int c) {
        if (c >= 'A' && c <= 'Z') {
            return c - 'A' + 27;
        } else if (c >= 'a' && c <= 'z') {
            return  c - 'a' + 1;
        } else {
            throw new IllegalArgumentException();
        }
    }
}
