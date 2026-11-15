
package io.github.farmw840.sharkwarps;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

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

                UUID owner =
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

                boolean removed =
                        plugin.getWarpManager().removeWarp(name);

                if (removed) {
                    player.sendMessage(ChatColor.GREEN +
                            "A PWARP " + name + " foi removida!");
                } else {
                    player.sendMessage(ChatColor.RED +
                            "Não foi possível remover essa PWARP!");
                }
            }

            case "visita" -> {

                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED +
                            "Use: /pw visita <nome>");
                    return true;
                }

                String name = args[1].toLowerCase();

                Location destination =
                        plugin.getWarpManager().getWarp(name);

                if (destination == null) {
                    player.sendMessage(ChatColor.RED +
                            "Essa PWARP não existe ou o mundo não está carregado!");
                    return true;
                }

                if (destination.getWorld().equals(player.getWorld())
                        && destination.distanceSquared(player.getLocation()) < 1) {
                    player.sendMessage(ChatColor.YELLOW +
                            "Você já está nesse local!");
                    return true;
                }

                plugin.getTeleportManager().teleport(
                        player,
                        destination,
                        name
                );
            }

            case "edita" -> {
                player.sendMessage(ChatColor.YELLOW +
                        "Sistema de edição em desenvolvimento.");
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
