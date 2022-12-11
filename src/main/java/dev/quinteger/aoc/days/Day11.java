package dev.quinteger.aoc.days;

import dev.quinteger.aoc.Solution;

import java.util.*;

public class Day11 extends Solution {
    public Day11(List<String> input) {
        super(input);
    }

    private record Monkey(int id, Deque<Long> items, String operation, String operationValue, int divisionTest, int ifTrue, int ifFalse) {
        public Monkey(int id, List<Long> items, String operation, String operationValue, int divisionTest, int ifTrue, int ifFalse) {
            this(id, new ArrayDeque<>(items), operation, operationValue, divisionTest, ifTrue, ifFalse);
        }

        public boolean hasItems() {
            return !items.isEmpty();
        }

        public long removeFirstItem() {
            return items.removeFirst();
        }

        public void addLastItem(long item) {
            items.addLast(item);
        }
    }

    private final Map<Integer, Monkey> monkeys = new TreeMap<>();
    private final Map<Integer, Integer> inspections = new HashMap<>();

    @Override
    public Object solvePart1() {
        return solveWithRounds(20, false);
    }

    @Override
    public Object solvePart2() {
        return solveWithRounds(10000, true);
    }

    private void loadMonkeys() {
        inspections.clear();
        monkeys.clear();

        for (int i = 0; i < input.size(); i+=7) {
            var line1 = input.get(i).split(" ");
            var line2 = input.get(i + 1).split(" ");
            var items =  new ArrayList<Long>();
            for (int j = 4; j < line2.length; j++) {
                items.add(Long.parseLong(line2[j].substring(0, 2)));
            }
            var line3 = input.get(i + 2).split(" ");
            var line4 = input.get(i + 3).split(" ");
            var line5 = input.get(i + 4).split(" ");
            var line6 = input.get(i + 5).split(" ");

            int monkeyId = Integer.parseInt(line1[1].substring(0, 1));
            monkeys.put(monkeyId, new Monkey(
                    monkeyId,
                    items,
                    line3[6],
                    line3[7],
                    Integer.parseInt(line4[5]),
                    Integer.parseInt(line5[9]),
                    Integer.parseInt(line6[9])
            ));
        }
    }

    private Object solveWithRounds(int rounds, boolean optimize) {
        loadMonkeys();

        long lcm;

        if (optimize) {
            lcm = monkeys.values().stream()
                    .mapToLong(Monkey::divisionTest)
                    .reduce((a, b) -> a * b)
                    .orElseThrow();
        } else {
            lcm = Long.MAX_VALUE;
        }

        for (int round = 0; round < rounds; round++) {
            for (var monkeyEntry : monkeys.entrySet()) {
                int monkeyId = monkeyEntry.getKey();
                var monkey = monkeyEntry.getValue();
                while (monkey.hasItems()) {
                    long item = monkey.removeFirstItem();
                    long parsedOperationValue;
                    String operationValue = monkey.operationValue();
                    if (operationValue.equals("old")) {
                        parsedOperationValue = item;
                    } else {
                        parsedOperationValue = Long.parseLong(operationValue);
                    }
                    long newItem = switch (monkey.operation()) {
                        case "+" -> item + parsedOperationValue;
                        case "*" -> item * parsedOperationValue;
                        default -> throw new IllegalStateException("Unexpected operation: " + monkey.operation());
                    };
                    inspections.merge(monkeyId, 1, Integer::sum);
                    if (!optimize) {
                        newItem = newItem / 3;
                    }
                    int newMonkey = newItem % monkey.divisionTest() == 0 ? monkey.ifTrue() : monkey.ifFalse();
                    monkeys.get(newMonkey).addLastItem(newItem % lcm);
                }
            }
        }
        return inspections.values().stream()
                .sorted(Comparator.reverseOrder())
                .limit(2)
                .reduce(1L, (a, b) -> a * b, (a, b) -> a * b);
    }
}
