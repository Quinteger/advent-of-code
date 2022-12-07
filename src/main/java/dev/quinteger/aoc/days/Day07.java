package dev.quinteger.aoc.days;

import dev.quinteger.aoc.Solution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day07 extends Solution {
    public Day07(List<String> input) {
        super(input);
    }

    private final Map<String, Integer> dirs = new HashMap<>();

    private static final int SIZE_THRESHOLD = 100_000;

    @Override
    public Object solvePart1() {
        var currentDir = "/";
        for (String line : input) {
            var split = line.split(" ");
            if (split[1].equals("cd")) {
                if (split[2].charAt(0) == '/') {
                    currentDir = split[2];
                } else if (split[2].equals("..")) {
                    var lastIndex = currentDir.lastIndexOf('/');
                    if (lastIndex == 0) {
                        currentDir = "/";
                    } else {
                        currentDir = currentDir.substring(0, lastIndex);
                    }
                } else {
                    if (currentDir.charAt(currentDir.length() - 1) == '/') {
                        currentDir = currentDir + split[2];
                    } else {
                        currentDir = currentDir + "/" + split[2];
                    }
                }
                dirs.putIfAbsent(currentDir, 0);
            } else if (split[0].matches("\\d+")) {
                for (Map.Entry<String, Integer> e : dirs.entrySet()) {
                    if (currentDir.startsWith(e.getKey())) {
                        e.setValue(e.getValue() + Integer.parseInt(split[0]));
                    }
                }
            }
        }
        return dirs.values().stream().filter(integer -> integer <= SIZE_THRESHOLD).mapToInt(Integer::intValue).sum();
    }

    private static final int TOTAL_SIZE = 70_000_000;
    private static final int REQUIRED = 30_000_000;

    @Override
    public Object solvePart2() {
        int unused = TOTAL_SIZE - dirs.get("/");
        int needToDelete = REQUIRED - unused;
        return dirs.entrySet().stream()
                .filter(e -> e.getValue() >= needToDelete)
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getValue)
                .orElseThrow(RuntimeException::new);
    }
}
