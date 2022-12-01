package dev.quinteger.aoc;

import java.util.List;

public abstract class Solution {
    protected final List<String> input;

    public Solution(List<String> input) {
        this.input = input;
    }

    public void solve() {
        solvePart1();
        solvePart2();
    }

    public abstract void solvePart1();

    public abstract void solvePart2();
}
