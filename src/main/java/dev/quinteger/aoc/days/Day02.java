package dev.quinteger.aoc.days;

import dev.quinteger.aoc.Solution;

import java.util.List;

public class Day02 extends Solution {
    public Day02(List<String> input) {
        super(input);
    }

    @Override
    public Object solvePart1() {
        return input.stream()
                .map(l -> l.split(" "))
                .map(stringPair -> {
                    var opponentMove = Move.getOpponentMove(stringPair[0]);
                    var responseMove = Move.getResponseMove(stringPair[1]);
                    return responseMove.score + opponentMove.defendWith(responseMove);
                }).mapToInt(Integer::intValue)
                .sum();
    }

    @Override
    public Object solvePart2() {
        return input.stream()
                .map(l -> l.split(" "))
                .map(stringPair -> {
                    var opponentMove = Move.getOpponentMove(stringPair[0]);
                    var responseMove = opponentMove.pickShapeForOutcome(stringPair[1]);
                    return responseMove.score + opponentMove.defendWith(responseMove);
                }).mapToInt(Integer::intValue)
                .sum();
    }

    private enum Move {
        ROCK(1),
        PAPER( 2),
        SCISSORS( 3);

        private final int score;

        Move(int score){
            this.score = score;
        }

        public int defendWith(Move response) {
            return switch (this) {
                case ROCK -> response == PAPER ? 6 : response == SCISSORS ? 0 : 3;
                case PAPER -> response == SCISSORS ? 6 : response == ROCK ? 0 : 3;
                case SCISSORS -> response == ROCK ? 6 : response == PAPER ? 0 : 3;
            };
        }

        public Move pickShapeForOutcome(String move) {
            return switch (move) {
                case "X" -> this == ROCK ? SCISSORS : this == SCISSORS ? PAPER : ROCK;
                case "Y" -> this == ROCK ? ROCK : this == SCISSORS ? SCISSORS : PAPER;
                case "Z" -> this == ROCK ? PAPER : this == SCISSORS ? ROCK : SCISSORS;
                default -> throw new IllegalArgumentException();
            };
        }

        public static Move getOpponentMove(String move) {
            return switch (move) {
                case "A" -> ROCK;
                case "B" -> PAPER;
                case "C" -> SCISSORS;
                default -> throw new IllegalArgumentException();
            };
        }

        public static Move getResponseMove(String move) {
            return switch (move) {
                case "X" -> ROCK;
                case "Y" -> PAPER;
                case "Z" -> SCISSORS;
                default -> throw new IllegalArgumentException();
            };
        }
    }
}
