package dev.quinteger.aoc.days;

import dev.quinteger.aoc.Solution;

import java.util.Arrays;
import java.util.List;

public class Day22 extends Solution {
    private char[][] grid;

    private int[] minRowCoords;
    private int[] maxRowCoords;
    private int[] minColumnCoords;
    private int[] maxColumnCoords;

    private void createGrid(int height, int width, List<String> lines) {
        grid = new char[height][width];
        for (int i = 0; i < lines.size(); i++) {
            var chars = lines.get(i).toCharArray();
            for (int j = 0; j < width; j++) {
                if (j >= chars.length || chars[j] == ' ') {
                    grid[i][j] = ' ';
                } else {
                    grid[i][j] = chars[j];
                }
            }
        }
        printGrid();

        minRowCoords = new int[height];
        maxRowCoords = new int[height];
        minColumnCoords = new int[width];
        maxColumnCoords = new int[width];

        for (int row = 0; row < height; row++) {
            boolean firstFound = false;
            for (int column = 0; column < grid[row].length; column++) {
                char c = grid[row][column];
                if (c != ' ') {
                    if (!firstFound) {
                        minRowCoords[row] = column;
                        firstFound = true;
                    }
                    maxRowCoords[row] = column;
                }
            }
        }

        for (int column = 0; column < width; column++) {
            boolean firstFound = false;
            for (int row = 0; row < grid.length; row++) {
                char c = grid[row][column];
                if (c != ' ') {
                    if (!firstFound) {
                        minColumnCoords[column] = row;
                        firstFound = true;
                    }
                    maxColumnCoords[column] = row;
                }
            }
        }

        System.out.println(Arrays.toString(minRowCoords));
        System.out.println(Arrays.toString(maxRowCoords));
        System.out.println(Arrays.toString(minColumnCoords));
        System.out.println(Arrays.toString(maxColumnCoords));
    }

    private void printGrid() {
        for (char[] chars : grid) {
            for (char c : chars) {
                System.out.print(c);
            }
            System.out.println();
        }
        System.out.println();
    }

    private enum Direction {
        RIGHT,
        DOWN,
        LEFT,
        UP;

        private static final int size = values().length;

        public Direction rotate(String input) {
            int newIndex;
            if (input.charAt(0) == 'R') {
                newIndex = (ordinal() + 1) % size;
            } else if (input.charAt(0) == 'L') {
                newIndex = (ordinal() - 1);
                if (newIndex < 0) {
                    newIndex += size;
                }
            } else {
                throw new RuntimeException();
            }
            return values()[newIndex];
        }

        public State move(int row, int column) {
            return switch (this) {
                case RIGHT -> new State(row, column + 1, this);
                case DOWN -> new State(row + 1, column, this);
                case LEFT -> new State(row, column - 1, this);
                case UP -> new State(row - 1, column, this);
            };
        }
    }

    private int row;

    private int column;
    private Direction direction;

    @Override
    public Object solvePart1(List<String> input, boolean example) {
        int gridWidth = 0;
        int gridHeight = 0;
        for (int i = 0; i < input.size(); i++) {
            String line = input.get(i);
            int lineSize = line.length();
            if (lineSize == 0) {
                gridHeight = i;
                break;
            }
            if (lineSize > gridWidth) {
                gridWidth = lineSize;
            }
        }
        createGrid(gridHeight, gridWidth, input.subList(0, gridHeight));
        row = 0;
        column = minRowCoords[row];
        direction = Direction.RIGHT;
        actions = input.get(input.size() - 1).toCharArray();
        actionIndex = 0;

        String action;

        while ((action = getNextAction()) != null) {
            if (action.matches("^\\d+$")) {
                int amount = Integer.parseInt(action);
                System.out.println("Walk " + amount);
                walk(amount);
            } else {
                System.out.println("Turn " + action);
                direction = direction.rotate(action);
            }
        }

        return (row + 1) * 1000 + (column + 1) * 4 + direction.ordinal();
    }

    private char[] actions;
    private int actionIndex;

    private String getNextAction() {
        if (actionIndex >= actions.length) {
            return null;
        }
        char c = actions[actionIndex];
        if (Character.isLetter(c)) {
            actionIndex++;
            return String.valueOf(c);
        }
        int start = actionIndex;
        int end = actionIndex;
        do {
            end++;
        } while (end < actions.length && Character.isDigit(actions[end]));
        actionIndex = end;
        return new String(Arrays.copyOfRange(actions, start, end));
    }

    private void walk(int distance) {
        for (int i = 0; i < distance; i++) {
            if (!walkSingleTile()) {
                break;
            }
        }
    }

