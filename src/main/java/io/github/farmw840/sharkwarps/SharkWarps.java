
package io.github.farmw840.sharkwarps;

import org.bukkit.plugin.java.JavaPlugin;

public final class SharkWarps extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("================================");
        getLogger().info("       SHARK WARPS");
        getLogger().info("       Plugin iniciado!");
        getLogger().info("================================");
    }

    @Override
    public void onDisable() {
        getLogger().info("SharkWarps desligado!");
    }
}
