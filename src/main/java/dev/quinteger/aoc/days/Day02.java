package dev.quinteger.aoc.days;

import dev.quinteger.aoc.Solution;

import java.util.List;
import java.util.function.BiFunction;

public class Day02 extends Solution {
    public Day02(List<String> input) {
        super(input);
    }

    @Override
    public Object solvePart1() {
        return solveWithMoveSelector((move, c) -> Move.getResponseMove(c));
    }

    @Override
    public Object solvePart2() {
        return solveWithMoveSelector(Move::pickMoveForOutcome);
    }

    public Object solveWithMoveSelector(BiFunction<? super Move, ? super Character, ? extends Move> responseMoveSelector) {
        return input.stream()
                .map(String::toCharArray)
                .map(chars -> {
                    var opponentMove = Move.getOpponentMove(chars[0]);
                    var responseMove = responseMoveSelector.apply(opponentMove, chars[2]);
                    return responseMove.getScore() + opponentMove.defendWith(responseMove);
                }).mapToInt(Integer::intValue)
                .sum();
    }

    private enum Move {
        ROCK,
        PAPER,
        SCISSORS;

        public int getScore() {
            return ordinal() + 1;
        }

        public int defendWith(Move response) {
            return switch (this) {
                case ROCK -> response == PAPER ? 6 : response == SCISSORS ? 0 : 3;
                case PAPER -> response == SCISSORS ? 6 : response == ROCK ? 0 : 3;
                case SCISSORS -> response == ROCK ? 6 : response == PAPER ? 0 : 3;
            };
        }

        public Move pickMoveForOutcome(char outcome) {
            return switch (outcome) {
                case 'X' -> this == ROCK ? SCISSORS : this == SCISSORS ? PAPER : ROCK;
                case 'Y' -> this;
                case 'Z' -> this == ROCK ? PAPER : this == SCISSORS ? ROCK : SCISSORS;
                default -> throw new IllegalArgumentException();
            };
        }

        public static Move getOpponentMove(char move) {
            return switch (move) {
                case 'A' -> ROCK;
                case 'B' -> PAPER;
                case 'C' -> SCISSORS;
                default -> throw new IllegalArgumentException();
            };
        }

        public static Move getResponseMove(char move) {
            return switch (move) {
                case 'X' -> ROCK;
                case 'Y' -> PAPER;
                case 'Z' -> SCISSORS;
                default -> throw new IllegalArgumentException();
            };
        }
    }
}
