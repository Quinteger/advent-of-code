package dev.quinteger.aoc.days;

import dev.quinteger.aoc.Solution;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day19 extends Solution {
    private static final Pattern pattern = Pattern.compile("^Blueprint (\\d+): Each ore robot costs (\\d+) ore\\. Each clay robot costs (\\d+) ore\\. Each obsidian robot costs (\\d+) ore and (\\d+) clay\\. Each geode robot costs (\\d+) ore and (\\d+) obsidian\\.$");

    private record Blueprint(int oreRobotOreCost, int clayRobotOreCost, int obsidianRobotOreCost, int obsidianRobotClayCost, int geodeRobotOreCost, int geodeRobotObsidianCost) {}
    private record Resources(int ore, int clay, int obsidian, int geode) {
        public boolean canBuildOreRobot(Blueprint blueprint) {
            return ore >= blueprint.oreRobotOreCost();
        }

        public Resources buildOreRobot(Blueprint blueprint) {
            return new Resources(ore - blueprint.oreRobotOreCost(), clay, obsidian, geode);
        }

        public boolean canBuildClayRobot(Blueprint blueprint) {
            return ore >= blueprint.clayRobotOreCost();
        }

        public Resources buildClayRobot(Blueprint blueprint) {
            return new Resources(ore - blueprint.clayRobotOreCost(), clay, obsidian, geode);
        }

        public boolean canBuildObsidianRobot(Blueprint blueprint) {
            return ore >= blueprint.obsidianRobotOreCost() && clay >= blueprint.obsidianRobotClayCost();
        }

        public Resources buildObsidianRobot(Blueprint blueprint) {
            return new Resources(ore - blueprint.obsidianRobotOreCost(), clay - blueprint.obsidianRobotClayCost(), obsidian, geode);
        }

        public boolean canBuildGeodeRobot(Blueprint blueprint) {
            return ore >= blueprint.geodeRobotOreCost() && obsidian >= blueprint.geodeRobotObsidianCost();
        }

        public Resources buildGeodeRobot(Blueprint blueprint) {
            return new Resources(ore - blueprint.geodeRobotOreCost(), clay, obsidian - blueprint.geodeRobotObsidianCost(), geode);
        }
    }
    private record Robots(int oreRobots, int clayRobots, int obsidianRobots, int geodeRobots) {
        public Robots addOreRobot() {
            return new Robots(oreRobots + 1, clayRobots, obsidianRobots, geodeRobots);
        }
        public Robots addClayRobot() {
            return new Robots(oreRobots, clayRobots + 1, obsidianRobots, geodeRobots);
        }
        public Robots addObsidianRobot() {
            return new Robots(oreRobots, clayRobots, obsidianRobots + 1, geodeRobots);
        }
        public Robots addGeodeRobot() {
            return new Robots(oreRobots, clayRobots, obsidianRobots, geodeRobots + 1);
        }
    }
    private record State(Resources resources, Robots robots, int minute) {
        public State setResources(Resources resources) {
            if (!this.resources.equals(resources)) {
                return new State(resources, robots, minute);
            }
            return this;
        }

        public State addMinutes(int minutes) {
            if (minutes != 0) {
                return new State(resources, robots, minute + minutes);
            }
            return this;
        }
    }

    private final Map<Integer, Blueprint> blueprints = new HashMap<>();

    private void parseBlueprints(List<String> input) {
        blueprints.clear();
        for (String line : input) {
            var matcher = pattern.matcher(line);
            if (matcher.matches() && matcher.groupCount() == 7) {
                int id = Integer.parseInt(matcher.group(1));
                int oreRobotOreCost = Integer.parseInt(matcher.group(2));
                int clayRobotOreCost = Integer.parseInt(matcher.group(3));
                int obsidianRobotOreCost = Integer.parseInt(matcher.group(4));
                int obsidianRobotClayCost = Integer.parseInt(matcher.group(5));
                int geodeRobotOreCost = Integer.parseInt(matcher.group(6));
                int geodeRobotObsidianCost = Integer.parseInt(matcher.group(7));
                blueprints.put(id, new Blueprint(oreRobotOreCost, clayRobotOreCost, obsidianRobotOreCost, obsidianRobotClayCost, geodeRobotOreCost, geodeRobotObsidianCost));
            } else {
                throw new RuntimeException();
            }
        }
        for (Map.Entry<Integer, Blueprint> entry : blueprints.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    @Override
    public Object solvePart1(List<String> input, boolean example) {
        parseBlueprints(input);
        var result = blueprints.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> findBestForBlueprint(e.getValue(), 24)));
        System.out.println(result);
        return result.entrySet().stream().mapToInt(e -> e.getKey() * e.getValue()).sum();
    }

    private int findBestForBlueprint(Blueprint blueprint, int maxMinutes) {
        max = 0;
        findBestForBlueprintRecursive(blueprint, new State(new Resources(0, 0, 0, 0), new Robots(1, 0, 0, 0), 1), maxMinutes);
        return max;
    }

    private int max = 0;

    private void findBestForBlueprintRecursive(Blueprint blueprint, State state, int maxMinutes) {
        var resources = state.resources();
        var robots = state.robots();
        int minute = state.minute();

        boolean canBuildOreRobot = resources.canBuildOreRobot(blueprint);
        boolean canBuildClayRobot = resources.canBuildClayRobot(blueprint);
        boolean canBuildObsidianRobot = resources.canBuildObsidianRobot(blueprint);
        boolean canBuildGeodeRobot = resources.canBuildGeodeRobot(blueprint);

        resources = new Resources(resources.ore() + robots.oreRobots(), resources.clay() + robots.clayRobots(), resources.obsidian() + robots.obsidianRobots(), resources.geode() + robots.geodeRobots());
        state = state.setResources(resources);

        if (state.minute() >= maxMinutes) {
            if (state.resources().geode() > max) {
//                System.out.println(state);
                max = state.resources().geode();
            }
            return;
        }

        var newStates = new HashSet<State>();
        newStates.add(state.addMinutes(1));
        if (canBuildOreRobot) {
            newStates.add(new State(resources.buildOreRobot(blueprint), robots.addOreRobot(), minute + 1));
        }
        if (canBuildClayRobot) {
            newStates.add(new State(resources.buildClayRobot(blueprint), robots.addClayRobot(), minute + 1));
        }
        if (canBuildObsidianRobot) {
            newStates.add(new State(resources.buildObsidianRobot(blueprint), robots.addObsidianRobot(), minute + 1));
        }
        if (canBuildGeodeRobot) {
            newStates.add(new State(resources.buildGeodeRobot(blueprint), robots.addGeodeRobot(), minute + 1));
        }

        for (State newState : newStates) {
            if (shouldContinue(blueprint, newState, maxMinutes)) {
                findBestForBlueprintRecursive(blueprint, newState, maxMinutes);
            }
        }
    }

    private boolean shouldContinue(Blueprint blueprint, State state, int maxMinutes) {
        int ore = state.resources().ore();
        int clay = state.resources().clay();
        int obsidian = state.resources().obsidian();
        int geode = state.resources().geode();

        int oreRobots = state.robots().oreRobots();
        int clayRobots = state.robots().clayRobots();
        int obsidianRobots = state.robots().obsidianRobots();
        int geodeRobots = state.robots().geodeRobots();

        int minute = state.minute();
        for (; minute <= maxMinutes; minute++) {
            boolean canBuildClayRobot = ore >= blueprint.clayRobotOreCost();
            boolean canBuildObsidianRobot = clay >= blueprint.obsidianRobotClayCost();
            boolean canBuildGeodeRobot = obsidian >= blueprint.geodeRobotObsidianCost();

            ore += oreRobots;
            clay += clayRobots;
            obsidian += obsidianRobots;
            geode += geodeRobots;

            oreRobots++;
            if (canBuildClayRobot) {
                ore -= blueprint.clayRobotOreCost();
                clayRobots++;
            }
            if (canBuildObsidianRobot) {
                clay -= blueprint.obsidianRobotClayCost();
                obsidianRobots++;
            }
            if (canBuildGeodeRobot) {
                obsidian -= blueprint.geodeRobotObsidianCost();
                geodeRobots++;
            }
        }

        return geode > max;
    }

    @Override
    public Object solvePart2(List<String> input, boolean example) {
        parseBlueprints(input);
        var result = blueprints.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getKey))
                .limit(3)
                .collect(Collectors.toMap(Map.Entry::getKey, e -> findBestForBlueprint(e.getValue(), 32)));
        System.out.println(result);
        return result.values().stream().mapToInt(e -> e).reduce(1, (i1, i2) -> i1 * i2);
    }
}
