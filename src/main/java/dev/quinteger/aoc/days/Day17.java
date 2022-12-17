package dev.quinteger.aoc.days;

import dev.quinteger.aoc.Solution;

import java.util.List;

public class Day17 extends Solution {
    private static final int pieceCount = 2022;
    private char[][] grid;
    private int towerHeight = 0;

    @Override
    public Object solvePart1(List<String> input, boolean example) {
        initializeGrid();
        char[] inputs = input.get(0).toCharArray();
        for (int i = 0; i < 1; i++) {
            int spawnHeight = getSpawnHeight();
            resizeGridForHeight(spawnHeight);
            RockPiece piece = new HorizontalPiece(new Point(2, spawnHeight));
            for (char c : inputs) {
                if (c == '<') {
                    piece.tryMoveLeft(grid);
                } else if (c == '>') {
                    piece.tryMoveRight(grid);
                } else {
                    throw new RuntimeException("Incorrect input");
                }
                if (piece.tryMoveDown(grid)) {
                    break;
                }
            }
        }
        return null;
    }

    private int getSpawnHeight() {
        return towerHeight + 3;
    }

    private void initializeGrid() {
        grid = new char[5][9];
        grid[4] = new char[] {'#', '#', '#', '#', '#', '#', '#', '#', '#'};
        for (int i = 0; i < 4; i++) {
            grid[i] = new char[] {'#', '.', '.', '.', '.', '.', '.', '.', '#'};
        }
    }

    private void resizeGridForHeight(int height) {
        char[][] oldGrid = grid;
        int newHeight = Math.max(oldGrid.length, height + 2);
        if (newHeight <= oldGrid.length) {
            return;
        }
        char[][] newGrid = new char[newHeight][9];
        for (int i = 0; i < newHeight; i++) {
            if (i < oldGrid.length) {
                newGrid[newGrid.length - 1 - i] = oldGrid[oldGrid.length - 1 - i];
            } else {
                newGrid[newGrid.length - 1 - i] = new char[] {'#', '.', '.', '.', '.', '.', '.', '.', '#'};
            }
        }
        this.grid = newGrid;
    }

    @Override
    public Object solvePart2(List<String> input, boolean example) {
        return null;
    }

    private record Point(int x, int y) {}

    private interface RockPiece {
        int getLeftX();
        int getBottomY();
        int getBoundingWidth();
        int getBoundingHeight();
        boolean tryMoveDown(char[][] grid);
        boolean canMoveDown(char[][] grid);
        boolean tryMoveRight(char[][] grid);
        boolean canMoveRight(char[][] grid);
        boolean tryMoveLeft(char[][] grid);
        boolean canMoveLeft(char[][] grid);

        void addToGrid(char[][] grid);

        default int getGridRowUnder(char[][] grid) {
            return grid.length - 1 + getBottomY();
        }

        default int getGridColumnToTheLeft() {
            return getLeftX();
        }

        default int getGridColumnToTheRight() {
            return getLeftX() + getBoundingWidth() + 1;
        }
    }

    private static abstract class AbstractPiece implements RockPiece{
        protected int leftX;
        protected int bottomY;

        public AbstractPiece(int leftX, int bottomY) {
            this.leftX = leftX;
            this.bottomY = bottomY;
        }

        @Override
        public int getLeftX() {
            return leftX;
        }

        @Override
        public int getBottomY() {
            return bottomY;
        }

        public boolean tryMoveDown(char[][] grid) {
            if (canMoveDown(grid)) {
                bottomY--;
                return true;
            } else {
                return false;
            }
        }

        public boolean tryMoveRight(char[][] grid) {
            if (canMoveRight(grid)) {
                leftX++;
                return true;
            } else {
                return false;
            }
        }

        public boolean tryMoveLeft(char[][] grid) {
            if (canMoveLeft(grid)) {
                leftX--;
                return true;
            } else {
                return false;
            }
        }
    }

    private static class HorizontalPiece extends AbstractPiece {
        public HorizontalPiece(Point spawnPoint) {
            super(spawnPoint.x(), spawnPoint.y());
        }

        @Override
        public int getBoundingWidth() {
            return 4;
        }

        @Override
        public int getBoundingHeight() {
            return 1;
        }

        public boolean canMoveDown(char[][] grid) {
            for (int i = 0; i < getBoundingWidth(); i++) {
                if (grid[getGridRowUnder(grid)][getGridColumnToTheLeft() + 1 + i] != '.') {
                    return false;
                }
            }
            return true;
        }

        public boolean canMoveRight(char[][] grid) {
            return grid[getGridRowUnder(grid) - 1][getGridColumnToTheRight()] == '.';
        }

        public boolean canMoveLeft(char[][] grid) {
            return grid[getGridRowUnder(grid) - 1][getGridColumnToTheLeft()] == '.';
        }

        @Override
        public void addToGrid(char[][] grid) {
            int row = getGridRowUnder(grid) - 1;
            int column = getGridColumnToTheLeft() + 1;
            grid[row][column] = '@';
            grid[row][column + 1] = '@';
            grid[row][column + 2] = '@';
            grid[row][column + 3] = '@';
        }
    }

    private static class CrossPiece extends AbstractPiece {
        public CrossPiece(Point spawnPoint) {
            super(spawnPoint.x(), spawnPoint.y() - 2);
        }

