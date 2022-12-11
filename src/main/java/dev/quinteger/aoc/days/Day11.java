package dev.quinteger.aoc.days;

import dev.quinteger.aoc.Solution;

import java.util.*;

public class Day11 extends Solution {
    public Day11(List<String> input) {
        super(input);
    }

    private static class Monkey {
        private final int id;
        private final Deque<Long> items = new ArrayDeque<>();
        final String operation;
        final String operationValue;
        final int divisionTest;
        final int ifTrue;
        final int ifFalse;

        public Monkey(int id, List<Long> items, String operation, String operationValue, int divisionTest, int ifTrue, int ifFalse) {
            this.id = id;
            this.operation = operation;
            this.operationValue = operationValue;
            this.divisionTest = divisionTest;
            this.ifTrue = ifTrue;
            this.ifFalse = ifFalse;
            for (long item : items) {
                this.items.addLast(item);
            }
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

        @Override
        public String toString() {
            return "Monkey " + id;
        }
    }

    private static final Map<Integer, Monkey> monkeys = new TreeMap<>();
    private static final Map<Integer, Long> inspections = new HashMap<>();

    @Override
    public Object solvePart1() {
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
            System.out.println(Arrays.toString(line1));
            System.out.println(Arrays.toString(line2));
            System.out.println(Arrays.toString(line3));
            System.out.println(Arrays.toString(line4));
            System.out.println(Arrays.toString(line5));
            System.out.println(Arrays.toString(line6));
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

        System.out.println(monkeys);

        for (int round = 0; round < 20; round++) {
            for (var monkeyEntry : monkeys.entrySet()) {
                int monkeyId = monkeyEntry.getKey();
                var monkey = monkeyEntry.getValue();
                while (monkey.hasItems()) {
                    long item = monkey.removeFirstItem();
                    long value;
                    String operationValue = monkey.operationValue;
                    if (operationValue.equals("old")) {
                        value = item;
                    } else {
                        value = Long.parseLong(operationValue);
                    }
                    long newItem = switch (monkey.operation) {
                        case "+" -> item + value;
                        case "-" -> item - value;
                        case "*" -> item * value;
                        case "/" -> item / value;
                        default -> throw new IllegalStateException("Unexpected value: " + monkey.operation);
                    };
                    if (!inspections.containsKey(monkeyId)) {
                        inspections.put(monkeyId, 1L);
                    } else {
                        inspections.put(monkeyId, inspections.get(monkeyId) + 1);
                    }
                    newItem = newItem / 3;
                    if (newItem % monkey.divisionTest == 0) {
                        monkeys.get(monkey.ifTrue).addLastItem(newItem);
                    } else {
                        monkeys.get(monkey.ifFalse).addLastItem(newItem);
                    }
                }
            }
        }
        return inspections.values().stream().sorted(Comparator.reverseOrder()).limit(2).reduce(1L, (a, b) -> a * b);
    }

    @Override
    public Object solvePart2() {
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
            System.out.println(Arrays.toString(line1));
            System.out.println(Arrays.toString(line2));
            System.out.println(Arrays.toString(line3));
            System.out.println(Arrays.toString(line4));
            System.out.println(Arrays.toString(line5));
            System.out.println(Arrays.toString(line6));
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

        long gcd = monkeys.values().stream()
                .mapToLong(e -> e.divisionTest)
                .reduce((a, b) -> a * b)
                .orElseThrow();

        for (int round = 0; round < 10000; round++) {
            for (var monkeyEntry : monkeys.entrySet()) {
                int monkeyId = monkeyEntry.getKey();
                var monkey = monkeyEntry.getValue();
                while (monkey.hasItems()) {
                    long item = monkey.removeFirstItem();
                    long value;
                    String operationValue = monkey.operationValue;
                    if (operationValue.equals("old")) {
                        value = item;
                    } else {
                        value = Long.parseLong(operationValue);
                    }
                    long newItem = switch (monkey.operation) {
                        case "+" -> item + value;
                        case "-" -> item - value;
                        case "*" -> item * value;
                        case "/" -> item / value;
                        default -> throw new IllegalStateException("Unexpected value: " + monkey.operation);
                    };
                    if (!inspections.containsKey(monkeyId)) {
                        inspections.put(monkeyId, 1L);
                    } else {
                        inspections.put(monkeyId, inspections.get(monkeyId) + 1);
                    }
//                    newItem = newItem / 3;
                    if (newItem % monkey.divisionTest == 0) {
                        monkeys.get(monkey.ifTrue).addLastItem(newItem % gcd);
                    } else {
                        monkeys.get(monkey.ifFalse).addLastItem(newItem % gcd);
                    }
                }
            }
        }
        return inspections.values().stream().sorted(Comparator.reverseOrder()).limit(2).reduce(1L, (a, b) -> a * b);
    }
}
