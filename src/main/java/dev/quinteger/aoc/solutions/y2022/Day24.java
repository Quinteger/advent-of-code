package dev.quinteger.aoc.solutions.y2022;

import dev.quinteger.aoc.solutions.Solution;

import java.util.*;

public class Day24 extends Solution {
    private record Point(int row, int column) {
        public Point north() {
            return new Point(row - 1, column);
        }

        public Point south() {
            return new Point(row + 1, column);
        }

        public Point west() {
            return new Point(row, column - 1);
        }

        public Point east() {
            return new Point(row, column + 1);
        }
    }

    private final Set<Point> northBlizzards = new HashSet<>();
    private final Set<Point> southBlizzards = new HashSet<>();
    private final Set<Point> westBlizzards = new HashSet<>();
    private final Set<Point> eastBlizzards = new HashSet<>();
    private final Set<Point> walls = new HashSet<>();
    private int minRow;
    private int maxRow;
    private int minColumn;
    private int maxColumn;
    private Point start;
    private Point end;
    private int cycle;

    private void parseInput(List<String> input) {
        minRow = 0;
        minColumn = 0;
        maxRow = 0;
        maxColumn = 0;
        for (int i = 0; i < input.size(); i++) {
            var chars = input.get(i).toCharArray();
            for (int j = 0; j < chars.length; j++) {
                char c = chars[j];
                var point = new Point(i, j);
                if (c == '#') {
                    walls.add(point);
                } else if (c == '^') {
                    northBlizzards.add(point);
                } else if (c == 'v') {
                    southBlizzards.add(point);
                } else if (c == '<') {
                    westBlizzards.add(point);
                } else if (c == '>') {
                    eastBlizzards.add(point);
                } else if (i == 0 && c == '.') {
                    start = point;
                } else if (i == input.size() - 1 && c == '.') {
                    end = point;
                } else if (c != '.') {
                    throw new RuntimeException();
                }
                if (j > maxColumn) {
                    maxColumn = j;
                }
            }
            if (i > maxRow) {
                maxRow = i;
            }
        }
        cycle = lcm(maxRow - minRow - 1, maxColumn - minColumn - 1);
        printGrid();
    }

    private static int lcm(int number1, int number2) {
        if (number1 == 0 || number2 == 0) {
            return 0;
        }
        int absNumber1 = Math.abs(number1);
        int absNumber2 = Math.abs(number2);
        int absHigherNumber = Math.max(absNumber1, absNumber2);
        int absLowerNumber = Math.min(absNumber1, absNumber2);
        int lcm = absHigherNumber;
        while (lcm % absLowerNumber != 0) {
            lcm += absHigherNumber;
        }
        return lcm;
    }

    private void printGrid() {
        printGrid(northBlizzards, southBlizzards, westBlizzards, eastBlizzards, null);
    }

