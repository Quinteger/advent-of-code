package dev.quinteger.aoc;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.Collections;
import java.util.List;

public class AOCLauncher {
    private static final int MIN = 1;
    private static final int MAX = 1;

    public static void main(String[] args) {
        var arg = getArg(args);
        int intArg = getIntArg(arg);

        var suffix = "%02d".formatted(intArg);

        Class<? extends Solution> solutionClass = getSolutionClass(suffix);
        Constructor<? extends Solution> constructor = getSolutionClassConstructor(solutionClass);

        var fileName = "/day%s.txt".formatted(suffix);

        URL url = AOCLauncher.class.getResource(fileName);
        if (url == null) {
            throw new RuntimeException("Resource %s does not exist".formatted(fileName));
        }

        URI uri;
        try {
            uri = url.toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        List<String> lines;
        var protocol = url.getProtocol();
        if (protocol.equals("jar")) {
            try (var fileSystem = createFileSystem(uri)) {
                lines = Files.readAllLines(fileSystem.getPath(fileName));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                lines = Files.readAllLines(Path.of(uri));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Solution solution = createSolutionInstance(constructor, lines);
        solution.solve();
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

    private static Class<? extends Solution> getSolutionClass(String suffix) {
        try {
            return Class.forName("dev.quinteger.aoc.days.Day%s".formatted(suffix)).asSubclass(Solution.class);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class for the specified solution is not implemented", e);
        } catch (ClassCastException e) {
            throw new RuntimeException("Class does not extend Solution", e);
        }
    }

    private static Constructor<? extends Solution> getSolutionClassConstructor(Class<? extends Solution> solutionClass) {
        try {
            return solutionClass.getConstructor(List.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Class does not have the required constructor", e);
        }
    }

    private static FileSystem createFileSystem(URI uri) throws IOException {
        try {
            return FileSystems.getFileSystem(uri);
        } catch (FileSystemNotFoundException e) {
            return FileSystems.newFileSystem(uri, Collections.emptyMap());
        }
    }

    private static Solution createSolutionInstance(Constructor<? extends Solution> constructor, List<String> lines) {
        try {
            return constructor.newInstance(lines);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Reflection error while trying to instantiate the solution class", e);
        }
    }
}