    private boolean walkSingleTile() {
        int newRow = row;
        int newColumn = column;
        if (direction == Direction.RIGHT) {
            newColumn++;
            if (newColumn >= grid[row].length || grid[newRow][newColumn] == ' ') {
                newColumn = minRowCoords[newRow];
            }
        } else if (direction == Direction.DOWN) {
            newRow++;
            if (newRow >= grid.length || grid[newRow][newColumn] == ' ') {
                newRow = minColumnCoords[newColumn];
            }
        } else if (direction == Direction.LEFT) {
            newColumn--;
            if (newColumn < 0 || grid[newRow][newColumn] == ' ') {
                newColumn = maxRowCoords[newRow];
            }
        } else if (direction == Direction.UP) {
            newRow--;
            if (newRow < 0 || grid[newRow][newColumn] == ' ') {
                newRow = maxColumnCoords[newColumn];
            }
        } else {
            throw new RuntimeException();
        }
        if (grid[newRow][newColumn] == '#') {
            return false;
        } else {
            row = newRow;
            column = newColumn;
//            System.out.printf("Row: %d, Column: %d%n", row, column);
            return true;
        }
    }

    @Override
    public Object solvePart2(List<String> input, boolean example) {
        row = 0;
        column = minRowCoords[row];
        direction = Direction.RIGHT;
        actions = input.get(input.size() - 1).toCharArray();
        actionIndex = 0;
        var state = new State(row, column, direction);

        String action;

        while ((action = getNextAction()) != null) {
            if (action.matches("^\\d+$")) {
                int amount = Integer.parseInt(action);
                System.out.println("Walk " + amount);
                state = walkState(state, amount);
            } else {
                System.out.println("Turn " + action);
                state = new State(state.row(), state.column(), state.direction().rotate(action));
            }
        }

        return (state.row() + 1) * 1000 + (state.column() + 1) * 4 + state.direction().ordinal();
    }

    private State walkState(State state, int distance) {
        var previousState = state;
        var nextState = state;
        for (int i = 0; i < distance; i++) {
            nextState = getNewState(previousState);
            if (grid[nextState.row()][nextState.column()] != '#') {
                previousState = nextState;
            } else {
                break;
            }
        }
        return previousState;
    }

    private record State(int row, int column, Direction direction) {}

    private State getNewState(State state) {
        int row = state.row();
        int column = state.column();
        var direction = state.direction();
        if (0 <= row && row < 50 && 50 <= column && column < 100) {
            if (column == 50 && direction == Direction.LEFT) {
                row = 149 - (row % 50);
                column = 0;
                direction = Direction.RIGHT;
                return new State(row, column, direction);
            }
            if (row == 0 && direction == Direction.UP) {
                row = 150 + (column % 50);
                column = 0;
                direction = Direction.RIGHT;
                return new State(row, column, direction);
            }
            return direction.move(row, column);
        }
        if (0 <= row && row < 50 && 100 <= column && column < 150) {
            if (column == 149 && direction == Direction.RIGHT) {
                row = 149 - (row % 50);
                column = 99;
                direction = Direction.LEFT;
                return new State(row, column, direction);
            }
            if (row == 49 && direction == Direction.DOWN) {
                row = 50 + (column % 50);
                column = 99;
                direction = Direction.LEFT;
                return new State(row, column, direction);
            }
            if (row == 0 && direction == Direction.UP) {
                row = 199;
                column = column % 50;
                return new State(row, column, direction);
            }
            return direction.move(row, column);
        }
        if (50 <= row && row < 100 && 50 <= column && column < 100) {
            if (column == 99 && direction == Direction.RIGHT) {
                column = 100 + (row % 50);
                row = 49;
                direction = Direction.UP;
                return new State(row, column, direction);
            }
            if (column == 50 && direction == Direction.LEFT) {
                column = row % 50;
                row = 100;
                direction = Direction.DOWN;
                return new State(row, column, direction);
            }
            return direction.move(row, column);
        }
        if (100 <= row && row < 150 && 0 <= column && column < 50) {
            if (column == 0 && direction == Direction.LEFT) {
                row = 49 - (row % 50);
                column = 50;
                direction = Direction.RIGHT;
                return new State(row, column, direction);
            }
            if (row == 100 && direction == Direction.UP) {
                row = 50 + (column % 50);
                column = 50;
                direction = Direction.RIGHT;
                return new State(row, column, direction);
            }
            return direction.move(row, column);
        }
        if (100 <= row && row < 150 && 50 <= column && column < 100) {
            if (column == 99 && direction == Direction.RIGHT) {
                row = 49 - (row % 50);
                column = 149;
                direction = Direction.LEFT;
                return new State(row, column, direction);
            }
            if (row == 149 && direction == Direction.DOWN) {
                row = 150 + (column % 50);
                column = 49;
                direction = Direction.LEFT;
                return new State(row, column, direction);
            }
            return direction.move(row, column);
        }
        if (150 <= row && row < 200 && 0 <= column && column < 50) {
            if (column == 49 && direction == Direction.RIGHT) {
                column = 50 + (row % 50);
                row = 149;
                direction = Direction.UP;
                return new State(row, column, direction);
            }
            if (row == 199 && direction == Direction.DOWN) {
                column = 100 + (column % 50);
                row = 0;
                return new State(row, column, direction);
            }
            if (column == 0 && direction == Direction.LEFT) {
                column = 50 + (row % 50);
                row = 0;
                direction = Direction.DOWN;
                return new State(row, column, direction);
            }
            return direction.move(row, column);
        }
        throw new RuntimeException();
    }
}
