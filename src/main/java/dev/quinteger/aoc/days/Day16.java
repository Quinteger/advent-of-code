package dev.quinteger.aoc.days;

import dev.quinteger.aoc.Solution;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day16 extends Solution {
    private static final Pattern patternSingle = Pattern.compile("^Valve (\\w{2}) has flow rate=(\\d+); tunnel leads to valve (\\w{2})$");
    private static final Pattern patternMultiple = Pattern.compile("^Valve (\\w{2}) has flow rate=(\\d+); tunnels lead to valves (.*)$");

    private final Map<String, ValveData> valves = new HashMap<>();

    private record ValveData(int pressure, Set<String> paths) {}

    private void parseValves(List<String> input) {
        valves.clear();
        for (String line : input) {
            var matcher = patternSingle.matcher(line);
            if (matcher.matches() && matcher.groupCount() == 3) {
                var valve = matcher.group(1);
                int pressure = Integer.parseInt(matcher.group(2));
                var valvePath = matcher.group(3);
                valves.put(valve, new ValveData(pressure, Set.of(valvePath)));
            } else {
                matcher = patternMultiple.matcher(line);
                if (matcher.matches() && matcher.groupCount() == 3) {
                    var valve = matcher.group(1);
                    int pressure = Integer.parseInt(matcher.group(2));
                    String[] valvePaths = matcher.group(3).split(", ");
                    valves.put(valve, new ValveData(pressure, Set.of(valvePaths)));
                }
            }
        }
    }

    private int maxPressure = 0;

    private static final int part1MinuteCap = 30;

    private void find(List<String> path, Deque<String> activePath, Map<String, Boolean> valveStates, int currentMinute, int pressureReleased) {
        int pressureReleaseSpeed = valveStates.entrySet().stream().mapToInt(e -> e.getValue() ? valves.get(e.getKey()).pressure() : 0).sum();
        pressureReleased += pressureReleaseSpeed;
        currentMinute++;
        if (currentMinute >= part1MinuteCap) {
            if (pressureReleased > maxPressure) {
//                System.out.println(path);
//                System.out.println(pressureReleased);
                maxPressure = pressureReleased;
            }
            return;
        }

        String currentValve = null;
        for (var itr = path.listIterator(path.size()); itr.hasPrevious();) {
            var element = itr.previous();
            if (element.equals(element.toUpperCase())) {
                currentValve = element;
                break;
            }
        }
        Objects.requireNonNull(currentValve);

        if (!activePath.isEmpty()) {
            path.add(activePath.removeFirst());
            find(path, activePath, valveStates, currentMinute, pressureReleased);
            return;
        } else if (valves.get(currentValve).pressure() > 0 && !valveStates.get(currentValve)) {
            path.add(currentValve.toLowerCase());
            valveStates.put(currentValve, true);
            find(path, activePath, valveStates, currentMinute, pressureReleased);
            return;
        }

        if (valveStates.values().stream().allMatch(v -> v)) {
            find(path, activePath, valveStates, currentMinute, pressureReleased);
        } else {
            var valvesToVisit = valveStates.entrySet().stream().filter(e -> !e.getValue()).map(Map.Entry::getKey).toList();
            int finalCurrentMinute = currentMinute;
            int approxPossiblePressure = valvesToVisit.stream().mapToInt(v -> valves.get(v).pressure() * (part1MinuteCap - finalCurrentMinute)).sum();
            pressureReleaseSpeed = valveStates.entrySet().stream().mapToInt(e -> e.getValue() ? valves.get(e.getKey()).pressure() : 0).sum();
            if (pressureReleased + approxPossiblePressure + pressureReleaseSpeed * (part1MinuteCap - currentMinute) < maxPressure) {
//                System.out.println("CULL FAST: " + path);
                return;
            }
            var searchData = getSearchData(currentValve, valvesToVisit, currentMinute, 30);
            if (searchData.isEmpty()) {
                find(path, activePath, valveStates, currentMinute, pressureReleased);
            }
//            var maximumPossiblePressure = searchData.values().stream().mapToInt(SearchData::possiblePressure).sum();
//            if (pressureReleased + maximumPossiblePressure + pressureReleaseSpeed * (part1MinuteCap - currentMinute) < maxPressure) {
//                System.out.println("CULL: " + path);
//                return;
//            }
            for (var searchDataValue : searchData.values()) {
                var activePathCopy = new ArrayDeque<>(searchDataValue.path());
                activePathCopy.removeFirst();
                var pathCopy = new ArrayList<>(path);
                pathCopy.add(activePathCopy.removeFirst());
                var valveStatesCopy = new HashMap<>(valveStates);
                find(pathCopy, activePathCopy, valveStatesCopy, currentMinute, pressureReleased);
            }
        }
    }

    private record ValvePair(String from, String to) {}
    private final Map<ValvePair, Deque<String>> paths = new HashMap<>();

    private Deque<String> findPathBetweenValves(String from, String to) {
        return paths.computeIfAbsent(new ValvePair(from, to), vp -> {
            var f = vp.from();
            var t = vp.to();
            var path = new ArrayDeque<String>();
            path.addLast(f);
            if (f.equals(t)) {
                return path;
            }
            return findPathBetweenValvesRecursive(t, path, Set.of(f));
        });
    }

    private Deque<String> findPathBetweenValvesRecursive(String to, Deque<String> path, Set<String> visited) {
        var neighbours = valves.get(path.getLast()).paths().stream().filter(p -> !visited.contains(p)).toList();
        if (neighbours.isEmpty()) {
            return null;
        }
        int length = Integer.MAX_VALUE;
        Deque<String> foundPath = null;
        for (String neighbour : neighbours) {
            if (neighbour.equals(to)) {
                path.addLast(neighbour);
                return path;
            } else {
                var newPath = new ArrayDeque<>(path);
                newPath.add(neighbour);
                var newVisited = new HashSet<>(visited);
                newVisited.add(neighbour);

                var newSearch = findPathBetweenValvesRecursive(to, newPath, newVisited);

                if (newSearch != null && newSearch.size() < length) {
                    foundPath = newSearch;
                    length = newSearch.size();
                }
            }
        }
        return foundPath;
    }

    private Map<String, Deque<String>> getPaths(String currentValve, Collection<String> toVisit) {
        return toVisit.stream().collect(Collectors.toMap(Function.identity(), v -> findPathBetweenValves(currentValve, v), (m1, m2) -> m2, HashMap::new));
    }

    private Map<String, SearchData> getSearchData(String currentValve, Collection<String> toVisit, int minute, int minuteCap) {
        return toVisit.stream()
                .collect(Collectors.toMap(Function.identity(), v -> {
                    var path = findPathBetweenValves(currentValve, v);
                    return new SearchData(path, (minuteCap - minute - path.size()) * valves.get(v).pressure());
                }));
    }

    @Override
    public Object solvePart1(List<String> input, boolean example) {
        paths.clear();
        maxPressure = 0;
        parseValves(input);
        var path = new ArrayList<String>();
        path.add("AA");
        var states = valves.entrySet().stream()
                .filter(e -> e.getValue().pressure() > 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toMap(Function.identity(), v -> false, (v1, v2) -> false, HashMap::new));
        find(path, new ArrayDeque<>(), states, 0, 0);
        return maxPressure;
    }

    private record SearchData(Deque<String> path, int possiblePressure) {}

    @Override
    public Object solvePart2(List<String> input, boolean example) {
        maxPressure = 0;
        var humanPath = new ArrayList<String>();
        var elephantPath = new ArrayList<String>();
        humanPath.add("AA");
        elephantPath.add("AA");
        var states = valves.entrySet().stream()
                .filter(e -> e.getValue().pressure() > 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toMap(Function.identity(), v -> false, (v1, v2) -> false, HashMap::new));
        findForTwo(humanPath, new ArrayDeque<>(), elephantPath, new ArrayDeque<>(), states, 0, 0);
        return maxPressure;
    }

    private record ValveStatistics(Map<String, Boolean> valveStates, int pressureReleaseSpeed, int pressure) {}

    private void findForTwo(List<String> humanPath, Deque<String> humanActivePath, List<String> elephantPath, Deque<String> elephantActivePath,
                            Map<String, Boolean> valveStates, int currentMinute, int pressureReleased) {
        int pressureReleaseSpeed = valveStates.entrySet().stream().mapToInt(e -> e.getValue() ? valves.get(e.getKey()).pressure() : 0).sum();
        pressureReleased += pressureReleaseSpeed;
        currentMinute++;

        if (currentMinute >= 26) {
            if (pressureReleased > maxPressure) {
//                System.out.println(humanPath);
//                System.out.println(elephantPath);
//                System.out.println(pressureReleased);
                maxPressure = pressureReleased;
            }
            return;
        }


        String currentHumanValve = null;
        String currentElephantValve = null;
        for (var itr = humanPath.listIterator(humanPath.size()); itr.hasPrevious();) {
            var element = itr.previous();
            if (element.equals(element.toUpperCase())) {
                currentHumanValve = element;
                break;
            }
        }
        for (var itr = elephantPath.listIterator(elephantPath.size()); itr.hasPrevious();) {
            var element = itr.previous();
            if (element.equals(element.toUpperCase())) {
                currentElephantValve = element;
                break;
            }
        }

        Objects.requireNonNull(currentHumanValve);
        Objects.requireNonNull(currentElephantValve);

        boolean humanFlag = false;
        boolean elephantFlag = false;

        if (!humanActivePath.isEmpty()) {
            humanFlag = true;
            humanPath.add(humanActivePath.removeFirst());
        } else if (valves.get(currentHumanValve).pressure() > 0 && !valveStates.get(currentHumanValve)) {
            humanFlag = true;
            humanPath.add(currentHumanValve.toLowerCase());
            valveStates.put(currentHumanValve, true);
        }

        if (!elephantActivePath.isEmpty()) {
            elephantFlag = true;
            elephantPath.add(elephantActivePath.removeFirst());
        } else if (valves.get(currentElephantValve).pressure() > 0 && !valveStates.get(currentElephantValve)) {
            elephantFlag = true;
            elephantPath.add(currentElephantValve.toLowerCase());
            valveStates.put(currentElephantValve, true);
        }

        if (humanFlag && elephantFlag) {
            findForTwo(humanPath, humanActivePath, elephantPath, elephantActivePath, valveStates, currentMinute, pressureReleased);
            return;
        }

        if (valveStates.values().stream().allMatch(v -> v)) {
            findForTwo(humanPath, humanActivePath, elephantPath, elephantActivePath, valveStates, currentMinute, pressureReleased);
        } else {
            var valvesToVisit = valveStates.entrySet().stream().filter(e -> !e.getValue()).map(Map.Entry::getKey).collect(Collectors.toCollection(HashSet::new));

            int finalCurrentMinute = currentMinute;
            int approxPossiblePressure = valvesToVisit.stream().mapToInt(v -> valves.get(v).pressure() * (26 - finalCurrentMinute)).sum();
            pressureReleaseSpeed = valveStates.entrySet().stream().mapToInt(e -> e.getValue() ? valves.get(e.getKey()).pressure() : 0).sum();
            if (pressureReleased + approxPossiblePressure + pressureReleaseSpeed * (26 - currentMinute) < maxPressure) {
//                System.out.println("CULL FAST: " + path);
                return;
            }

            if (!humanActivePath.isEmpty()) {
                valvesToVisit.remove(humanActivePath.getLast());
            } else {
                valvesToVisit.remove(currentHumanValve);
            }
            if (!elephantActivePath.isEmpty()) {
                valvesToVisit.remove(elephantActivePath.getLast());
            } else {
                valvesToVisit.remove(currentElephantValve);
            }

            if (valvesToVisit.isEmpty()) {
                findForTwo(humanPath, humanActivePath, elephantPath, elephantActivePath, valveStates, currentMinute, pressureReleased);
            } else if (!humanFlag && !elephantFlag) {
                for (String humanTarget : valvesToVisit) {
                    var elephantTargets = new HashSet<>(valvesToVisit);
                    elephantTargets.remove(humanTarget);
                    for (String elephantTarget : elephantTargets) {
                        var humanActivePathCopy = new ArrayDeque<>(findPathBetweenValves(currentHumanValve, humanTarget));
                        var elephantActivePathCopy = new ArrayDeque<>(findPathBetweenValves(currentElephantValve, elephantTarget));
                        humanActivePathCopy.removeFirst();
                        elephantActivePathCopy.removeFirst();

                        var humanPathCopy = new ArrayList<>(humanPath);
                        var elephantPathCopy = new ArrayList<>(elephantPath);
                        humanPathCopy.add(humanActivePathCopy.removeFirst());
                        elephantPathCopy.add(elephantActivePathCopy.removeFirst());

                        var valveStatesCopy = new HashMap<>(valveStates);
                        findForTwo(humanPathCopy, humanActivePathCopy, elephantPathCopy, elephantActivePathCopy, valveStatesCopy, currentMinute, pressureReleased);
                    }
                }
            } else if (!humanFlag) {
                for (String humanTarget : valvesToVisit) {
                    var humanActivePathCopy = new ArrayDeque<>(findPathBetweenValves(currentHumanValve, humanTarget));
                    humanActivePathCopy.removeFirst();
                    var elephantActivePathCopy = new ArrayDeque<>(elephantActivePath);

                    var humanPathCopy = new ArrayList<>(humanPath);
                    humanPathCopy.add(humanActivePathCopy.removeFirst());
                    var elephantPathCopy = new ArrayList<>(elephantPath);

                    var valveStatesCopy = new HashMap<>(valveStates);
                    findForTwo(humanPathCopy, humanActivePathCopy, elephantPathCopy, elephantActivePathCopy, valveStatesCopy, currentMinute, pressureReleased);
                }
            } else {
                for (String elephantTarget : valvesToVisit) {
                    var elephantActivePathCopy = new ArrayDeque<>(findPathBetweenValves(currentElephantValve, elephantTarget));
                    elephantActivePathCopy.removeFirst();
                    var humanActivePathCopy = new ArrayDeque<>(humanActivePath);

                    var elephantPathCopy = new ArrayList<>(elephantPath);
                    elephantPathCopy.add(elephantActivePathCopy.removeFirst());
                    var humanPathCopy = new ArrayList<>(humanPath);

                    var valveStatesCopy = new HashMap<>(valveStates);
                    findForTwo(humanPathCopy, humanActivePathCopy, elephantPathCopy, elephantActivePathCopy, valveStatesCopy, currentMinute, pressureReleased);
                }
            }
        }
    }
}
