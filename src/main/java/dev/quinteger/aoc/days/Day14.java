package dev.quinteger.aoc.days;

import dev.quinteger.aoc.Solution;

import java.util.Arrays;
import java.util.List;

public class Day14 extends Solution {
    public Day14(List<String> input) {
        super(input);
    }

    private final char[][] grid = new char[170][401];

    private record Point(int x, int y) {
        public int realCol() {
            return y - 300;
        }
        public Point tryGoDown(char[][] grid) {
            int realRow = x;
            int realCol = realCol();
            if (realRow >= grid.length - 1) {
                return null;
            } else if (grid[realRow + 1][realCol] == '.') {
                return new Point(x + 1, y);
            } else if (grid[realRow + 1][realCol - 1] == '.') {
                return new Point(x + 1, y - 1);
            } else if (grid[realRow + 1][realCol + 1] == '.') {
                return new Point(x + 1, y + 1);
            } else {
                return this;
            }
        }
    }

    private void fillGrid() {
        for (char[] chars : grid) {
            Arrays.fill(chars, '.');
        }

        for (String line : input) {
            String[] points = line.split(" -> ");
//            String[] startPoint = points[0].split(",");
//            Point start = new Point(Integer.parseInt(startPoint[0]), Integer.parseInt(startPoint[1]));
            for (int i = 0; i < points.length - 1; i++) {
                String[] startPoint = points[i].split(",");
                String[] endPoint = points[i + 1].split(",");
                int startCol = Integer.parseInt(startPoint[0]);
                int startRow = Integer.parseInt(startPoint[1]);
                int endCol = Integer.parseInt(endPoint[0]);
                int endRow = Integer.parseInt(endPoint[1]);
                if (startCol == endCol) {
                    int min = Math.min(startRow, endRow);
                    int max = Math.max(startRow, endRow);
                    for (int j = min; j <= max; j++) {
                        grid[j][startCol - 300] = '#';
                    }
                } else if (startRow == endRow) {
                    int min = Math.min(startCol, endCol);
                    int max = Math.max(startCol, endCol);
                    for (int j = min; j <= max; j++) {
                        grid[startRow][j - 300] = '#';
                    }
                } else {
                    throw new RuntimeException();
                }
            }
        }
        printGrid();
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

        var sandPrev = spawnSand();
        var sandNext = spawnSand();
        int count = 2;
        do {
            sandPrev = sandNext;
            sandNext = spawnSand();
            count++;

//            printGrid();

        } while (sandNext != null && !sandNext.equals(sandPrev));

//        printGrid();

        return count - 1;
    }

    private Point spawnSand() {
        var sandPrev = new Point(0, 500);
        var sandNext = sandPrev.tryGoDown(grid);
        while (!sandPrev.equals(sandNext) && sandNext != null) {
            sandPrev = sandNext;
            sandNext = sandNext.tryGoDown(grid);
        }
        if (sandNext != null) {
            grid[sandNext.x()][sandNext.realCol()] = '0';
        }
        return sandNext;
    }

    @Override
    public Object solvePart2() {
        fillGrid();

        char[] bottom = grid[grid.length - 1];
        Arrays.fill(bottom, '#');

//        printGrid();

        var start = new Point(0, 500);
        int count = 0;
        Point newPoint = null;
        while (!start.equals(newPoint)) {
            newPoint = spawnSand();
            count++;
//            if (count % 10 == 0) {
//                printGrid();
//            }
//            printGrid();
        }

        return count;
    }
}
