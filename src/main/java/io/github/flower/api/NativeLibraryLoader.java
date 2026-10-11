package io.github.flower.api;

import io.github.flower.api.external.Launcher;
import io.github.flower.api.record.Resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class NativeLibraryLoader {

    private static volatile boolean loaded = false;

    public static synchronized Resources load(String libName) {
        final String PLATFORM_NAME = detectPlatformDir();
        final String LIB_FILE_NAME = detectLibFileName(libName);
        final String RESOURCE_PATH = "/";
        Path resourceDir;
        Path libResourceFilePath;
        Path gameResourceDir;
        Resources resources;

        if (loaded) return Launcher.getResources();

        resourceDir = ResourceLoader.extractResourceFolder(RESOURCE_PATH);
        libResourceFilePath = Path.of(
                resourceDir.toAbsolutePath().toString(), "game", "bin", PLATFORM_NAME, LIB_FILE_NAME
        );
        gameResourceDir = Path.of(resourceDir.toAbsolutePath().toString(), "game");

        resources = new Resources(gameResourceDir, libResourceFilePath);

        loaded = true;
        return resources;
    }

    private static String detectPlatformDir() {
        String os = System.getProperty("os.name").toLowerCase();
        String arch = normalizeArch(System.getProperty("os.arch"));

        String osPart;
        if (os.contains("win")) {
            osPart = "windows";
        } else if (os.contains("mac") || os.contains("darwin")) {
            osPart = "macos";
        } else if (os.contains("nux") || os.contains("nix")) {
            osPart = "linux";
        } else {
            throw new UnsatisfiedLinkError("Unsupported OS: " + os);
        }

        return osPart + "-" + arch;
    }

    private static String normalizeArch(String rawArch) {
        String arch = rawArch.toLowerCase();
        if (arch.equals("x86_64") || arch.equals("amd64")) {
            return "x64";
        } else if (arch.equals("aarch64") || arch.equals("arm64")) {
            return "arm64";
        }
        throw new UnsatisfiedLinkError("Unsupported architecture: " + rawArch);
    }

    private static String detectLibFileName(String libName) {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return libName + ".dll";
        } else if (os.contains("mac") || os.contains("darwin")) {
            return "lib" + libName + ".dylib";
        } else {
            return "lib" + libName + ".so";
        }
    }
}