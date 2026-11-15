
package io.github.farmw840.sharkwarps;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MenuManager implements Listener {

    private final SharkWarps plugin;

    public MenuManager(SharkWarps plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void open(Player player) {
        MenuHolder holder = new MenuHolder();
        Inventory inventory = Bukkit.createInventory(
                holder, 54, Component.text("§3§lSHARK WARPS")
        );

        holder.inventory = inventory;

        inventory.setItem(4, item(
                Material.PRISMARINE_SHARD,
                "§b§lSHARK WARPS",
                "§7Encontre lojas de outros jogadores."
        ));

        inventory.setItem(45, item(
                Material.EMERALD,
                "§a§lCriar PWARP",
                "§7Use: §f/pw cria <nome>"
        ));

        inventory.setItem(47, item(
                Material.CHEST,
                "§e§lMinhas PWARPS",
                "§7Veja suas lojas cadastradas."
        ));

        inventory.setItem(49, item(
                Material.NETHER_STAR,
                "§b§lTodas as lojas",
                "§7Lista de lojas disponíveis."
        ));

        inventory.setItem(51, item(
                Material.BARRIER,
                "§c§lFechar",
                "§7Fechar este menu."
        ));

        List<String> warps = plugin.getWarpManager().getWarpNames();

        int slot = 10;

        for (String warp : warps) {
            if (slot >= 44) break;

            if (slot % 9 == 8) slot += 2;

            if (slot >= 44) break;

            ItemStack icon = item(
                    Material.CHEST,
                    "§b§l" + warp,
                    "§7Dono: §f" + ownerName(warp),
                    "§7Visitas: §e" +
                            plugin.getWarpManager().getVisits(warp),
                    " ",
                    "§aClique para visitar!"
            );

            inventory.setItem(slot, icon);
            slot++;
        }

        player.openInventory(inventory);
    }

    private String ownerName(String warp) {
        var owner = plugin.getWarpManager().getOwner(warp);

        if (owner == null) return "Desconhecido";

        String name = Bukkit.getOfflinePlayer(owner).getName();

        return name == null ? "Desconhecido" : name;
    }

    private ItemStack item(
            Material material,
            String name,
            String... lore
    ) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();

        meta.displayName(Component.text(name));

        List<Component> lines = new ArrayList<>();

        for (String line : lore) {
            lines.add(Component.text(line));
        }

        meta.lore(lines);
        stack.setItemMeta(meta);

        return stack;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof MenuHolder)) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        int slot = event.getRawSlot();

        if (slot < 0 || slot >= 54) return;

        if (slot == 51) {
            player.closeInventory();
            return;
        }

        if (slot == 45) {
            player.closeInventory();
            player.sendMessage("§aUse §f/pw cria <nome> §apara criar sua loja.");
            return;
        }

        if (slot == 47) {
            player.closeInventory();
            player.sendMessage("§bUse §f/pw lista §bpara consultar as lojas.");
            return;
        }

        if (slot == 49) {
            open(player);
            return;
        }

        ItemStack clicked = event.getCurrentItem();

        if (clicked == null || !clicked.hasItemMeta()) return;

        String name = clicked.getItemMeta()
                .getDisplayName()
                .replace("§b§l", "");

        if (!plugin.getWarpManager().getWarpNames().contains(name)) {
            return;
        }

        var destination = plugin.getWarpManager().getWarp(name);

        if (destination == null) {
            player.sendMessage("§cEssa loja não está disponível.");
            open(player);
            return;
        }

        player.closeInventory();

        plugin.getTeleportManager().teleport(
                player,
                destination,
                name
        );
    }

    private static class MenuHolder implements InventoryHolder {

        private Inventory inventory;

        @Override
        public Inventory getInventory() {
            return inventory;
        }
    }
}
