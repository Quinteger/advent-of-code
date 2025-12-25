package dev.quinteger.aoc.solutions.y2025;

import dev.quinteger.aoc.solutions.Solution;
import dev.quinteger.aoc.util.PathfnderUtilsKt;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day11 extends Solution {

    private final Map<String, Device> devicesByNames;
    private final Device youDevice;
    private final Device svrDevice;
    private final Device outDevice;
    private final Map<String, List<Device>> parentDevices;

    public Day11(List<String> input) {
        Map<String, Device> devicesByNames = associateDevicesByNames(input);
        this.youDevice = devicesByNames.get("you");
        this.svrDevice = devicesByNames.get("svr");
        Device outDevice = new Device("out", List.of());
        this.outDevice = outDevice;
        devicesByNames.put("out", outDevice);
        Map<String, List<Device>> parentDevices = new HashMap<>();
        for (Device device : devicesByNames.values()) {
            for (String output : device.outputs()) {
                parentDevices.computeIfAbsent(output, _ -> new ArrayList<>()).add(device);
            }
        }
        this.devicesByNames = Collections.unmodifiableMap(devicesByNames);
        this.parentDevices = parentDevices.entrySet()
                .stream()
                .peek(e -> e.setValue(Collections.unmodifiableList(e.getValue())))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Object solvePart1(List<String> input, boolean example) {
        return PathfnderUtilsKt.bfsCountAllDistinctPaths(
                youDevice,
                device -> device.outputs().stream().map(devicesByNames::get).toList(),
                outDevice
        );
    }

    @Override
    public Object solvePart2(List<String> input, boolean example) {
        Map<String, Device> devicesByNames;
        Device svrDevice;
        if (example) {
            devicesByNames = associateDevicesByNames(input);
            devicesByNames.put("out", outDevice);
            svrDevice = devicesByNames.get("svr");
        } else {
            devicesByNames = this.devicesByNames;
            svrDevice = this.svrDevice;
        }
        Queue<Device> nodesToCheck = new ArrayDeque<>();
        Map<Device, Long> mundanePathsMap = new HashMap<>();
        Map<Device, Long> pathsThatContainDacMap = new HashMap<>();
        Map<Device, Long> pathsThatContainFftMap = new HashMap<>();
        Map<Device, Long> pathsThatContainDacAndFftMap = new HashMap<>();
        nodesToCheck.add(svrDevice);
        mundanePathsMap.put(svrDevice, 1L);
        pathsThatContainDacMap.put(svrDevice, 0L);
        pathsThatContainFftMap.put(svrDevice, 0L);
        pathsThatContainDacAndFftMap.put(svrDevice, 0L);
        Map<Device, List<Device>> devicesWithUncheckedParents = new HashMap<>();
        for (Device device : devicesByNames.values()) {
            for (String output : device.outputs()) {
                devicesWithUncheckedParents.computeIfAbsent(devicesByNames.get(output), _ -> new ArrayList<>()).add(device);
            }
        }
        while (true) {
            while (!nodesToCheck.isEmpty()) {
                Device deviceBeingChecked = nodesToCheck.remove();
                long mundanePaths = mundanePathsMap.get(deviceBeingChecked);
                long pathsThatContainDac = pathsThatContainDacMap.get(deviceBeingChecked);
                long pathsThatContainFft = pathsThatContainFftMap.get(deviceBeingChecked);
                long pathsThatContainDacAndFft = pathsThatContainDacAndFftMap.get(deviceBeingChecked);
                List<Device> neighbours = deviceBeingChecked.outputs().stream().map(devicesByNames::get).toList();
                for (Device neighbour : neighbours) {
                    long neighbourMundanePaths;
                    long neighbourPathsThatContainDac;
                    long neighbourPathsThatContainFft;
                    long neighbourPathsThatContainDacAndFft;
                    if (neighbour.name().equals("dac")) {
                        neighbourMundanePaths = 0L;
                        neighbourPathsThatContainDac = pathsThatContainDac + mundanePaths;
                        neighbourPathsThatContainFft = 0L;
                        neighbourPathsThatContainDacAndFft = pathsThatContainDacAndFft + pathsThatContainFft;
                    } else if (neighbour.name().equals("fft")) {
                        neighbourMundanePaths = 0L;
                        neighbourPathsThatContainDac = 0L;
                        neighbourPathsThatContainFft = pathsThatContainFft + mundanePaths;
                        neighbourPathsThatContainDacAndFft = pathsThatContainDacAndFft + pathsThatContainDac;
                    } else {
                        neighbourMundanePaths = mundanePaths;
                        neighbourPathsThatContainDac = pathsThatContainDac;
                        neighbourPathsThatContainFft = pathsThatContainFft;
                        neighbourPathsThatContainDacAndFft = pathsThatContainDacAndFft;
                    }
                    mundanePathsMap.merge(neighbour, neighbourMundanePaths, Long::sum);
                    pathsThatContainDacMap.merge(neighbour, neighbourPathsThatContainDac, Long::sum);
                    pathsThatContainFftMap.merge(neighbour, neighbourPathsThatContainFft, Long::sum);
                    pathsThatContainDacAndFftMap.merge(neighbour, neighbourPathsThatContainDacAndFft, Long::sum);
                    devicesWithUncheckedParents.get(neighbour).remove(deviceBeingChecked);
                }
            }
            if (!devicesWithUncheckedParents.isEmpty()) {
                for (var iterator = devicesWithUncheckedParents.entrySet().iterator(); iterator.hasNext(); ) {
                    var deviceWithUncheckedParents = iterator.next();
                    var uncheckedParents = deviceWithUncheckedParents.getValue();
                    if (uncheckedParents.isEmpty()) {
                        iterator.remove();
                        nodesToCheck.add(deviceWithUncheckedParents.getKey());
                    }
                }
            } else {
                break;
            }
        }
        return pathsThatContainDacAndFftMap.get(outDevice);
    }

    private static Map<String, Device> associateDevicesByNames(List<String> input) {
        return input.stream()
                .map(line -> line.split(": "))
                .map(splitLine -> {
                    String name = splitLine[0];
                    return new Device(name, Arrays.stream(splitLine[1].split(" ")).toList());
                })
                .collect(Collectors.toMap(Device::name, Function.identity()));
    }

    private record Device(String name, List<String> outputs) {}
}
