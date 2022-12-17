package dev.quinteger.aoc.days;

import dev.quinteger.aoc.Solution;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Day17 extends Solution {
    private static final int pieceCount = 2022;
    private char[][] grid;
    private int towerMaxHeight = 0;
    private int hiddenRows = 0;

    @Override
    public Object solvePart1(List<String> input, boolean example) {
        grid = null;
        towerMaxHeight = 0;
        initializeGrid();
//        printGrid();
        char[] inputs = input.get(0).toCharArray();
        int jetIndex = 0;
        for (int i = 0; i < pieceCount; i++) {
            int pieceIndex = i % 5;
            int spawnY = getSpawnY();
//            RockPiece piece = new HorizontalPiece(new Point(2, spawnHeight));
            RockPiece piece = switch (pieceIndex) {
                case 0 -> new HorizontalPiece(2, spawnY);
                case 1 -> new CrossPiece(2, spawnY);
                case 2 -> new ReverseLPiece(2, spawnY);
                case 3 -> new VerticalPiece(2, spawnY);
                case 4 -> new CubePiece(2, spawnY);
                default -> throw new RuntimeException();
            };
            resizeGridForY(spawnY + piece.getBoundingHeight() - 1);

            while (true){
                char c = inputs[jetIndex];
                if (c == '<') {
                    piece.tryMoveLeft(grid);
                } else if (c == '>') {
                    piece.tryMoveRight(grid);
                } else {
                    throw new RuntimeException("Incorrect input");
                }
                jetIndex++;
                jetIndex = jetIndex % inputs.length;

                boolean down = piece.tryMoveDown(grid);
                if (!down) {
                    break;
                }
            }

            piece.addToGrid(grid);
//            printGrid();
            int pieceTopPoint = piece.getBottomY() + piece.getBoundingHeight() - 1;
            if (pieceTopPoint + 1 > towerMaxHeight) {
                towerMaxHeight = pieceTopPoint + 1;
            }
        }
//        printGrid();
        return towerMaxHeight;
    }

    private void printGrid() {
        for (char[] chars : grid) {
            for (char c : chars) {
                System.out.print(c);
//                System.out.print(c);
            }
            System.out.println();
        }
        System.out.println();
    }

    private int getSpawnY() {
        return towerMaxHeight + 3;
    }

    private void initializeGrid() {
        grid = new char[5][9];
        grid[4] = new char[] {'#', '#', '#', '#', '#', '#', '#', '#', '#'};
        for (int i = 0; i < 4; i++) {
            grid[i] = new char[] {'#', '.', '.', '.', '.', '.', '.', '.', '#'};
        }
        hiddenRows = 0;
    }

    private void resizeGridForY(int y) {
        char[][] oldGrid = grid;
        int newHeight = Math.max(oldGrid.length, y + 2);
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
//        char[][] oldGrid = grid;
//        int necessaryTotalGridHeight = y + 2;
//        int currentTotalGridHeight = oldGrid.length + hiddenRows;
//        int addRows = necessaryTotalGridHeight - currentTotalGridHeight;
//        if (addRows <= 0) {
//            return;
//        }
//        int maxVisible = 0;
//        for (int i = 0; i < oldGrid.length; i++) {
//            for (int j = 1; j < 8; j++) {
//                if (grid[i][j] != '.') {
//                    break;
//                }
//            }
//            maxVisible++;
//        }

    }

    private record FallStart(int pieceType, int inputStartIndex) {}

    private record FallResult(int moveX, int moveDown, int inputCount, int towerGrowth) {}

    @Override
    public Object solvePart2(List<String> input, boolean example) {
        grid = null;
        towerMaxHeight = 0;
        initializeGrid();
//        printGrid();
        char[] inputs = input.get(0).toCharArray();
        int jetIndex = 0;
        Map<FallStart, FallResult> movements = new LinkedHashMap<>();
        Map<FallStart, Boolean> checks = new LinkedHashMap<>();
        long pieceIndex = 0;
        for (; ; pieceIndex++) {
            int pieceType = (int) (pieceIndex % 5);
            int spawnY = getSpawnY();
//            RockPiece piece = new HorizontalPiece(new Point(2, spawnHeight));
            RockPiece piece = switch (pieceType) {
                case 0 -> new HorizontalPiece(2, spawnY);
                case 1 -> new CrossPiece(2, spawnY);
                case 2 -> new ReverseLPiece(2, spawnY);
                case 3 -> new VerticalPiece(2, spawnY);
                case 4 -> new CubePiece(2, spawnY);
                default -> throw new RuntimeException();
            };
            resizeGridForY(spawnY + piece.getBoundingHeight() - 1);

            int moveX = 0;
            int moveDown = 0;
            int inputCount = 0;
            var start = new FallStart(pieceType, jetIndex);
            while (true){
                char c = inputs[jetIndex];
                if (c == '<') {
                    boolean left = piece.tryMoveLeft(grid);
                    if (left) {
                        moveX--;
                    }
                } else if (c == '>') {
                    boolean right = piece.tryMoveRight(grid);
                    if (right) {
                        moveX++;
                    }
                } else {
                    throw new RuntimeException("Incorrect input");
                }
                inputCount++;
                jetIndex++;
                jetIndex = jetIndex % inputs.length;

                boolean down = piece.tryMoveDown(grid);
                if (!down) {
                    break;
                } else {
                    moveDown++;
                }
            }
            piece.addToGrid(grid);
//            printGrid();
            int pieceTopY = piece.getBottomY() + piece.getBoundingHeight() - 1;
            int towerGrowth = Math.max(pieceTopY + 1 - towerMaxHeight, 0);
            if (towerGrowth > 0) {
                towerMaxHeight += towerGrowth;
            }

            var result = new FallResult(moveX, moveDown, inputCount, towerGrowth);
            if (movements.containsKey(start)) {
                var prevResult = movements.get(start);
                if(prevResult.equals(result)) {
//                    System.out.println("Result match: " + start + ", " + result);
//                    System.out.println("Piece index: " + pieceIndex);
                    if (checks.containsKey(start) && checks.get(start)) {
                        break;
                    } else {
                        checks.put(start, true);
                    }
                } else {
                    checks.put(start, false);
                }
            } else {
                checks.clear();
                movements.put(start, result);
            }
        }
        long piecesFit = pieceIndex + 1;
        long repeatingIntervalSize = checks.size();
        long repeatingIntervalStart = pieceIndex - repeatingIntervalSize * 2;
        long repeatingIntervalEnd = repeatingIntervalStart + repeatingIntervalSize - 1;
        long towerGrowthPerInterval = checks.keySet().stream().mapToInt(start -> movements.get(start).towerGrowth()).sum();
        System.out.println("First repeating piece has index " + repeatingIntervalStart);
        System.out.println("Last repeating piece has index " + repeatingIntervalEnd);
        System.out.println("Tower growth per interval " + towerGrowthPerInterval);
        long remainingPieces = 1_000_000_000_000L - piecesFit;
        System.out.println("Pieces already fit: " + piecesFit);
        System.out.println("Pieces to fit: " + remainingPieces);
        long towerHeight = towerMaxHeight;
        System.out.println("Current tower height: " + towerHeight);
        long intervalsToFit = remainingPieces / repeatingIntervalSize;
        System.out.println("Can fit intervals: " + intervalsToFit);
        towerHeight += intervalsToFit * towerGrowthPerInterval;
        pieceIndex += intervalsToFit * repeatingIntervalSize;
        piecesFit += intervalsToFit * repeatingIntervalSize;
        remainingPieces = 1_000_000_000_000L - piecesFit;
        System.out.printf("After fitting intervals: %d pieces fit, %d remaining, tower height %d%n", piecesFit, remainingPieces, towerHeight);
        var itr = checks.keySet().iterator();
        for (long i = 0; i <= remainingPieces; i++) {
            var next = itr.next();
            if (i == 0) {
                continue;
            }
            towerHeight += movements.get(next).towerGrowth();

        }
//        printGrid();
//        System.out.println(movements);
        return towerHeight;
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
            return grid.length - 1 - getBottomY();
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
        public HorizontalPiece(int leftX, int bottomY) {
            super(leftX, bottomY);
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
        public CrossPiece(int leftX, int bottomY) {
            super(leftX, bottomY);
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
            return grid[getGridRowUnder(grid)][getGridColumnToTheLeft() + 2] == '.'
                    && grid[getGridRowUnder(grid) - 1][getGridColumnToTheLeft() + 1] == '.'
                    && grid[getGridRowUnder(grid) - 1][getGridColumnToTheLeft() + 3] == '.';
        }

        @Override
        public boolean canMoveRight(char[][] grid) {
            return grid[getGridRowUnder(grid) - 2][getGridColumnToTheRight()] == '.'
                    && grid[getGridRowUnder(grid) - 3][getGridColumnToTheRight() - 1] == '.'
                    && grid[getGridRowUnder(grid) - 1][getGridColumnToTheRight() - 1] == '.';
        }

        @Override
        public boolean canMoveLeft(char[][] grid) {
            return grid[getGridRowUnder(grid) - 2][getGridColumnToTheLeft()] == '.'
                    && grid[getGridRowUnder(grid) - 3][getGridColumnToTheLeft() + 1] == '.'
                    && grid[getGridRowUnder(grid) - 1][getGridColumnToTheLeft() + 1] == '.';
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

        public ReverseLPiece(int leftX, int bottomY) {
            super(leftX, bottomY);
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
            return grid[getGridRowUnder(grid) - 1][getGridColumnToTheLeft()] == '.'
                    && grid[getGridRowUnder(grid) - 2][getGridColumnToTheLeft() + 2] == '.'
                    && grid[getGridRowUnder(grid) - 3][getGridColumnToTheLeft() + 2] == '.';
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

        public VerticalPiece(int leftX, int bottomY) {
            super(leftX, bottomY);
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

        public CubePiece(int leftX, int bottomY) {
            super(leftX, bottomY);
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
