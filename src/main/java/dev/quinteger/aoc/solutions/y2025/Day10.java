package dev.quinteger.aoc.solutions.y2025;

import dev.quinteger.aoc.solutions.Solution;
import dev.quinteger.aoc.util.PathfnderUtilsKt;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day10 extends Solution {

    private final List<MachineInfo> machineInfos;

    public Day10(List<String> input) {
        this.machineInfos = input.stream()
                .map(line -> line.split(" "))
                .map(split -> {
                    List<Boolean> indicatorLights = split[0].chars()
                            .mapToObj(c -> switch (c) {
                                case '.' -> Boolean.FALSE;
                                case '#' -> Boolean.TRUE;
                                default -> null;
                            })
                            .filter(Objects::nonNull)
                            .toList();
                    List<Set<Integer>> buttonWirings = Arrays.stream(split)
                            .limit(split.length - 1)
                            .skip(1)
                            .map(buttonWiringString -> Arrays.stream(buttonWiringString.split(","))
                                    .map(buttonWiringElement -> buttonWiringElement.replaceAll("[()]", ""))
                                    .map(Integer::parseInt)
                                    .collect(Collectors.toUnmodifiableSet())
                            )
                            .toList();
                    List<Integer> joltageRequirements = Arrays.stream(split)
                            .skip(split.length - 1)
                            .findFirst()
                            .map(requirementsString -> Arrays.stream(requirementsString.split(","))
                                    .map(requirementsElement -> requirementsElement.replaceAll("[{}]", ""))
                                    .map(Integer::parseInt)
                                    .toList()
                            )
                            .orElseThrow();
                    return new MachineInfo(indicatorLights, buttonWirings, joltageRequirements);
                })
                .toList();
    }

    @Override
    public Object solvePart1(List<String> input, boolean example) {
        long totalButtonPresses = 0;
        for (MachineInfo machineInfo : machineInfos) {
            List<Set<Integer>> buttonWirings = machineInfo.buttonWirings();
            List<Boolean> requiredIndicatorLights = machineInfo.requiredIndicatorLights();
            Map<List<Integer>, List<Boolean>> stateMap = Map.of(Collections.emptyList(), IntStream.range(0, requiredIndicatorLights.size()).mapToObj(_ -> Boolean.FALSE).toList());
            while (true) {
                Map<List<Integer>, List<Boolean>> newStateMap = new HashMap<>();
                boolean found = false;
                for (Map.Entry<List<Integer>, List<Boolean>> stateEntry : stateMap.entrySet()) {
                    List<Integer> buttonWiringOrder = stateEntry.getKey();
                    List<Boolean> indicatorLightsState = stateEntry.getValue();
                    Integer lastButtonWiringIndex;
                    if (buttonWiringOrder.isEmpty()) {
                        lastButtonWiringIndex = null;
                    } else {
                        lastButtonWiringIndex = buttonWiringOrder.getLast();
                    }
                    for (int nextButtonWiringIndex = 0; nextButtonWiringIndex < buttonWirings.size(); nextButtonWiringIndex++) {
                        if (lastButtonWiringIndex != null && lastButtonWiringIndex == nextButtonWiringIndex) {
                            continue;
                        }
                        List<Integer> newButtonWiringOrder = new ArrayList<>(buttonWiringOrder);
                        newButtonWiringOrder.add(nextButtonWiringIndex);
                        List<Boolean> existingIndicatorLightsState = newStateMap.get(newButtonWiringOrder);
                        if (existingIndicatorLightsState == null) {
                            List<Boolean> newIndicatorLightsState = new ArrayList<>(indicatorLightsState);
                            Set<Integer> buttonWiring = buttonWirings.get(nextButtonWiringIndex);
                            for (Integer button : buttonWiring) {
                                newIndicatorLightsState.set(button, !newIndicatorLightsState.get(button));
                            }
                            if (newIndicatorLightsState.equals(requiredIndicatorLights)) {
                                totalButtonPresses += newButtonWiringOrder.size();
                                found = true;
                                break;
                            }
                            newStateMap.put(Collections.unmodifiableList(newButtonWiringOrder), Collections.unmodifiableList(newIndicatorLightsState));
                        }
                    }
                    if (found) {
                        break;
                    }
                }
                if (found) {
                    break;
                } else {
                    stateMap = newStateMap;
                }
            }
        }
        return totalButtonPresses;
    }

    @Override
    public Object solvePart2(List<String> input, boolean example) {
        long totalSteps = 0;
        for (MachineInfo machineInfo : machineInfos) {
            System.out.println("Fetching next MachineInfo: " + machineInfo);
            List<Set<Integer>> buttonWirings = machineInfo.buttonWirings();
            List<Integer> requiredJoltageState = machineInfo.joltageRequirements();
            List<Integer> initialJoltageState = IntStream.range(0, requiredJoltageState.size()).mapToObj(_ -> 0).toList();
            JoltageSettingsState initialJoltageSettingsState = new JoltageSettingsState(initialJoltageState, Map.of());
            List<JoltageSettingsState> result = PathfnderUtilsKt.bfsFindShortestPath(
                    initialJoltageSettingsState,
                    (JoltageSettingsState joltageSettingsState) -> {
                        List<Integer> joltageState = joltageSettingsState.joltageState();
                        Collection<JoltageSettingsState> newJoltageSettingsStates = new ArrayList<>();
                        for (int buttonWiringIndex = 0; buttonWiringIndex < buttonWirings.size(); buttonWiringIndex++) {
                            Set<Integer> buttonWiring = buttonWirings.get(buttonWiringIndex);
                            Integer maxAllowedButtonPresses = null;
                            for (Integer counterIndex : buttonWiring) {
                                int currentCounterValue = joltageState.get(counterIndex);
                                int requiredCounterValue = requiredJoltageState.get(counterIndex);
                                int diff = requiredCounterValue - currentCounterValue;
                                if (maxAllowedButtonPresses == null || diff < maxAllowedButtonPresses) {
                                    maxAllowedButtonPresses = diff;
                                }
                            }
                            Objects.requireNonNull(maxAllowedButtonPresses);
//                            for (int i = 1; i <= maxAllowedButtonPresses; i++) {
//                                List<Integer> newJoltageState = new ArrayList<>(joltageState);
//                                for (Integer buttonIndex : buttonWiring) {
//                                    newJoltageState.set(buttonIndex, newJoltageState.get(buttonIndex) + i);
//                                }
//                                Map<Integer, Integer> newButtonSelectionState = new HashMap<>(joltageSettingsState.buttonSelectionState());
//                                newButtonSelectionState.merge(buttonWiringIndex, i, Integer::sum);
//                                newJoltageSettingsStates.add(new JoltageSettingsState(newJoltageState, newButtonSelectionState));
//                            }
                            if (maxAllowedButtonPresses > 0) {
                                List<Integer> newJoltageState = new ArrayList<>(joltageState);
                                for (Integer buttonIndex : buttonWiring) {
                                    newJoltageState.set(buttonIndex, newJoltageState.get(buttonIndex) + maxAllowedButtonPresses);
                                }
                                Map<Integer, Integer> newButtonSelectionState = new HashMap<>(joltageSettingsState.buttonSelectionState());
                                newButtonSelectionState.merge(buttonWiringIndex, maxAllowedButtonPresses, Integer::sum);
                                newJoltageSettingsStates.add(new JoltageSettingsState(newJoltageState, newButtonSelectionState));
                            }
                        }
                        return newJoltageSettingsStates;
                    },
                    (JoltageSettingsState joltageSettingsState) -> joltageSettingsState.joltageState().equals(requiredJoltageState),
                    (List<? extends JoltageSettingsState> path) -> path.getLast().countButtonPresses(),
                    true
            );
            totalSteps += result.getLast().countButtonPresses();
        }
        return totalSteps;
    }

    private record MachineInfo(List<Boolean> requiredIndicatorLights, List<Set<Integer>> buttonWirings, List<Integer> joltageRequirements) {}
    
    private record JoltageSettingsState(List<Integer> joltageState, Map<Integer, Integer> buttonSelectionState) {

        @Override
        public int hashCode() {
            return Objects.hash(joltageState);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof JoltageSettingsState(List<Integer> otherJoltageState, _)) {
                return Objects.equals(joltageState, otherJoltageState);
            }
            return false;
        }
        
        public long countButtonPresses() {
            return buttonSelectionState.values().stream().mapToLong(i -> i).sum();
        }
    }
}
