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

    private static boolean isVisible(int[][] trees, int i, int j) {
        int tree = trees[i][j];

        boolean topVisible = true;
        for (int k = 0; k < i; k++) {
            if (trees[k][j] >= tree) {
                topVisible = false;
                break;
            }
        }

        boolean bottomVisible = true;
        for (int k = i + 1; k < trees.length; k++) {
            if (trees[k][j] >= tree) {
                bottomVisible = false;
                break;
            }
        }

        boolean leftVisible = true;
        for (int k = 0; k < j; k++) {
            if (trees[i][k] >= tree) {
                leftVisible = false;
                break;
            }
        }

        boolean rightVisible = true;
        for (int k = j + 1; k < trees[i].length; k++) {
            if (trees[i][k] >= tree) {
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

    private static int getScore(int[][] trees, int i, int j) {
        int tree = trees[i][j];

        int topScore = 0;
        for (int k = i - 1; k >=0; k--) {
            topScore++;
            if (trees[k][j] >= tree) {
                break;
            }
        }

        int bottomScore = 0;
        for (int k = i + 1; k < trees.length; k++) {
            bottomScore++;
            if (trees[k][j] >= tree) {
                break;
            }
        }

        int leftScore = 0;
        for (int k = j - 1; k >=0; k--) {
            leftScore++;
            if (trees[i][k] >= tree) {
                break;
            }
        }

        int rightScore = 0;
        for (int k = j + 1; k < trees[i].length; k++) {
            rightScore++;
            if (trees[i][k] >= tree) {
                break;
            }
        }

        return topScore * bottomScore * leftScore * rightScore;
    }
}