    private void printGrid(Set<Point> northBlizzards, Set<Point> southBlizzards, Set<Point> westBlizzards, Set<Point> eastBlizzards, Point position) {
        for (int i = minRow; i <= maxRow; i++) {
            for (int j = minColumn; j <= maxColumn; j++) {
                var point = new Point(i, j);
                if (walls.contains(point)) {
                    System.out.print('#');
                } else {
                    if (point.equals(position)) {
                        System.out.print('E');
                        continue;
                    }

                    int count = 0;
                    if (northBlizzards.contains(point)) {
                        count++;
                    }
                    if (southBlizzards.contains(point)) {
                        count++;
                    }
                    if (westBlizzards.contains(point)) {
                        count++;
                    }
                    if (eastBlizzards.contains(point)) {
                        count++;
                    }

                    if (count > 1) {
                        System.out.print(count);
                    } else {
                        if (northBlizzards.contains(point)) {
                            System.out.print('^');
                        } else if (southBlizzards.contains(point)) {
                            System.out.print('v');
                        } else if (westBlizzards.contains(point)) {
                            System.out.print('<');
                        } else if (eastBlizzards.contains(point)) {
                            System.out.print('>');
                        } else {
                            System.out.print('.');
                        }
                    }
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    private boolean isInBounds(Point point) {
        return point.row() >= minRow && point.row() <= maxRow
                && point.column() >= minColumn && point.column() <= maxColumn;
    }

    private boolean isFree(Point point, Set<Point> northBlizzards, Set<Point> southBlizzards, Set<Point> westBlizzards, Set<Point> eastBlizzards) {
        return !walls.contains(point) && !northBlizzards.contains(point) && !southBlizzards.contains(point) && !westBlizzards.contains(point) & !eastBlizzards.contains(point);
    }

    private boolean isTrappedInBlizzards(Point point, Set<Point> northBlizzards, Set<Point> southBlizzards, Set<Point> westBlizzards, Set<Point> eastBlizzards) {
        var points = new ArrayList<Point>(5);
        points.add(point);
        points.add(point.north());
        points.add(point.south());
        points.add(point.west());
        points.add(point.east());
        for (Point p : points) {
            if (!northBlizzards.contains(p) || !southBlizzards.contains(p) || !westBlizzards.contains(p) || !eastBlizzards.contains(p)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Object solvePart1(List<String> input, boolean example) {
        parseInput(input);
        return findBFS(start, end);
    }

    private record State(int minute, Point point, Set<Point> northBlizzards, Set<Point> southBlizzards, Set<Point> westBlizzards, Set<Point> eastBlizzards) {}


    private int findBFS(Point start, Point end) {
        return findBFS(start, end, 0);
    }

    private int findBFS(Point start, Point end, int initialMinute) {
        Map<Integer, Set<Point>> visited = new HashMap<>();
        Deque<State> queue = new ArrayDeque<>();
        visited.compute(initialMinute, (i, l) -> {
            if (l == null) {
                l = new HashSet<>();
            }
            l.add(start);
            return l;
        });

        var northBlizzards = moveNorth(this.northBlizzards, initialMinute);
        var southBlizzards = moveSouth(this.southBlizzards, initialMinute);
        var westBlizzards = moveWest(this.westBlizzards, initialMinute);
        var eastBlizzards = moveEast(this.eastBlizzards, initialMinute);

        queue.add(new State(0, start, northBlizzards, southBlizzards, westBlizzards, eastBlizzards));
        while (!queue.isEmpty()) {
            var state = queue.remove();
            if (state.point().equals(end)) {
                return state.minute();
            }

            var newNorthBlizzards = moveNorth(state.northBlizzards());
            var newSouthBlizzards = moveSouth(state.southBlizzards());
            var newWestBlizzards = moveWest(state.westBlizzards());
            var newEastBlizzards = moveEast(state.eastBlizzards());

            var possibleMoves = new ArrayList<Point>(5);
            Point possibleMove;

            var currentPoint = state.point();
            for (int neighbourIndex = 0; neighbourIndex < 5; neighbourIndex++) {
                possibleMove = switch (neighbourIndex) {
                    case 0 -> currentPoint.north();
                    case 1 -> currentPoint.south();
                    case 2 -> currentPoint.west();
                    case 3 -> currentPoint.east();
                    case 4 -> currentPoint;
                    default -> throw new RuntimeException();
                };

                var visitedSet = visited.get((state.minute() + 1) % cycle);
                if (visitedSet != null && visitedSet.contains(possibleMove)) {
                    continue;
                }

                if (isInBounds(possibleMove) && isFree(possibleMove, newNorthBlizzards, newSouthBlizzards, newWestBlizzards, newEastBlizzards)) {
                    possibleMoves.add(possibleMove);
                }
            }

            for (Point move : possibleMoves) {
                visited.compute(state.minute() + 1, (i, l) -> {
                    if (l == null) {
                        l = new HashSet<>();
                    }
                    l.add(move);
                    return l;
                });
                queue.add(new State(state.minute() + 1, move, newNorthBlizzards, newSouthBlizzards, newWestBlizzards, newEastBlizzards));
            }
        }
        return Integer.MAX_VALUE;
    }


    private Set<Point> moveNorth(Set<Point> points, int amount) {
        Set<Point> result = new HashSet<>(points);
        for (int i = 0; i < amount % cycle; i++) {
            result = moveNorth(result);
        }
        return result;
    }

    private Set<Point> moveNorth(Set<Point> points) {
        var result = new HashSet<Point>();
        for (Point point : points) {
            int newRow = point.row() - 1;
            if (newRow <= minRow) {
                result.add(new Point(maxRow - 1, point.column()));
            } else {
                result.add(point.north());
            }
        }
        return result;
    }


    private Set<Point> moveSouth(Set<Point> points, int amount) {
        Set<Point> result = new HashSet<>(points);
        for (int i = 0; i < amount % cycle; i++) {
            result = moveSouth(result);
        }
        return result;
    }

    private Set<Point> moveSouth(Set<Point> points) {
        var result = new HashSet<Point>();
        for (Point point : points) {
            int newRow = point.row() + 1;
            if (newRow >= maxRow) {
                result.add(new Point(minRow + 1, point.column()));
            } else {
                result.add(point.south());
            }
        }
        return result;
    }


    private Set<Point> moveWest(Set<Point> points, int amount) {
        Set<Point> result = new HashSet<>(points);
        for (int i = 0; i < amount % cycle; i++) {
            result = moveWest(result);
        }
        return result;
    }

    private Set<Point> moveWest(Set<Point> points) {
        var result = new HashSet<Point>();
        for (Point point : points) {
            int newColumn = point.column() - 1;
            if (newColumn <= minColumn) {
                result.add(new Point(point.row(), maxColumn - 1));
            } else {
                result.add(point.west());
            }
        }
        return result;
    }


    private Set<Point> moveEast(Set<Point> points, int amount) {
        Set<Point> result = new HashSet<>(points);
        for (int i = 0; i < amount % cycle; i++) {
            result = moveEast(result);
        }
        return result;
    }

    private Set<Point> moveEast(Set<Point> points) {
        var result = new HashSet<Point>();
        for (Point point : points) {
            int newColumn = point.column() + 1;
            if (newColumn >= maxColumn) {
                result.add(new Point(point.row(), minColumn + 1));
            } else {
                result.add(point.east());
            }
        }
        return result;
    }

    private int getManhattan(Point from, Point to) {
        return Math.abs(from.column() - to.column()) + Math.abs(from.row() - to.row());
    }

    @Override
    public Object solvePart2(List<String> input, boolean example) {
        int go = findBFS(start, end, 0);
        int goBack = findBFS(end, start, go);
        int goAgain = findBFS(start, end, go + goBack);
        return go + goBack + goAgain;
    }
}
