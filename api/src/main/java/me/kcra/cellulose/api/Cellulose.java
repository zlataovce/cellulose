package me.kcra.cellulose.api;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.File;
import java.util.List;

/**
 * The main entry point to the Cellulose plugin.
 */
public interface Cellulose {
    /**
     * Tries to acquire an instance of the plugin.
     *
     * @throws UnsupportedOperationException if the plugin is not loaded
     * @return the plugin instance, non-null
     */
    @NotNull
    @SneakyThrows
    static Cellulose getInstance() {
        final Cellulose pluginInstance = (Cellulose) Class.forName("me.kcra.cellulose.CellulosePlugin").getDeclaredField("INSTANCE").get(null);
        if (pluginInstance == null || !pluginInstance.isEnabled()) {
            throw new UnsupportedOperationException("Cellulose is not loaded");
        }
        return pluginInstance;
    }

    /**
     * Determines if the plugin is enabled.
     *
     * @return is the plugin enabled?
     */
    boolean isEnabled();

    /**
     * Gets the folder storing the scripts, usually <server root>/plugins/Cellulose/scripts.
     *
     * @return the 'scripts' folder, non-null
     */
    @NotNull
    File getScriptsFolder();

    /**
     * Gets the folder with compilation cache, usually <server root>/plugins/Cellulose/compiled.
     *
     * @return the 'compiled' folder, non-null
     */
    @NotNull
    File getCompiledCacheFolder();

    /**
     * Gets a list of loaded scripts (their instances).
     *
     * @return an immutable view of loaded scripts
     */
    @UnmodifiableView
    List<Object> getLoadedScripts();

    /**
     * Tries to compile and load a Kotlin script from the supplied file.<br>
     * If the script hash is found in the compilation cache, compilation is skipped.
     *
     * @param file the script file
     * @param silent should all console output of the loading process be suppressed?
     * @param handleEnable should the script's enable procedures be called?
     * @return the script instance, null if an error occurred
     */
    @Nullable
    Object loadScript(@NotNull File file, boolean silent, boolean handleEnable);

    /**
     * Tries to compile and load a Kotlin script from the supplied string.<br>
     * If the script hash is found in the compilation cache, compilation is skipped.
     *
     * @param script the script content
     * @param name the script name
     * @param silent should all console output of the loading process be suppressed?
     * @param handleEnable should the script's enable procedures be called?
     * @return the script instance, null if an error occurred
     */
    @Nullable
    Object loadScript(@NotNull String script, @Nullable String name, boolean silent, boolean handleEnable);
}
