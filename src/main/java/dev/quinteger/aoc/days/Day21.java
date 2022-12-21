package dev.quinteger.aoc.days;

import dev.quinteger.aoc.Solution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Day21 extends Solution {
    private static final Pattern pattern = Pattern.compile("^(\\w{4}): (.*)$");

    private final Map<String, String> monkeyInputs = new HashMap<>();
    private final Map<String, Long> monkeyNumbers = new HashMap<>();

    private void parseInputs(List<String> input) {
        for (String line : input) {
            var matcher = pattern.matcher(line);
            if (matcher.matches() || matcher.groupCount() == 2) {
                monkeyInputs.put(matcher.group(1), matcher.group(2));
            }
        }
//        System.out.println(monkeyInputs);
    }

    @Override
    public Object solvePart1(List<String> input, boolean example) {
        parseInputs(input);
        return getForMonkey("root");
    }

    private long getForMonkey(String monkey) {
        var value = monkeyNumbers.get(monkey);
        if (value != null) {
            return value;
        }

        var input = monkeyInputs.get(monkey);
        if (input.matches("^\\d+$")) {
            long newValue = Long.parseLong(input);
            monkeyNumbers.put(monkey, newValue);
            return newValue;
        }

        var split = input.split(" ");
        long value1 = getForMonkey(split[0]);
        long value2 = getForMonkey(split[2]);

        long computedValue = switch (split[1]) {
            case "+" -> value1 + value2;
            case "-" -> value1 - value2;
            case "*" -> value1 * value2;
            case "/" -> value1 / value2;
            default -> throw new RuntimeException();
        };
        monkeyNumbers.put(monkey, computedValue);
        return computedValue;
    }

    private static class Monkey {
        final String name;
        final Monkey left;
        final Monkey right;
        final String operation;
        Long value;

        public Monkey(String name) {
            this(name, null, null, null, null);
        }

        public Monkey(String name, Long value) {
            this(name, null, null, null, value);
        }

        public Monkey(String name, Monkey left, Monkey right, String operation, Long value) {
            this.name = name;
            this.left = left;
            this.right = right;
            this.operation = operation;
            this.value = value;
        }

        public long solve() {
            if (left.value == null) {
                return left.enforceValue(right.value);
            } else if (right.value == null) {
                return right.enforceValue(left.value);
            } else {
                throw new RuntimeException();
            }
        }

        private long enforceValue(long value) {
            if (left == null && right == null && this.value == null) {
                return value;
            }

            if (left != null && right != null) {
                return switch (operation) {
                    case "+" -> left.value == null ? left.enforceValue(value - right.value) : right.enforceValue(value - left.value);
                    case "-" -> left.value == null ? left.enforceValue(value + right.value) : right.enforceValue(left.value - value);
                    case "*" -> left.value == null ? left.enforceValue(value / right.value) : right.enforceValue(value / left.value);
                    case "/" -> left.value == null ? left.enforceValue(value * right.value) : right.enforceValue(left.value / value);
                    default -> throw new RuntimeException();
                };
            } else {
                throw new RuntimeException();
            }
        }
    }

    private Monkey parseMonkey(String name) {
        if (name.equals("humn")) {
            return new Monkey(name);
        }

        var input = monkeyInputs.get(name);
        if (input.matches("\\d+")) {
            return new Monkey(name, Long.parseLong(input));
        }

        var split = monkeyInputs.get(name).split(" ");
        var left = parseMonkey(split[0]);
        var right = parseMonkey(split[2]);
        Long leftValue = left.value;
        Long rightValue = right.value;
        String operation = split[1];
        if (leftValue == null || rightValue == null) {
            return new Monkey(name, left, right, operation, null);
        }
        Long computedValue = switch (operation) {
            case "+" -> leftValue + rightValue;
            case "-" -> leftValue - rightValue;
            case "*" -> leftValue * rightValue;
            case "/" -> leftValue / rightValue;
            default -> throw new RuntimeException();
        };
        return new Monkey(name, left, right, operation, computedValue);
    }

    @Override
    public Object solvePart2(List<String> input, boolean example) {
        return parseMonkey("root").solve();
    }
}
