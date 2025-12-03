package dev.quinteger.aoc.solutions.y2022;

import dev.quinteger.aoc.solutions.Solution;

import java.util.List;

public class Day08 extends Solution {
    private int[][] trees;
    private int count = 0;
    private int score = 0;

    private void createTrees(List<String> input) {
        trees = new int[input.size()][input.get(0).length()];
        for (int i = 0; i < input.size(); i++) {
            String line = input.get(i);
            for (int j = 0; j < line.length(); j++) {
                trees[i][j] = line.charAt(j) - '0';
            }
        }
    }

    @Override
    public Object solvePart1(List<String> input, boolean example) {
        createTrees(input);
        for (int row = 0; row < trees.length; row++) {
            for (int column = 0; column < trees[row].length; column++) {
                var treeInfo = getTreeInfo(trees, row, column);
                if (treeInfo.visible()){
                    count++;
                }
                int newScore = treeInfo.score();
                if (newScore > score) {
                    score = newScore;
                }
            }
        }
        return count;
    }

    private record TreeInfo(boolean visible, int score) {}

    private static TreeInfo getTreeInfo(int[][] trees, int row, int column) {
        int tree = trees[row][column];

        boolean topVisible = true;
        int topScore = 0;
        for (int newRow = row - 1; newRow >= 0; newRow--) {
            topScore++;
            if (trees[newRow][column] >= tree) {
                topVisible = false;
                break;
            }
        }

        boolean bottomVisible = true;
        int bottomScore = 0;
        for (int newRow = row + 1; newRow < trees.length; newRow++) {
            bottomScore++;
            if (trees[newRow][column] >= tree) {
                bottomVisible = false;
                break;
            }
        }

        boolean leftVisible = true;
        int leftScore = 0;
        for (int newColumn = column - 1; newColumn >= 0; newColumn--) {
            leftScore++;
            if (trees[row][newColumn] >= tree) {
                leftVisible = false;
                break;
            }
        }

        boolean rightVisible = true;
        int rightScore = 0;
        for (int newColumn = column + 1; newColumn < trees[row].length; newColumn++) {
            rightScore++;
            if (trees[row][newColumn] >= tree) {
                rightVisible = false;
                break;
            }
        }

        return new TreeInfo(topVisible || bottomVisible || leftVisible || rightVisible,
                topScore * bottomScore * leftScore * rightScore);
    }

    @Override
    public Object solvePart2(List<String> input, boolean example) {
        return score;
    }
}
