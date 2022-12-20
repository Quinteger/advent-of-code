package dev.quinteger.aoc.days;

import dev.quinteger.aoc.Solution;

import java.util.ArrayList;
import java.util.List;

public class Day20 extends Solution {
    private static class CircularLinkedList {
        private final List<Node> initialPositions = new ArrayList<>();
        private Node head;
        private int size = 0;

        public CircularLinkedList(List<Long> input) {
            if (input.isEmpty()) {
                return;
            }

            head = new Node(input.get(0));
            initialPositions.add(head);
            size++;
            var previousNode = head;
            for (int i = 1; i < input.size(); i++) {
                var newNode = new Node(input.get(i));
                initialPositions.add(newNode);
                size++;

                previousNode.next = newNode;
                newNode.previous = previousNode;
                previousNode = newNode;
            }
            previousNode.next = head;
            head.previous = previousNode;
        }

        public Node getInitialNode(int index) {
            return initialPositions.get(index);
        }

        public void multiplyAll(long key) {
            var node = head;
            node.value = node.value * key;
            for (int i = 1; i < size; i++) {
                node = node.next;
                node.value = node.value * key;
            }
        }
    }

    private static class Node {
        private long value;
        private Node previous;
        private Node next;

        public Node(long value) {
            this.value = value;
        }
    }

    private CircularLinkedList list;

    private void parseInput(List<String> input) {
        var intList = input.stream().map(Long::parseLong).toList();
        list = new CircularLinkedList(intList);
    }

    private void printList() {
        var node = list.head;
        System.out.print(node.value);

        for (int i = 1; i < list.size; i++) {
            System.out.print(", ");
            node = node.next;
            System.out.print(node.value);
        }
        System.out.println();
    }

    @Override
    public Object solvePart1(List<String> input, boolean example) {
        parseInput(input);
        printList();

        for (int i = 0; i < list.size; i++) {
            var node = list.getInitialNode(i);
            long value = node.value;
            if (value == 0) {
                continue;
            }

            var previous = node.previous;
            var next = node.next;
            previous.next = next;
            next.previous = previous;

            var newPrevious = previous;
            if (value > 0) {
                for (long j = 0; j < value; j++) {
                    newPrevious = newPrevious.next;
                }
            } else {
                for (long j = 0; j > value; j--) {
                    newPrevious = newPrevious.previous;
                }
            }
            var newNext = newPrevious.next;

            newPrevious.next = node;
            newNext.previous = node;
            node.previous = newPrevious;
            node.next = newNext;

//            printList();
        }

        long sum = 0;
        var node = list.head;
        while (node.value != 0 ) {
            node = node.next;
        }
        for (int i = 1; i <= 3000; i++) {
            node = node.next;
            if (i % 1000 == 0) {
                sum += node.value;
            }
        }
        return sum;
    }

    @Override
    public Object solvePart2(List<String> input, boolean example) {
        parseInput(input);
        printList();
        list.multiplyAll(811589153);
        printList();

        for (int mix = 0; mix < 10; mix++) {
            for (int i = 0; i < list.size; i++) {
                var node = list.getInitialNode(i);
                long value = node.value;
                if (value == 0) {
                    continue;
                } else {
                    value = value % (list.size - 1);
                }

                var previous = node.previous;
                var next = node.next;
                previous.next = next;
                next.previous = previous;

                var newPrevious = previous;
                if (value > 0) {
                    for (long j = 0; j < value; j++) {
                        newPrevious = newPrevious.next;
                    }
                } else {
                    for (long j = 0; j > value; j--) {
                        newPrevious = newPrevious.previous;
                    }
                }
                var newNext = newPrevious.next;

                newPrevious.next = node;
                newNext.previous = node;
                node.previous = newPrevious;
                node.next = newNext;
            }
            printList();
        }

        long sum = 0;
        var node = list.head;
        while (node.value != 0 ) {
            node = node.next;
        }
        for (int i = 1; i <= 3000; i++) {
            node = node.next;
            if (i % 1000 == 0) {
                sum += node.value;
            }
        }
        return sum;
    }
}
