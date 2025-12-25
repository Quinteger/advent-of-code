package dev.quinteger.aoc.solutions.y2025;

import dev.quinteger.aoc.solutions.Solution;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Day07 extends Solution {
    @Override
    public Object solvePart1(List<String> input, boolean example) {
        String firstLine = input.getFirst();
        int length = firstLine.length();
        State[] states = new State[length];
        for (int j = 0; j < length; j++) {
            char c = firstLine.charAt(j);
            switch (c) {
                case 'S' -> states[j] = State.BEAM;
                case '.' -> states[j] = State.EMPTY;
                default -> throw new IllegalStateException();
            }
        }
        int splits = 0;
        for (int i = 1; i < input.size(); i++) {
            String line = input.get(i);
            for (int j = 0; j < length; j++) {
                State previousState = states[j];
                char c = line.charAt(j);
                switch (c) {
                    case '.' -> states[j] = (previousState == State.BEAM ? State.BEAM : State.EMPTY);
                    case '^' -> {
                        states[j] = State.SPLITTER;
                        if (previousState == State.BEAM) {
                            
                            splits++;
                            if (j > 0) {
                                int previous = j - 1;
                                if (states[previous] != State.SPLITTER) {
                                    states[previous] = State.BEAM;
                                }
                            }
                            if (j < length - 1) {
                                int next = j + 1;
                                states[next] = State.BEAM;
                            }
                        }
                    }
                    default -> throw new IllegalStateException();
                }
            }
        }
        return splits;
    }

    @Override
    public Object solvePart2(List<String> input, boolean example) {
        String firstLine = input.getFirst();
        int length = firstLine.length();
        long[] states = new long[length];
        for (String line : input) {
            for (int i = 0; i < length; i++) {
                char c = line.charAt(i);
                switch (c) {
                    case 'S' -> {
                        states[i] = 1;
                    }
                    case '.' -> {
                        if (states[i] < 0) {
                            states[i] = 0;
                        }
                    }
                    case '^' -> {
                        long incomingState = states[i];
                        if (incomingState > 0) {
                            if (i > 0) {
                                int previousIndex = i - 1;
                                long previousState = states[previousIndex];
                                if (previousState >= 0) {
                                    states[previousIndex] = previousState + incomingState;
                                }
                            }
                            if (i < length - 1) {
                                int nextIndex = i + 1;
                                long nextState = states[nextIndex];
                                if (nextState >= 0) {
                                    states[nextIndex] = nextState + incomingState;
                                }
                            }
                        }
                        states[i] = -1;
                    }
                    default -> throw new IllegalStateException();
                }   
            }
        }
        return Arrays.stream(states).sum();
    }

    private enum State {
        EMPTY,
        BEAM,
        SPLITTER
        ;
    }
}
