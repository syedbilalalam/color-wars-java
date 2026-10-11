package io.github.flower.api.record;

import java.nio.file.Path;

public class Resources {
    private final Path game;
    private final Path launcherLib;

    public Resources(Path game, Path launcherLib) {
        this.game = game;
        this.launcherLib = launcherLib;
    }

    public Path getGame() {
        return game;
    }

    public Path getLauncherLib() {
        return launcherLib;
    }
}
