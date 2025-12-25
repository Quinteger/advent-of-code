package dev.quinteger.aoc;

import dev.quinteger.aoc.solutions.Solution;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.Collections;
import java.util.List;

public class AOCLauncher {
    private static final int MIN = 1;
    private static final int MAX = 25;

    public static void main(String[] args) {
//        var arg = getArg(args);
//        int intArg = getIntArg(arg);
        var builder = new SettingsBuilder();
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-y" -> {
                    builder.setYear(Integer.parseInt(args[i + 1]));
                    i++;
                }
                case "-d" -> {
                    builder.setDay(Integer.parseInt(args[i + 1]));
                    i++;
                }
                case "-u" -> {
                    builder.setUser(args[i + 1]);
                    i++;
                }
                default -> throw new IllegalArgumentException("Unknown config parameter: " + args[i]);
            }
        }

        var settings = builder.build();
        int year = settings.year();
        int day = settings.day();
        System.out.printf("Firing solution for year %d, day %d%n", year, day);

        var suffix = "%02d".formatted(day);
        Class<? extends Solution> solutionClass = getSolutionClass(year, suffix);

        String part1InputPath;
        String part2InputPath;
        String solutionPath;
        String message;

        String user = settings.user();
        if (user == null) {
            part1InputPath = "/%d/day%s/input.txt".formatted(year, suffix);
            part2InputPath = "/%d/day%s/input2.txt".formatted(year, suffix);
            solutionPath = "/%d/day%s/answer.txt".formatted(year, suffix);
            message = "Firing example solution";
        } else {
            part1InputPath = "/%d/day%s/%s/input.txt".formatted(year, suffix, settings.user());
            part2InputPath = part1InputPath;
            solutionPath = "/%d/day%s/%s/answer.txt".formatted(year, suffix, settings.user());
            message = "Firing solution for user " + user;
        }

        var part1InputLines = readLines(part1InputPath, true);
        boolean example = user == null;
        List<String> part2InputLines;
        if (example) {
            var maybePart2InputLines = readLines(part2InputPath, false);
            if (maybePart2InputLines != null) {
                part2InputLines = maybePart2InputLines;
            } else {
                part2InputLines = part1InputLines;
            }
        } else {
            part2InputLines = part1InputLines;
        }

        Solution solution = createSolutionInstance(solutionClass, part1InputLines, part2InputLines, example);

        var solutionLines = readLines(solutionPath, false);

        if (!part1InputLines.isEmpty()) {
            System.out.println(message);
            if (solutionLines.isEmpty()) {
                solution.solve(part1InputLines, part2InputLines, example, null, null);
            } else if (solutionLines.size() == 2) {
                solution.solve(part1InputLines, part2InputLines, example, solutionLines.get(0), solutionLines.get(1));
            } else {
                throw new IllegalArgumentException("Solution file must have 2 lines");
            }
        }
    }

    private static Class<? extends Solution> getSolutionClass(int year, String deySuffix) {
        try {
            return Class.forName("dev.quinteger.aoc.solutions.y%d.Day%s".formatted(year, deySuffix)).asSubclass(Solution.class);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class for the specified solution is not implemented", e);
        } catch (ClassCastException e) {
            throw new RuntimeException("Class does not extend Solution", e);
        }
    }

    private static List<String> readLines(String resource, boolean required) {
        var lines = readResource(resource, Files::readAllLines, required);
        if (lines == null) {
            return Collections.emptyList();
        }
        else {
            return Collections.unmodifiableList(lines);
        }
    }

    private static String readString(String resource, boolean required) {
        return readResource(resource, Files::readString, required);
    }

    private static <R> R readResource(String resource, ResourceReader<R> resourceReader, boolean required) {
        URL url = AOCLauncher.class.getResource(resource);
        if (url == null) {
            if (required) {
                throw new RuntimeException("Resource %s does not exist".formatted(resource));
            } else {
                return null;
            }
        }

        URI uri;
        try {
            uri = url.toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        R result;
        var protocol = url.getProtocol();
        if (protocol.equals("jar")) {
            try (var fileSystem = createFileSystem(uri)) {
                var path = fileSystem.getPath(resource);
                if (!Files.isRegularFile(path)) {
                    return null;
                }
                result = resourceReader.readResource(path);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        } else {
            try {
                var path = Path.of(uri);
                if (!Files.isRegularFile(path)) {
                    return null;
                }
                result = resourceReader.readResource(path);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return result;
    }

    private static FileSystem createFileSystem(URI uri) throws IOException {
        try {
            return FileSystems.getFileSystem(uri);
        } catch (FileSystemNotFoundException e) {
            return FileSystems.newFileSystem(uri, Collections.emptyMap());
        }
    }
    private static Solution createSolutionInstance(
            Class<? extends Solution> solutionClass,
            List<String> part1InputLines,
            List<String> part2InputLines,
            boolean example
    ) {
        try {
            Constructor<? extends Solution> constructor = getSolutionConstructorIfExists(solutionClass, List.class, List.class, boolean.class);
            if (constructor != null) {
                return callAndTimeConstructor(constructor, part1InputLines, part2InputLines, example);
            }
            constructor = getSolutionConstructorIfExists(solutionClass, List.class, boolean.class);
            if (constructor != null) {
                return callAndTimeConstructor(constructor, part1InputLines, example);
            }
            constructor = getSolutionConstructorIfExists(solutionClass, List.class);
            if (constructor != null) {
                return callAndTimeConstructor(constructor, part1InputLines);
            }
            constructor = solutionClass.getConstructor();
            return callAndTimeConstructor(constructor, part1InputLines);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Reflection error while trying to instantiate the solution class", e);
        }
    }

    @Nullable
    private static Constructor<? extends Solution> getSolutionConstructorIfExists(Class<? extends Solution> solutionClass, Class<?>... parameterTypes) {
        try {
            return solutionClass.getConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
    
    private static Solution callAndTimeConstructor(Constructor<? extends Solution> constructor, Object... args) throws ReflectiveOperationException {
        long start = System.nanoTime();
        Solution solution = constructor.newInstance(args);
        long end = System.nanoTime();
        System.out.printf("Created solution instance of type %s in %.3fms%n", solution.getClass().getSimpleName(), (end - start) / 1e6D);
        return solution;
    }

    @FunctionalInterface
    private interface ResourceReader<R> {

        R readResource(Path path) throws IOException;
    }
}








