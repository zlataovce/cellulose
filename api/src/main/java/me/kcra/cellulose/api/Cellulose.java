package me.kcra.cellulose.api;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.File;
import java.util.List;

public interface Cellulose {
    @NotNull
    @SneakyThrows
    static Cellulose getInstance() {
        final Cellulose pluginInstance = (Cellulose) Class.forName("me.kcra.cellulose.CellulosePlugin").getDeclaredField("INSTANCE").get(null);
        if (pluginInstance == null || !pluginInstance.isEnabled()) {
            throw new UnsupportedOperationException("Cellulose is not loaded");
        }
        return pluginInstance;
    }

    boolean isEnabled();

    File getScriptsFolder();

    @UnmodifiableView
    List<Object> getLoadedScripts();

    @Nullable
    Object loadScript(@NotNull File file, boolean silent, boolean handleEnable);

    @Nullable
    Object loadScript(@NotNull String script, @Nullable String name, boolean silent, boolean handleEnable);
}
