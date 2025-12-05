package dev.quinteger.aoc.solutions.y2025;

import dev.quinteger.aoc.solutions.Solution;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day04 extends Solution {
    @Override
    public Object solvePart1(List<String> input, boolean example) {
        int[] flattenedGrid = createInitialGrid(input);
        int matches = 0;
        for (int amountOfNeighbours : flattenedGrid) {
            if (amountOfNeighbours >= 0 && amountOfNeighbours < 4) {
                matches++;
            }
        }
        return matches;
    }

    @Override
    public Object solvePart2(List<String> input, boolean example) {
        int rowCount = input.size();
        int columnCount = input.getFirst().length();
        int lastRowIndex = rowCount - 1;
        int lastColumnIndex = columnCount - 1;
        int[] flattenedGrid = createInitialGrid(input);
        int rollsRemovedTotal = 0;
        int rollsRemovedInLastPass;
        do {
            rollsRemovedInLastPass = 0;
            for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
                for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
                    int flattenedGridIndex = rowIndex * columnCount + columnIndex;
                    int amountOfNeighbours = flattenedGrid[flattenedGridIndex];
                    if (amountOfNeighbours >= 0 && amountOfNeighbours < 4) {
                        rollsRemovedInLastPass++;
                        flattenedGrid[flattenedGridIndex] = -1;
                        for (int modifiedRowIndex = rowIndex - 1; modifiedRowIndex <= rowIndex + 1; modifiedRowIndex++) {
                            for (int modifiedColumnIndex = columnIndex - 1; modifiedColumnIndex <= columnIndex + 1; modifiedColumnIndex++) {
                                if (modifiedRowIndex >= 0 && modifiedRowIndex <= lastRowIndex
                                        && modifiedColumnIndex >= 0 && modifiedColumnIndex <= lastColumnIndex
                                        && !(modifiedRowIndex == rowIndex && modifiedColumnIndex == columnIndex)
                                ) {
                                    int modifiedFlattenedGridIndex = modifiedRowIndex * columnCount + modifiedColumnIndex;
                                    int previous = flattenedGrid[modifiedFlattenedGridIndex];
                                    if (previous > 0) {
                                        flattenedGrid[modifiedFlattenedGridIndex] = previous - 1;
                                    } else if (previous == 0) {
                                        throw new IllegalStateException("Trying to remove a neighbour from a position with no neighbours, row index %d, column index %d".formatted(modifiedRowIndex, modifiedColumnIndex));
                                    }
                                }
                            }
                        }
                    }
                }
            }
            rollsRemovedTotal += rollsRemovedInLastPass;
        } while (rollsRemovedInLastPass > 0);
        String gridAfter = drawGrid(flattenedGrid, rowCount, columnCount);
        System.out.println(gridAfter);

        return rollsRemovedTotal;
    }

    private static int[] createInitialGrid(List<String> input) {
        int rowCount = input.size();
        int columnCount = input.getFirst().length();
        int gridSize = rowCount * columnCount;
        int[] flattenedGrid = new int[gridSize];
        int lastRowIndex = rowCount - 1;
        int lastColumnIndex = columnCount - 1;
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            String line = input.get(rowIndex);
            for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
                char c = line.charAt(columnIndex);
                switch (c) {
                    case '@' -> {
                        for (int modifiedRowIndex = rowIndex - 1; modifiedRowIndex <= rowIndex + 1; modifiedRowIndex++) {
                            for (int modifiedColumnIndex = columnIndex - 1; modifiedColumnIndex <= columnIndex + 1; modifiedColumnIndex++) {
                                if (modifiedRowIndex >= 0 && modifiedRowIndex <= lastRowIndex
                                        && modifiedColumnIndex >= 0 && modifiedColumnIndex <= lastColumnIndex
                                        && !(modifiedRowIndex == rowIndex && modifiedColumnIndex == columnIndex)
                                ) {
                                    int flattenedGridIndex = modifiedRowIndex * columnCount + modifiedColumnIndex;
                                    if (flattenedGrid[flattenedGridIndex] >= 0) {
                                        flattenedGrid[flattenedGridIndex]++;
                                    }
                                }
                            }
                        }
                    }
                    case '.' -> {
                        int flattenedGridIndex = rowIndex * columnCount + columnIndex;
                        flattenedGrid[flattenedGridIndex] = -1;
                    }
                    default -> throw new IllegalStateException();
                }
            }
        }
        return flattenedGrid;
    }

    private static String drawGrid(int[] flattenedGrid, int rowCount, int columnCount) {
        return IntStream.range(0, rowCount)
                .mapToObj(rowIndex -> Arrays.copyOfRange(flattenedGrid, rowIndex * columnCount, (rowIndex + 1) * columnCount))
                .map(array -> Arrays.stream(array).mapToObj(value -> {
                    if (value >= 0) {
                        return "@";
                    } else {
                        return ".";
                    }
                }).collect(Collectors.joining()))
                .collect(Collectors.joining("\n"));
    }
}
