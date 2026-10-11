package io.github.flower.api.external;

import io.github.flower.api.NativeLibraryLoader;
import io.github.flower.api.NativeLibraryName;
import io.github.flower.api.record.Resources;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.nio.file.Path;

public class Launcher {
    private static Resources resources;

    static {
        setResources(
            NativeLibraryLoader.load(NativeLibraryName.FROST_COLOR_WARS)
        );
    }

    public static void launchColorWars() {
        try (Arena arena = Arena.ofConfined()) {
            SymbolLookup lib = SymbolLookup.libraryLookup(resources.getLauncherLib(), arena);   // loads the DLL

            Linker linker = Linker.nativeLinker();
            MethodHandle launchGame = linker.downcallHandle(
                    lib.find("frost_colorwars_launch").orElseThrow(),
                    FunctionDescriptor.ofVoid(ValueLayout.ADDRESS)
            );

            MemorySegment gameResource = arena.allocateFrom(resources.getGame().toAbsolutePath().toString());
            launchGame.invokeExact(gameResource);
//            System.out.println(result); // 5
        } // <- DLL is unloaded here
        catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static Resources getResources() {
        return resources;
    }

    public static void setResources(Resources resources) {
        Launcher.resources = resources;
    }
}
