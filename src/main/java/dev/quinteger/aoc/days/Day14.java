package dev.quinteger.aoc.days;

import dev.quinteger.aoc.Solution;

import java.util.Arrays;
import java.util.List;

public class Day14 extends Solution {
    public Day14(List<String> input) {
        super(input);
    }
    private static final int originOffset = 500;
    private static final Point origin = new Point(0, originOffset);

    private char[][] grid = new char[1][1];
    private int distance = 0;

    private void resizeGridForDistance(int distance) {
        char[][] oldGrid = grid;
        int distanceDiff = distance - oldGrid.length + 1;
        if (distanceDiff <= 0) {
            return;
        }
        int newHeight = distance + 1;
        int newWidth = distance * 2 + 1;
        char[][] newGrid = new char[newHeight][newWidth];
        emptyGrid(newGrid);
        for (int i = 0; i < oldGrid.length; i++) {
            System.arraycopy(oldGrid[i], 0, newGrid[i], distanceDiff, oldGrid[i].length);
        }
        this.grid = newGrid;
        this.distance = distance;
    }

    private void emptyGrid() {
        emptyGrid(grid);
    }

    private static void emptyGrid(char[][] grid) {
        for (char[] chars : grid) {
            Arrays.fill(chars, '.');
        }
    }

    private void fillGrid() {
        emptyGrid();
        for (String line : input) {
            String[] points = line.split(" -> ");
            for (int i = 0; i < points.length - 1; i++) {
                String[] startPoint = points[i].split(",");
                String[] endPoint = points[i + 1].split(",");

                int startCol = Integer.parseInt(startPoint[0]);
                int startRow = Integer.parseInt(startPoint[1]);
                int endCol = Integer.parseInt(endPoint[0]);
                int endRow = Integer.parseInt(endPoint[1]);

                resizeGridForDistance(Math.max(Math.max(Math.abs(startCol - originOffset), Math.abs(endCol - originOffset)) + 1, Math.max(startRow, endRow) + 2));

                if (startCol == endCol) {
                    int min = Math.min(startRow, endRow);
                    int max = Math.max(startRow, endRow);
                    for (int j = min; j <= max; j++) {
                        grid[j][getRealColumn(startCol)] = '#';
                    }
                } else if (startRow == endRow) {
                    int min = Math.min(startCol, endCol);
                    int max = Math.max(startCol, endCol);
                    for (int j = min; j <= max; j++) {
                        grid[startRow][getRealColumn(j)] = '#';
                    }
                } else {
                    throw new RuntimeException();
                }
            }
        }
    }

    private int getRealColumn(int column) {
        return column - originOffset + distance;
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

    @Override
    public Object solvePart1() {
        fillGrid();

        Point newPoint;
        int count = 0;
        do {
            newPoint = spawnSand();
            if (newPoint != null) {
                count++;
            }
            if (distance <= 10) {
                printGrid();
            }

        } while (newPoint != null && !origin.equals(newPoint));

        return count;
    }

    private Point spawnSand() {
        var sandPrev = origin;
        var sandNext = tryGoDown(sandPrev);

        while (!sandPrev.equals(sandNext) && sandNext != null) {
            sandPrev = sandNext;
            sandNext = tryGoDown(sandNext);
        }

        if (sandNext != null) {
            grid[sandNext.x()][getRealColumn(sandNext.y())] = '0';
        }
        return sandNext;
    }

    public Point tryGoDown(Point sand) {
        int realRow = sand.x();
        int realCol = getRealColumn(sand.y());
        if (realRow >= grid.length - 1) {
            return null;
        } else if (grid[realRow + 1][realCol] == '.') {
            return new Point(sand.x() + 1, sand.y());
        } else if (grid[realRow + 1][realCol - 1] == '.') {
            return new Point(sand.x() + 1, sand.y() - 1);
        } else if (grid[realRow + 1][realCol + 1] == '.') {
            return new Point(sand.x() + 1, sand.y() + 1);
        } else {
            return sand;
        }
    }

    @Override
    public Object solvePart2() {
        fillGrid();

        Arrays.fill(grid[grid.length - 1], '#');

        int count = 0;
        Point newPoint = null;
        while (!origin.equals(newPoint)) {
            newPoint = spawnSand();
            count++;
            if (distance <= 10) {
                printGrid();
            }
        }

        return count;
    }

    private record Point(int x, int y) {}
}
