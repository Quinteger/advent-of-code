package dev.quinteger.aoc.days;

import dev.quinteger.aoc.Solution;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.regex.Pattern;

public class Day05 extends Solution {
    public Day05(List<String> input) {
        super(input);
    }

    private static final Pattern MOVE_PATTERN = Pattern.compile("^move (\\d+) from (\\d) to (\\d)$");

    @Override
    public Object solvePart1() {
        List<Deque<Character>> stacks = new ArrayList<>(9);
        for (int i = 0; i < 9; i++) {
            stacks.add(new ArrayDeque<>());
        }
        for (String line : input.subList(0, 8)) {
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
        for (String line : input.subList(10, input.size())) {
            var matcher = MOVE_PATTERN.matcher(line);
            if (matcher.matches() && matcher.groupCount() == 3) {
                Deque<Character> stackFrom = stacks.get(Integer.parseInt(matcher.group(2)) - 1);
                Deque<Character> stackTo = stacks.get(Integer.parseInt(matcher.group(3)) - 1);
                for (int i = 0; i < Integer.parseInt(matcher.group(1)); i++) {
                    if (!stackFrom.isEmpty()) {
                        stackTo.addFirst(stackFrom.pollFirst());
                    } else {
                        break;
                    }
                }
            }
        }
        var sb = new StringBuilder();
        for (Deque<Character> stack : stacks) {
            if (!stack.isEmpty()) {
                sb.append(stack.getFirst());
            }
        }
        return sb.toString();
    }

    @Override
    public Object solvePart2() {
        List<Deque<Character>> stacks = new ArrayList<>(9);
        for (int i = 0; i < 9; i++) {
            stacks.add(new ArrayDeque<>());
        }
        for (String line : input.subList(0, 8)) {
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
        for (String line : input.subList(10, input.size())) {
            var matcher = MOVE_PATTERN.matcher(line);
            if (matcher.matches() && matcher.groupCount() == 3) {
                Deque<Character> stackFrom = stacks.get(Integer.parseInt(matcher.group(2)) - 1);
                Deque<Character> stackTo = stacks.get(Integer.parseInt(matcher.group(3)) - 1);
                Deque<Character> movedStack = new ArrayDeque<>();
                for (int i = 0; i < Integer.parseInt(matcher.group(1)); i++) {
                    if (!stackFrom.isEmpty()) {
                        movedStack.addLast(stackFrom.pollFirst());
                    } else {
                        break;
                    }
                }
                int size = movedStack.size();
                for (int i = 0; i < size; i++) {
                    stackTo.addFirst(movedStack.pollLast());
                }
            }
        }
        var sb = new StringBuilder();
        for (Deque<Character> stack : stacks) {
            if (!stack.isEmpty()) {
                sb.append(stack.getFirst());
            }
        }
        return sb.toString();
    }
}
