package dev.quinteger.aoc;

import dev.quinteger.aoc.solutions.Solution;

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
        Constructor<? extends Solution> constructor = getSolutionClassConstructor(solutionClass);

        Solution solution = createSolutionInstance(constructor);


        String inputPath;
        String solutionPath;
        String message;

        String user = settings.user();
        if (user == null) {
            inputPath = "/%d/day%s/input.txt".formatted(year, suffix);
            solutionPath = "/%d/day%s/answer.txt".formatted(year, suffix);
            message = "Firing example solution";
        } else {
            inputPath = "/%d/day%s/%s/input.txt".formatted(year, suffix, settings.user());
            solutionPath = "/%d/day%s/%s/answer.txt".formatted(year, suffix, settings.user());
            message = "Firing solution for user " + user;
        }


        var inputLines = Collections.unmodifiableList(readLines(inputPath, true));
        var solutionLines = Collections.unmodifiableList(readLines(solutionPath, false));

        if (!inputLines.isEmpty()) {
            System.out.println(message);
            if (solutionLines.isEmpty()) {
                solution.solve(inputLines, true, "", "");
            } else if (solutionLines.size() == 2) {
                solution.solve(inputLines, true, solutionLines.get(0), solutionLines.get(1));
            } else {
                throw new IllegalArgumentException("Solution file must have 2 lines");
            }
        }
    }

    private static String getArg(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Please specify a single argument with the day number");
        }
        return args[0];
    }

    private static int getIntArg(String arg) {
        int intArg;
        try {
            intArg = Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("The specified argument is not a valid integer", e);
        }
        if (intArg < MIN || intArg > MAX) {
            throw new IllegalArgumentException("The argument \"%d\" is out of bounds, should be between %d and %d".formatted(intArg, MIN, MAX));
        }
        return intArg;
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

    private static Constructor<? extends Solution> getSolutionClassConstructor(Class<? extends Solution> solutionClass) {
        try {
            return solutionClass.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Class does not have the required constructor", e);
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

    private interface ResourceReader<R> {
        R readResource(Path path) throws IOException;
    }

    private static FileSystem createFileSystem(URI uri) throws IOException {
        try {
            return FileSystems.getFileSystem(uri);
        } catch (FileSystemNotFoundException e) {
            return FileSystems.newFileSystem(uri, Collections.emptyMap());
        }
    }

    private static Solution createSolutionInstance(Constructor<? extends Solution> constructor) {
        try {
            return constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Reflection error while trying to instantiate the solution class", e);
        }
    }
}