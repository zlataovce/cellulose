package me.kcra.cellulose.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.File;
import java.util.List;

public interface Cellulose {
    @NotNull
    Cellulose getInstance();

    File getScriptsFolder();

    @UnmodifiableView
    List<Object> getLoadedScripts();

    @Nullable
    Object loadScript(@NotNull File file, boolean silent);

    @Nullable
    Object loadScript(@NotNull String script, @Nullable String name, boolean silent);
}
