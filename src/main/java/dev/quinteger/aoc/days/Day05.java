package dev.quinteger.aoc.days;

import dev.quinteger.aoc.Solution;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.regex.Pattern;

public class Day05 extends Solution {
    private static final int STACK_COUNT = 9;
    private static final int MOVE_START_LINE = 11;

    private static final Pattern MOVE_PATTERN = Pattern.compile("^move (\\d+) from (\\d+) to (\\d+)$");

    private interface StackMover {
        void move(Deque<Character> from, Deque<Character> to, int amount);
    }

    @Override
    public Object solvePart1(List<String> input, boolean example) {
        return solveWithStackMover(input, (stackFrom, stackTo, amount) -> {
            for (int i = 0; i < amount; i++) {
                stackTo.addFirst(stackFrom.removeFirst());
            }
        });
    }

    @Override
    public Object solvePart2(List<String> input, boolean example) {
        return solveWithStackMover(input, (stackFrom, stackTo, amount) -> {
            var movedStack = new ArrayDeque<Character>();
            for (int i = 0; i < amount; i++) {
                movedStack.addLast(stackFrom.removeFirst());
            }
            int size = movedStack.size();
            for (int i = 0; i < size; i++) {
                stackTo.addFirst(movedStack.removeLast());
            }
        });
    }

    private Object solveWithStackMover(List<String> input, StackMover stackMover) {
        List<Deque<Character>> stacks = new ArrayList<>(STACK_COUNT);
        for (int i = 0; i < STACK_COUNT; i++) {
            stacks.add(new ArrayDeque<>());
        }
        for (String line : input.subList(0, MOVE_START_LINE - 3)) {
            var chars = line.toCharArray();
            int j = 0;
            for (int i = 0; i < chars.length; i+=4) {
                char c = chars[i];
                if (c != ' ') {
                    stacks.get(j).addLast(chars[i + 1]);
                }
                j++;
            }
        }
        for (String line : input.subList(MOVE_START_LINE - 1, input.size())) {
            var matcher = MOVE_PATTERN.matcher(line);
            if (matcher.matches() && matcher.groupCount() == 3) {
                stackMover.move(
                        stacks.get(Integer.parseInt(matcher.group(2)) - 1),
                        stacks.get(Integer.parseInt(matcher.group(3)) - 1),
                        Integer.parseInt(matcher.group(1)));
            }
        }
        var sb = new StringBuilder();
        for (var stack : stacks) {
            sb.append(stack.getFirst());
        }
        return sb.toString();
    }
}
