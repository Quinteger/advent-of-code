package dev.quinteger.aoc.days;

import dev.quinteger.aoc.Solution;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class Day08 extends Solution {
    public Day08(List<String> input) {
        super(input);
        trees = new int[input.size()][input.get(0).length()];
        for (int i = 0; i < input.size(); i++) {
            String line = input.get(i);
            for (int j = 0; j < line.length(); j++) {
                trees[i][j] = line.charAt(j) - '0';
            }
        }
    }

    private final int[][] trees;

    @Override
    public Object solvePart1() {
        int count = 0;
        for (int i = 0; i < trees.length; i++) {
            for (int j = 0; j < trees[i].length; j++) {
                if (isVisible(trees, i, j)) {
                    count++;
                }
            }
        }
        return count;
    }

    private static boolean isVisible(int[][] trees, int row, int column) {
        int tree = trees[row][column];

        boolean topVisible = true;
        for (int k = 0; k < row; k++) {
            if (trees[k][column] >= tree) {
                topVisible = false;
                break;
            }
        }

        boolean bottomVisible = true;
        for (int k = row + 1; k < trees.length; k++) {
            if (trees[k][column] >= tree) {
                bottomVisible = false;
                break;
            }
        }

        boolean leftVisible = true;
        for (int k = 0; k < column; k++) {
            if (trees[row][k] >= tree) {
                leftVisible = false;
                break;
            }
        }

        boolean rightVisible = true;
        for (int k = column + 1; k < trees[row].length; k++) {
            if (trees[row][k] >= tree) {
                rightVisible = false;
                break;
            }
        }

        return topVisible || bottomVisible || leftVisible || rightVisible;
    }

    @Override
    public Object solvePart2() {
        SortedSet<Integer> scores = new TreeSet<>();
        for (int i = 0; i < trees.length; i++) {
            for (int j = 0; j < trees[i].length; j++) {
                scores.add(getScore(trees, i, j));
            }
        }
        return scores.last();
    }

    private static int getScore(int[][] trees, int row, int column) {
        int tree = trees[row][column];

        int topScore = 0;
        for (int k = row - 1; k >=0; k--) {
            topScore++;
            if (trees[k][column] >= tree) {
                break;
            }
        }

        int bottomScore = 0;
        for (int k = row + 1; k < trees.length; k++) {
            bottomScore++;
            if (trees[k][column] >= tree) {
                break;
            }
        }

        int leftScore = 0;
        for (int k = column - 1; k >=0; k--) {
            leftScore++;
            if (trees[row][k] >= tree) {
                break;
            }
        }

        int rightScore = 0;
        for (int k = column + 1; k < trees[row].length; k++) {
            rightScore++;
            if (trees[row][k] >= tree) {
                break;
            }
        }

        return topScore * bottomScore * leftScore * rightScore;
    }
}
