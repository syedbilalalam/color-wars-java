package io.github.flower.api;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Stream;

public class ResourceLoader {

    /**
     * Copies a classpath folder (e.g. "/native") into a fresh temp directory.
     * @return the temp directory containing the extracted files
     */
    public static Path extractResourceFolder(String resourceFolder) {
        try {
            if (!resourceFolder.startsWith("/")) {
                resourceFolder = "/" + resourceFolder;
            }

            URL url = NativeLibraryLoader.class.getResource(resourceFolder);
            if (url == null) {
                throw new RuntimeException("Resource folder not found: " + resourceFolder);
            }

            Path tempDir = Files.createTempDirectory("res");
            Runtime.getRuntime().addShutdownHook(new Thread(() -> deleteRecursively(tempDir)));

            URI uri = url.toURI();

            if ("jar".equals(uri.getScheme())) {
                // Running from a JAR: mount it as a filesystem
                try (FileSystem fs = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
                    copyFolder(fs.getPath(resourceFolder), tempDir);
                } catch (FileSystemAlreadyExistsException e) {
                    // Already mounted by something else, reuse it
                    copyFolder(FileSystems.getFileSystem(uri).getPath(resourceFolder), tempDir);
                }
            } else {
                // Running from IDE / exploded classes directory
                copyFolder(Paths.get(uri), tempDir);
            }

            return tempDir;
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Failed to extract resource folder: " + resourceFolder, e);
        }
    }

    private static void copyFolder(Path source, Path target) throws IOException {
        try (Stream<Path> walk = Files.walk(source)) {
            walk.forEach(src -> {
                try {
                    // relativize() across different filesystems needs string-based resolution
                    String rel = source.relativize(src).toString();
                    Path dest = target.resolve(rel);
                    if (Files.isDirectory(src)) {
                        Files.createDirectories(dest);
                    } else {
                        Files.createDirectories(dest.getParent());
                        Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
                        dest.toFile().setExecutable(true); // harmless for libs, needed on some platforms
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Failed to copy " + src, e);
                }
            });
        }
    }

    private static void deleteRecursively(Path dir) {
        try (Stream<Path> walk = Files.walk(dir)) {
            walk.sorted(Comparator.reverseOrder())
                    .forEach(p -> p.toFile().delete());
        } catch (IOException ignored) {
        }
    }

    /**
     * Extracts the folder, then loads the given libraries in order.
     * Order matters if one library depends on another.
     */
    public static void loadFromResourceFolder(String resourceFolder, String... libFileNames) {
        Path dir = extractResourceFolder(resourceFolder);
        for (String name : libFileNames) {
            Path lib = dir.resolve(name);
            if (!Files.exists(lib)) {
                throw new RuntimeException("Library not found in extracted folder: " + lib);
            }
            System.load(lib.toAbsolutePath().toString());
        }
    }
}