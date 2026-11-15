
package io.github.farmw840.sharkwarps;

import org.bukkit.plugin.java.JavaPlugin;

public final class SharkWarps extends JavaPlugin {

    @Override
    public void onEnable() {

        getLogger().info("================================");
        getLogger().info("          SHARK WARPS");
        getLogger().info("================================");

        // Registrar comando /pw
        if (getCommand("pw") != null) {
            getCommand("pw").setExecutor(new PwCommand(this));
            getLogger().info("Comando /pw registrado!");
        } else {
            getLogger().severe("Comando /pw não encontrado no plugin.yml!");
        }

        getLogger().info("Plugin iniciado com sucesso!");
        getLogger().info("================================");
    }

    @Override
    public void onDisable() {
        getLogger().info("SharkWarps desligado!");
    }
}
