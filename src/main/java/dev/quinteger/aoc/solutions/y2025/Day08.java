package dev.quinteger.aoc.solutions.y2025;

import dev.quinteger.aoc.solutions.Solution;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.List;

public class Day08 extends Solution {
    @Override
    public Object solvePart1(List<String> input, boolean example) {
        NavigableMap<Double, BoxPair> byDistanceMap = createBoxPairsByDistance(input);
        Map<Box, Set<Box>> circuits = new LinkedHashMap<>();
        int connections;
        if (example) {
            connections = 10;
        } else {
            connections = 1000;
        }
        Map.Entry<Double, BoxPair> distanceEntry;
        while (connections > 0 && (distanceEntry = byDistanceMap.pollFirstEntry()) != null) {
            BoxPair boxPair = distanceEntry.getValue();
            Box box1 = boxPair.box1();
            Box box2 = boxPair.box2();
            Set<Box> existingCircuitOfBox1 = circuits.get(box1);
            Set<Box> existingCircuitOfBox2 = circuits.get(box2);
            if (existingCircuitOfBox1 == null && existingCircuitOfBox2 == null) {
                Set<Box> circuit = new HashSet<>();
                circuit.add(box1);
                circuit.add(box2);
                circuits.put(box1, circuit);
                circuits.put(box2, circuit);
            } else if (existingCircuitOfBox1 == null) {
                existingCircuitOfBox2.add(box1);
                circuits.put(box1, existingCircuitOfBox2);
            } else if (existingCircuitOfBox2 == null) {
                existingCircuitOfBox1.add(box2);
                circuits.put(box2, existingCircuitOfBox1);
            } else if (existingCircuitOfBox1 != existingCircuitOfBox2) {
                existingCircuitOfBox1.addAll(existingCircuitOfBox2);
                for (Box boxInCircuit2 : existingCircuitOfBox2) {
                    circuits.put(boxInCircuit2, existingCircuitOfBox1);
                }
            }
            connections--;
        }
        Set<Set<Box>> circuitIdentitySet = Collections.newSetFromMap(new IdentityHashMap<>());
        circuitIdentitySet.addAll(circuits.values());
        return circuitIdentitySet
                .stream()
                .sorted(Comparator.<Set<?>>comparingInt(Set::size).reversed())
                .limit(3)
                .mapToLong(Set::size)
                .reduce(1L, (a, b) -> a * b);
    }

    @Override
    public Object solvePart2(List<String> input, boolean example) {
        List<Box> boxes = createBoxes(input);
        NavigableMap<Double, BoxPair> byDistanceMap = associateBoxesByDistance(boxes);
        Map<Box, Set<Box>> circuitsOfBoxes = new LinkedHashMap<>();
        int circuitCount = 0;
        Box lastMergedBox1 = null;
        Box lastMergedBox2 = null;
        while (!(circuitsOfBoxes.size() == boxes.size()) || circuitCount != 1) {
            Map.Entry<Double, BoxPair> distanceEntry = byDistanceMap.pollFirstEntry();
            BoxPair boxPair = distanceEntry.getValue();
            Box box1 = boxPair.box1();
            Box box2 = boxPair.box2();
            Set<Box> existingCircuitOfBox1 = circuitsOfBoxes.get(box1);
            Set<Box> existingCircuitOfBox2 = circuitsOfBoxes.get(box2);
            if (existingCircuitOfBox1 == null && existingCircuitOfBox2 == null) {
                Set<Box> circuit = new HashSet<>();
                circuit.add(box1);
                circuit.add(box2);
                circuitsOfBoxes.put(box1, circuit);
                circuitsOfBoxes.put(box2, circuit);
                circuitCount += 1;
            } else if (existingCircuitOfBox1 == null) {
                existingCircuitOfBox2.add(box1);
                circuitsOfBoxes.put(box1, existingCircuitOfBox2);
                lastMergedBox1 = box1;
                lastMergedBox2 = box2;
            } else if (existingCircuitOfBox2 == null) {
                existingCircuitOfBox1.add(box2);
                circuitsOfBoxes.put(box2, existingCircuitOfBox1);
                lastMergedBox1 = box1;
                lastMergedBox2 = box2;
            } else if (existingCircuitOfBox1 != existingCircuitOfBox2) {
                existingCircuitOfBox1.addAll(existingCircuitOfBox2);
                for (Box boxInCircuit2 : existingCircuitOfBox2) {
                    circuitsOfBoxes.put(boxInCircuit2, existingCircuitOfBox1);
                }
                circuitCount--;
                lastMergedBox1 = box1;
                lastMergedBox2 = box2;
            }
        }
        Objects.requireNonNull(lastMergedBox1);
        Objects.requireNonNull(lastMergedBox2);
        return lastMergedBox1.x() * lastMergedBox2.x();
    }

    private static NavigableMap<Double, BoxPair> createBoxPairsByDistance(List<String> input) {
        List<Box> boxes = createBoxes(input);
        return associateBoxesByDistance(boxes);
    }

    private static List<Box> createBoxes(List<String> input) {
        return input.stream()
                .map(line -> line.split(","))
                .map(splitLine -> new Box(Long.parseLong(splitLine[0]), Long.parseLong(splitLine[1]), Long.parseLong(splitLine[2])))
                .toList();
    }

    private static NavigableMap<Double, BoxPair> associateBoxesByDistance(List<Box> boxes) {
        NavigableMap<Double, BoxPair> byDistanceMap = new TreeMap<>();
        for (int i = 0; i < boxes.size(); i++) {
            Box firstBox = boxes.get(i);
            for (int j = i + 1; j < boxes.size(); j++) {
                Box secondBox = boxes.get(j);
                double distance = firstBox.distanceTo(secondBox);
                byDistanceMap.put(distance, new BoxPair(firstBox, secondBox));
            }
        }
        return byDistanceMap;
    }

    private record Box(long x, long y, long z) {

        
        public double distanceTo(Box other) {
            return Math.sqrt(Math.pow(other.x - this.x, 2) + Math.pow(other.y - this.y, 2)  + Math.pow(other.z - this.z, 2));
        }
    }
    
    private record BoxPair(Box box1, Box box2) {}
}
