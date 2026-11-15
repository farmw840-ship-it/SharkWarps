package io.github.farmw840.sharkwarps;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class MenuManager implements Listener {

    private final SharkWarps plugin;
    private final Random random = new Random();

    public enum Category {
        ALL("Todas as PWarps"),
        MINE("Minhas PWarps"),
        VISITED("PWarps Visitadas"),
        FAVORITES("PWarps Favoritas");

        final String title;
        Category(String title) { this.title = title; }
    }

    public MenuManager(SharkWarps plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void open(Player player) {
        MainMenuHolder holder = new MainMenuHolder();
        Inventory inv = Bukkit.createInventory(holder, 45, Component.text("§8PWarps dos jogadores"));
        holder.inventory = inv;

        // Linha 2 (Slots 11 a 15): Categorias principais
        inv.setItem(11, createItem(
                Material.FLOWER_BANNER_PATTERN,
                "§6§lTODAS AS PWARPS",
                "§7Explore todas as PWarps disponíveis.",
                "§7Encontre novos lugares para visitar!",
                "",
                "§6CLIQUE §fpara visualizar."
        ));

        inv.setItem(12, createItem(
                Material.MOJANG_BANNER_PATTERN,
                "§9§lMINHAS PWARPS",
                "§7Gerencie todas as suas PWarps",
                "",
                "§9CLIQUE §fpara acessar."
        ));

        inv.setItem(13, createItem(
                Material.ENDER_EYE,
                "§a§lPWARPS VISITADAS",
                "§7Veja todas as PWarps que você já visitou.",
                "",
                "§aCLIQUE §fpara visualizar."
        ));

        inv.setItem(14, createItem(
                Material.NETHER_STAR,
                "§b§lPWARP FAVORITAS",
                "§7Veja suas pwarps favoritas.",
                "",
                "§bCLIQUE §fpara visualizar."
        ));

        inv.setItem(15, createItem(
                Material.WRITABLE_BOOK,
                "§6§lPWARP AJUDA",
                "§7Conheça os principais comandos do sistema.",
                "",
                "§6CLIQUE §fpara ver todos os comandos."
        ));

        // Linha 4 (Slots 29, 31, 33): Destaque, Aleatória e Perfil
        String topWarp = getTopWarp();
        if (topWarp != null) {
            inv.setItem(29, createItem(
                    Material.SUNFLOWER,
                    "§e§lPWARP EM DESTAQUE",
                    "§7Warp mais visitada: §f" + topWarp,
                    "§7Dono: §f" + ownerName(topWarp),
                    "§7Visitas: §e" + plugin.getWarpManager().getVisits(topWarp),
                    "",
                    "§eCLIQUE §fpara teleportar."
            ));
        } else {
            inv.setItem(29, createItem(
                    Material.SUNFLOWER,
                    "§e§lPWARP EM DESTAQUE",
                    "§7Nenhuma PWarp criada ainda.",
                    "",
                    "§7Use §f/pw criar <nome> §7para começar!"
            ));
        }

        inv.setItem(31, createItem(
                Material.REDSTONE_BLOCK,
                "§c§lPWARP ALEATÓRIA",
                "§7Explore uma PWarp aleatória do servidor!",
                "",
                "§cCLIQUE §fpara visitar."
        ));

        int minhasCount = plugin.getWarpManager().getPlayerWarps(player.getUniqueId()).size();
        int favCount = plugin.getWarpManager().getFavorites(player.getUniqueId()).size();
        inv.setItem(33, createPlayerHead(
                player,
                "§e§lSEU PERFIL",
                "§7Suas PWarps: §f" + minhasCount,
                "§7Favoritas: §f" + favCount,
                "",
                "§7Use §f/pw criar <nome> §7para criar uma."
        ));

        player.openInventory(inv);
    }

    public void openCategory(Player player, Category category, int page) {
        List<String> warps = switch (category) {
            case ALL -> plugin.getWarpManager().getWarpNames();
            case MINE -> plugin.getWarpManager().getPlayerWarps(player.getUniqueId());
            case VISITED -> plugin.getWarpManager().getVisited(player.getUniqueId());
            case FAVORITES -> plugin.getWarpManager().getFavorites(player.getUniqueId());
        };

        CategoryMenuHolder holder = new CategoryMenuHolder(category, page);
        Inventory inv = Bukkit.createInventory(holder, 54, Component.text("§8" + category.title));
        holder.inventory = inv;

        int perPage = 28;
        int totalPages = Math.max(1, (int) Math.ceil((double) warps.size() / perPage));
        int currentPage = Math.max(0, Math.min(page, totalPages - 1));
        holder.page = currentPage;

        int start = currentPage * perPage;
        int end = Math.min(start + perPage, warps.size());

        int slot = 10;
        for (int i = start; i < end; i++) {
            if (slot % 9 == 8) slot += 2;
            String warp = warps.get(i);
            boolean fav = plugin.getWarpManager().isFavorite(player.getUniqueId(), warp);

            inv.setItem(slot, createItem(
                    fav ? Material.NETHER_STAR : Material.ENDER_PEARL,
                    "§b§l" + warp + (fav ? " §e★" : ""),
                    "§7Dono: §f" + ownerName(warp),
                    "§7Visitas: §e" + plugin.getWarpManager().getVisits(warp),
                    "",
                    "§aClique Esquerdo §fpara teleportar.",
                    fav ? "§cClique Direito §fpara desfavoritar." : "§eClique Direito §fpara favoritar."
            ));
            slot++;
        }

        // Barra de navegação inferior
        if (currentPage > 0) {
            inv.setItem(45, createItem(Material.ARROW, "§ePágina Anterior", "§7Ir para página " + currentPage));
        }

        inv.setItem(49, createItem(Material.BARRIER, "§c§lVOLTAR", "§7Voltar ao menu principal."));

        if (currentPage + 1 < totalPages) {
            inv.setItem(53, createItem(Material.ARROW, "§ePróxima Página", "§7Ir para página " + (currentPage + 2)));
        }

        player.openInventory(inv);
    }

    private String getTopWarp() {
        return plugin.getWarpManager().getWarpNames().stream()
                .max(Comparator.comparingInt(w -> plugin.getWarpManager().getVisits(w)))
                .orElse(null);
    }

    private String ownerName(String warp) {
        var owner = plugin.getWarpManager().getOwner(warp);
        if (owner == null) return "Desconhecido";
        String name = Bukkit.getOfflinePlayer(owner).getName();
        return name == null ? "Desconhecido" : name;
    }

    private ItemStack createItem(Material material, String name, String... lore) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(name));
            List<Component> lines = new ArrayList<>();
            for (String line : lore) {
                lines.add(Component.text(line));
            }
            meta.lore(lines);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            stack.setItemMeta(meta);
        }
        return stack;
    }

    private ItemStack createPlayerHead(Player player, String name, String... lore) {
        ItemStack stack = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(player);
            meta.displayName(Component.text(name));
            List<Component> lines = new ArrayList<>();
            for (String line : lore) {
                lines.add(Component.text(line));
            }
            meta.lore(lines);
            stack.setItemMeta(meta);
        }
        return stack;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        InventoryHolder holder = event.getInventory().getHolder();

        if (holder instanceof MainMenuHolder) {
            event.setCancelled(true);
            int slot = event.getRawSlot();
            if (slot < 0 || slot >= 45) return;

            switch (slot) {
                case 11 -> openCategory(player, Category.ALL, 0);
                case 12 -> openCategory(player, Category.MINE, 0);
                case 13 -> openCategory(player, Category.VISITED, 0);
                case 14 -> openCategory(player, Category.FAVORITES, 0);
                case 15 -> {
                    player.closeInventory();
                    player.sendMessage("§6§l=== AJUDA SHARKWARPS ===");
                    player.sendMessage("§e/pw §7- Abre o menu principal.");
                    player.sendMessage("§e/pw criar <nome> §7- Cria uma PWarp na sua posição.");
                    player.sendMessage("§e/pw deletar <nome> §7- Remove uma PWarp sua.");
                    player.sendMessage("§e/pw <nome> §7- Teleporta direto para uma PWarp.");
                }
                case 29 -> {
                    String top = getTopWarp();
                    if (top != null) {
                        var loc = plugin.getWarpManager().getWarp(top);
                        if (loc != null) {
                            player.closeInventory();
                            plugin.getTeleportManager().teleport(player, loc, top);
                        }
                    }
                }
                case 31 -> {
                    List<String> all = plugin.getWarpManager().getWarpNames();
                    if (all.isEmpty()) {
                        player.sendMessage("§cAinda não existem PWarps criadas!");
                        return;
                    }
                    String chosen = all.get(random.nextInt(all.size()));
                    var loc = plugin.getWarpManager().getWarp(chosen);
                    if (loc != null) {
                        player.closeInventory();
                        plugin.getTeleportManager().teleport(player, loc, chosen);
                    }
                }
            }
        } else if (holder instanceof CategoryMenuHolder catHolder) {
            event.setCancelled(true);
            int slot = event.getRawSlot();
            if (slot < 0 || slot >= 54) return;

            if (slot == 49) {
                open(player);
                return;
            }
            if (slot == 45 && catHolder.page > 0) {
                openCategory(player, catHolder.category, catHolder.page - 1);
                return;
            }
            if (slot == 53) {
                openCategory(player, catHolder.category, catHolder.page + 1);
                return;
            }

            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || !clicked.hasItemMeta()) return;

            String rawName = clicked.getItemMeta().getDisplayName()
                    .replace("§b§l", "")
                    .replace(" §e★", "");

            if (!plugin.getWarpManager().getWarpNames().contains(rawName)) return;

            if (event.getClick() == ClickType.RIGHT) {
                boolean added = plugin.getWarpManager().toggleFavorite(player.getUniqueId(), rawName);
                player.sendMessage(added
                        ? "§ePWarp §f" + rawName + " §eadicionada aos favoritos!"
                        : "§cPWarp §f" + rawName + " §cremovida dos favoritos.");
                openCategory(player, catHolder.category, catHolder.page);
                return;
            }

            var destination = plugin.getWarpManager().getWarp(rawName);
            if (destination == null) {
                player.sendMessage("§cEssa PWarp não está disponível.");
                openCategory(player, catHolder.category, catHolder.page);
                return;
            }

            player.closeInventory();
            plugin.getTeleportManager().teleport(player, destination, rawName);
        }
    }

    private static class MainMenuHolder implements InventoryHolder {
        private Inventory inventory;
        @Override
        public Inventory getInventory() { return inventory; }
    }

    private static class CategoryMenuHolder implements InventoryHolder {
        private Inventory inventory;
        private final Category category;
        private int page;

        CategoryMenuHolder(Category category, int page) {
            this.category = category;
            this.page = page;
        }

        @Override
        public Inventory getInventory() { return inventory; }
    }
}