        @Override
        public int getBoundingWidth() {
            return 3;
        }

        @Override
        public int getBoundingHeight() {
            return 3;
        }

        @Override
        public boolean canMoveDown(char[][] grid) {
            return grid[getGridRowUnder(grid)][getGridColumnToTheLeft() + 2] == '.';
        }

        @Override
        public boolean canMoveRight(char[][] grid) {
            return grid[getGridRowUnder(grid) - 2][getGridColumnToTheRight()] == '.';
        }

        @Override
        public boolean canMoveLeft(char[][] grid) {
            return grid[getGridRowUnder(grid) - 2][getGridColumnToTheLeft()] == '.';
        }

        @Override
        public void addToGrid(char[][] grid) {
            int row = getGridRowUnder(grid) - 1;
            int column = getGridColumnToTheLeft() + 1;
            grid[row][column + 1] = '@';
            grid[row - 1][column] = '@';
            grid[row - 1][column + 1] = '@';
            grid[row - 1][column + 2] = '@';
            grid[row - 2][column + 1] = '@';
        }
    }

    private static class ReverseLPiece extends AbstractPiece {

        public ReverseLPiece(Point spawnPoint) {
            super(spawnPoint.x(), spawnPoint.y() - 2);
        }

        @Override
        public int getBoundingWidth() {
            return 3;
        }

        @Override
        public int getBoundingHeight() {
            return 3;
        }

        @Override
        public boolean canMoveDown(char[][] grid) {
            for (int i = 0; i < getBoundingWidth(); i++) {
                if (grid[getGridRowUnder(grid)][getGridColumnToTheLeft() + 1 + i] != '.') {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean canMoveRight(char[][] grid) {
            for (int i = 0; i < getBoundingHeight(); i++) {
                if (grid[getGridRowUnder(grid) - 1 - i][getGridColumnToTheRight()] != '.') {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean canMoveLeft(char[][] grid) {
            return grid[getGridRowUnder(grid) - 1][getGridColumnToTheLeft()] == '.';
        }

        @Override
        public void addToGrid(char[][] grid) {
            int row = getGridRowUnder(grid) - 1;
            int column = getGridColumnToTheLeft() + 1;
            grid[row][column] = '@';
            grid[row][column + 1] = '@';
            grid[row][column + 2] = '@';
            grid[row - 1][column + 2] = '@';
            grid[row - 2][column + 2] = '@';
        }
    }

    private static class VerticalPiece extends AbstractPiece {

        public VerticalPiece(Point spawnPoint) {
            super(spawnPoint.x(), spawnPoint.y() - 3);
        }

        @Override
        public int getBoundingWidth() {
            return 1;
        }

        @Override
        public int getBoundingHeight() {
            return 4;
        }

        @Override
        public boolean canMoveDown(char[][] grid) {
            return grid[getGridRowUnder(grid)][getGridColumnToTheLeft() + 1] == '.';
        }

        @Override
        public boolean canMoveRight(char[][] grid) {
            for (int i = 0; i < getBoundingHeight(); i++) {
                if (grid[getGridRowUnder(grid) - 1 - i][getGridColumnToTheRight()] != '.') {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean canMoveLeft(char[][] grid) {
            for (int i = 0; i < getBoundingHeight(); i++) {
                if (grid[getGridRowUnder(grid) - 1 - i][getGridColumnToTheLeft()] != '.') {
                    return false;
                }
            }
            return true;
        }

        @Override
        public void addToGrid(char[][] grid) {
            int row = getGridRowUnder(grid) - 1;
            int column = getGridColumnToTheLeft() + 1;
            grid[row][column] = '@';
            grid[row - 1][column] = '@';
            grid[row - 2][column] = '@';
            grid[row - 3][column] = '@';
        }
    }

    private static class CubePiece extends AbstractPiece {

        public CubePiece(Point spawnPoint) {
            super(spawnPoint.x(), spawnPoint.y() - 1);
        }

        @Override
        public int getBoundingWidth() {
            return 2;
        }

        @Override
        public int getBoundingHeight() {
            return 2;
        }

        @Override
        public boolean canMoveDown(char[][] grid) {
            for (int i = 0; i < getBoundingWidth(); i++) {
                if (grid[getGridRowUnder(grid)][getGridColumnToTheLeft() + 1 + i] != '.') {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean canMoveRight(char[][] grid) {
            for (int i = 0; i < getBoundingHeight(); i++) {
                if (grid[getGridRowUnder(grid) - 1 - i][getGridColumnToTheRight()] != '.') {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean canMoveLeft(char[][] grid) {
            for (int i = 0; i < getBoundingHeight(); i++) {
                if (grid[getGridRowUnder(grid) - 1 - i][getGridColumnToTheLeft()] != '.') {
                    return false;
                }
            }
            return true;
        }

        @Override
        public void addToGrid(char[][] grid) {
            int row = getGridRowUnder(grid) - 1;
            int column = getGridColumnToTheLeft() + 1;
            grid[row][column] = '@';
            grid[row][column + 1] = '@';
            grid[row - 1][column] = '@';
            grid[row - 1][column + 1] = '@';
        }
    }
}
