package io.github.farmw840.sharkwarps;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PwCommand implements CommandExecutor {

    private final SharkWarps plugin;

    public PwCommand(SharkWarps plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Use este comando dentro do Minecraft.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.DARK_AQUA + "§m--------------------------");
            player.sendMessage(ChatColor.AQUA + "       SHARK WARPS");
            player.sendMessage(ChatColor.GRAY + "/pw cria <nome>");
            player.sendMessage(ChatColor.GRAY + "/pw remove <nome>");
            player.sendMessage(ChatColor.GRAY + "/pw edita <nome>");
            player.sendMessage(ChatColor.GRAY + "/pw visita <nome>");
            player.sendMessage(ChatColor.GRAY + "/pw lista");
            player.sendMessage(ChatColor.DARK_AQUA + "§m--------------------------");
            return true;
        }

        switch (args[0].toLowerCase()) {

            case "cria" -> {

                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED +
                            "Use: /pw cria <nome>");
                    return true;
                }

                String name = args[1].toLowerCase();

                if (!name.matches("[a-z0-9_-]{3,16}")) {
                    player.sendMessage(ChatColor.RED +
                            "O nome deve ter de 3 a 16 caracteres.");
                    return true;
                }

                boolean created =
                        plugin.getWarpManager().createWarp(player, name);

                if (created) {
                    player.sendMessage(ChatColor.GREEN +
                            "Sua PWARP " + name + " foi criada!");
                } else {
                    player.sendMessage(ChatColor.RED +
                            "Já existe uma PWARP com esse nome!");
                }
            }

            case "remove" -> {

    if (args.length < 2) {
        player.sendMessage(ChatColor.RED +
                "Use: /pw remove <nome>");
        return true;
    }

    String name = args[1].toLowerCase();

    if (!plugin.getWarpManager().getWarpNames().contains(name)) {
        player.sendMessage(ChatColor.RED +
                "Essa PWARP não existe!");
        return true;
    }

    java.util.UUID owner =
            plugin.getWarpManager().getOwner(name);

    boolean isOwner = owner != null &&
            owner.equals(player.getUniqueId());

    boolean isAdmin =
            player.hasPermission("sharkwarps.admin");

    if (!isOwner && !isAdmin) {
        player.sendMessage(ChatColor.RED +
                "Você não pode remover a PWARP de outro jogador!");
        return true;
    }

    plugin.getWarpManager().removeWarp(name);

    player.sendMessage(ChatColor.GREEN +
            "A PWARP " + name + " foi removida!");
            }

            case "edita" -> {
                player.sendMessage(ChatColor.YELLOW +
                        "Sistema de edição em desenvolvimento.");
            }

            case "visita" -> {
                player.sendMessage(ChatColor.YELLOW +
                        "Sistema de visitas em desenvolvimento.");
            }

            case "lista" -> {
                player.sendMessage(ChatColor.YELLOW +
                        "Sistema de listagem em desenvolvimento.");
            }

            default -> player.sendMessage(ChatColor.RED +
                    "Comando desconhecido. Use /pw");
        }

        return true;
    }
}

