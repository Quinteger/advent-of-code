package dev.quinteger.aoc.solutions.y2025;

import dev.quinteger.aoc.solutions.Solution;

import java.util.*;

public class Day05 extends Solution {
    @Override
    public Object solvePart1(List<String> input, boolean example) {
        List<IngredientRange> ranges = extractSortedMergedIngredientRanges(input);
        List<Long> ingredients = input.stream()
                .dropWhile(line -> !line.isBlank())
                .skip(1)
                .map(Long::parseLong)
                .toList();
        int fresh = 0;
        for (long ingredient : ingredients) {
            boolean match = false;
            for (IngredientRange range : ranges) {
                if (ingredient >= range.from() && ingredient <= range.to()) {
                    match = true;
                    break;
                }
            }
            if (match) {
                fresh++;
            }
        }
        return fresh;
    }

    @Override
    public Object solvePart2(List<String> input, boolean example) {
        List<IngredientRange> ranges = extractSortedMergedIngredientRanges(input);
        return ranges.stream()
                .mapToLong(ingredientRange -> ingredientRange.to() - ingredientRange.from() + 1)
                .sum();
    }

    private record IngredientRange(long from, long to) {}

    private static List<IngredientRange> extractSortedMergedIngredientRanges(List<String> input) {
        List<IngredientRange> sortedIngredientRanges = input.stream()
                .takeWhile(line -> !line.isBlank())
                .map(line -> line.split("-"))
                .map(splitLine -> new IngredientRange(Long.parseLong(splitLine[0]), Long.parseLong(splitLine[1])))
                .sorted(Comparator.comparingLong(IngredientRange::from))
                .toList();
        IngredientRange first = sortedIngredientRanges.getFirst();
        long currentRangeFrom = first.from();
        long currentRangeTo = first.to();
        List<IngredientRange> mergedIngredientRanges = new ArrayList<>();
        for (IngredientRange ingredientRange : sortedIngredientRanges) {
            long from = ingredientRange.from();
            long to = ingredientRange.to();
            if (from > currentRangeTo + 1) {
                mergedIngredientRanges.add(new IngredientRange(currentRangeFrom, currentRangeTo));
                currentRangeFrom = from;
            }
            if (to > currentRangeTo) {
                currentRangeTo = to;
            }
        }
        mergedIngredientRanges.add(new IngredientRange(currentRangeFrom, currentRangeTo));
        return Collections.unmodifiableList(mergedIngredientRanges);
    }
}
