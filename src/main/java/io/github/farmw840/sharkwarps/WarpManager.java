
package io.github.farmw840.sharkwarps;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WarpManager {

    private final SharkWarps plugin;
    private final File file;
    private final YamlConfiguration config;

    public WarpManager(SharkWarps plugin) {
        this.plugin = plugin;

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        this.file = new File(plugin.getDataFolder(), "warps.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public boolean createWarp(Player player, String name) {

        String path = "warps." + name.toLowerCase();

        if (config.contains(path)) {
            return false;
        }

        Location loc = player.getLocation();

        config.set(path + ".owner", player.getUniqueId().toString());
        config.set(path + ".world", loc.getWorld().getName());
        config.set(path + ".x", loc.getX());
        config.set(path + ".y", loc.getY());
        config.set(path + ".z", loc.getZ());
        config.set(path + ".yaw", loc.getYaw());
        config.set(path + ".pitch", loc.getPitch());

        save();
        return true;
    }

    public boolean removeWarp(String name) {

        String path = "warps." + name.toLowerCase();

        if (!config.contains(path)) {
            return false;
        }

        config.set(path, null);
        save();
        return true;
    }

    public Location getWarp(String name) {

        String path = "warps." + name.toLowerCase();

        if (!config.contains(path)) {
            return null;
        }

        String worldName = config.getString(path + ".world");
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            return null;
        }

        return new Location(
                world,
                config.getDouble(path + ".x"),
                config.getDouble(path + ".y"),
                config.getDouble(path + ".z"),
                (float) config.getDouble(path + ".yaw"),
                (float) config.getDouble(path + ".pitch")
        );
    }

    public List<String> getWarpNames() {

        ConfigurationSection section =
                config.getConfigurationSection("warps");

        if (section == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(section.getKeys(false));
    }

    public UUID getOwner(String name) {

        String owner = config.getString(
                "warps." + name.toLowerCase() + ".owner"
        );

        if (owner == null) {
            return null;
        }

        try {
            return UUID.fromString(owner);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private void save() {
        try {
            config.save(file);
        } catch (IOException exception) {
            plugin.getLogger().severe(
                    "Não foi possível salvar as PWARPS!"
            );
            exception.printStackTrace();
        }
    }
}
