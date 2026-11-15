package io.github.farmw840.sharkwarps;

import org.bukkit.plugin.java.JavaPlugin;

public final class SharkWarps extends JavaPlugin {

    private WarpManager warpManager;

    @Override
    public void onEnable() {

        getLogger().info("Iniciando SharkWarps...");

        warpManager = new WarpManager(this);

        if (getCommand("pw") != null) {
            getCommand("pw").setExecutor(new PwCommand(this));
        } else {
            getLogger().severe("Comando /pw não encontrado!");
        }

        getLogger().info("SharkWarps iniciado com sucesso!");
    }

    @Override
    public void onDisable() {
        getLogger().info("SharkWarps desligado!");
    }

    public WarpManager getWarpManager() {
        return warpManager;
    }
}
