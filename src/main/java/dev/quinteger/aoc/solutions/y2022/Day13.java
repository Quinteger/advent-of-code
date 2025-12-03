package dev.quinteger.aoc.solutions.y2022;

import dev.quinteger.aoc.solutions.Solution;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Day13 extends Solution {
    private static class Packet implements Comparable<Packet> {
        final List<Object> list = new ArrayList<>();

        @SuppressWarnings("unchecked")
        public void addElement(List<Integer> keys, Object element) {
            List<Object> l = list;
            for (int i = 0; i < keys.size() - 1; i++) {
                int key = keys.get(i);
                ensureCapacityForKey(l, key);
                Object o = l.get(key);
                if (o instanceof List<?>) {
                    l = (List<Object>) o;
                } else {
                    throw new RuntimeException("Expected a list, but got " + o);
                }
            }
            int key = keys.get(keys.size() - 1);
            ensureCapacityForKey(l, key);
            l.set(key, element);
        }

        private static void ensureCapacityForKey(List<Object> list, int key) {
            while (key >= list.size()) {
                list.add(new ArrayList<>());
            }
        }

        @SuppressWarnings("unchecked")
        public void ensureListExists(List<Integer> keys) {
            List<Object> l = list;
            for (int i = 0; i < keys.size() - 1; i++) {
                int key = keys.get(i);
                ensureCapacityForKey(l, key);
                Object o = l.get(key);
                if (o instanceof List<?>) {
                    l = (List<Object>) o;
                }
            }
        }

        @Override
        public int compareTo(Packet o) {
            return compare(this.list, o.list);
        }

        @SuppressWarnings("unchecked")
        private static int compare(Object o1, Object o2) {
            if (o1 instanceof List<?> l1 && o2 instanceof List<?> l2) {
                int endIndex = Math.max(l1.size(), l2.size());
                for (int i = 0; i < endIndex; i++) {
                    if (i >= l1.size()) {
                        return -1;
                    } else if (i >= l2.size()) {
                        return 1;
                    } else {
                        var e1 = l1.get(i);
                        var e2 = l2.get(i);
                        int result = compare(e1, e2);
                        if (result != 0) {
                            return result;
                        }
                    }
                }
                return 0;
            } else if (o1 instanceof List<?> l1) {
                return compare(l1, List.of(o2));
            } else if (o2 instanceof List<?> l2) {
                return compare(List.of(o1), l2);
            } else if (o1.getClass() == o2.getClass() && o1 instanceof Comparable<?> c1) {
                return ((Comparable<Object>) c1).compareTo(o2);
            }
            throw new RuntimeException("Incomparable objects: %s of type %s and %s of type %s".formatted(o1, o1.getClass().getSimpleName(), o2, o2.getClass().getSimpleName()));
        }

        @Override
        public String toString() {
            return list.toString();
        }
    }

    @Override
    public Object solvePart1(List<String> input, boolean example) {
        int pairIndex = 0;
        int sum = 0;
        for (int lineIndex = 0; lineIndex < input.size(); lineIndex+=3) {
            var packet1 = createPacket(input.get(lineIndex));
            var packet2 = createPacket(input.get(lineIndex + 1));
            pairIndex++;
            int result = packet1.compareTo(packet2);
            if (result < 0) {
                sum += pairIndex;
            }
        }
        return sum;
    }

    private static Packet createPacket(String line) {
        Packet packet = new Packet();
        Map<Integer, Integer> groups = new TreeMap<>();
        int currentDepth = -1;
        int currentGroup = 0;
        boolean endOfPacketReached = false;
        var chars = line.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == ',') {
                currentGroup++;
                groups.put(currentDepth, currentGroup);
            } else if (c == '[') {
                if (endOfPacketReached) {
                    throw new RuntimeException("Multiple packets on a line");
                } else if (currentDepth >= 0) {
                    groups.put(currentDepth, currentGroup);
                }
                currentDepth++;
                currentGroup = 0;
                groups.put(currentDepth, currentGroup);
            } else if (c == ']') {
                currentDepth--;
                if (currentDepth >= 0) {
                    currentGroup = groups.get(currentDepth);
                    packet.ensureListExists(groups.values().stream().toList());
                } else {
                    endOfPacketReached = true;
                }
            } else if (Character.isDigit(c)) {
                int numberStart = i;
                c = chars[i + 1];
                while (Character.isDigit(c) && i < chars.length) {
                    i++;
                    c = chars[i + 1];
                }
                int numberEnd = i + 1;
                int number = Integer.parseInt(line.substring(numberStart, numberEnd));
                List<Integer> groupValues = groups.values().stream().toList();
                packet.addElement(groupValues, number);
            }
        }
        return packet;
    }

    @Override
    public Object solvePart2(List<String> input, boolean example) {
        List<Packet> packets = new ArrayList<>();
        for (String line : input) {
            if (!line.isEmpty()) {
                var packet = createPacket(line);
                packets.add(packet);
            }
        }
        packets.add(createPacket("[[2]]"));
        packets.add(createPacket("[[6]]"));
        packets.sort(Packet::compareTo);
        int index2 = 0;
        int index6 = 0;
        for (int i = 0; i < packets.size(); i++) {
            Packet packet = packets.get(i);
            if (packet.list.equals(List.of(List.of(2)))) {
                index2 = i + 1;
            }
            if (packet.list.equals(List.of(List.of(6)))) {
                index6 = i + 1;
            }
        }
        return index2 * index6;
    }
}
