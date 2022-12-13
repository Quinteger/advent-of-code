package dev.quinteger.aoc.days;

import dev.quinteger.aoc.Solution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Day13 extends Solution {
    public Day13(List<String> input) {
        super(input);
    }

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
                    throw new RuntimeException("Expected a list or null, but got " + o);
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
            return comesBefore(this.list, o.list);
        }

        @SuppressWarnings("unchecked")
        private static int comesBefore(Object o1, Object o2) {
            if (o1 instanceof Integer i1 && o2 instanceof Integer i2) {
                return i1.compareTo(i2);
            } else if (o1 instanceof List<?> l1 && o2 instanceof List<?> l2) {
                List<Object> list1 = (List<Object>) l1;
                List<Object> list2 = (List<Object>) l2;
                int endIndex = Math.max(list1.size(), list2.size());
                for (int i = 0; i < endIndex; i++) {
                    if (i >= list1.size()) {
                        return -1;
                    } else if (i >= list2.size()) {
                        return 1;
                    } else {
                        var e1 = list1.get(i);
                        var e2 = list2.get(i);
                        int result = comesBefore(e1, e2);
                        if (result != 0) {
                            return result;
                        }
                    }
                }
                return 0;
            } else if (o1 instanceof List<?> list) {
                return comesBefore(list, Collections.singletonList(o2));
            } else if (o2 instanceof List<?> list) {
                return comesBefore(Collections.singletonList(o1), list);
            }
            throw new RuntimeException("Comparing %s and %s".formatted(o1, o2));
        }

        @Override
        public String toString() {
            return list.toString();
        }
    }

    @Override
    public Object solvePart1() {
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
    public Object solvePart2() {
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
