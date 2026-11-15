
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
            sender.sendMessage("Este comando só pode ser usado no jogo.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.DARK_AQUA + "§m------------------------");
            player.sendMessage(ChatColor.AQUA + "       SHARK WARPS");
            player.sendMessage(ChatColor.GRAY + "/pw cria <nome>");
            player.sendMessage(ChatColor.GRAY + "/pw remove <nome>");
            player.sendMessage(ChatColor.GRAY + "/pw edita <nome>");
            player.sendMessage(ChatColor.GRAY + "/pw visita <nome>");
            player.sendMessage(ChatColor.GRAY + "/pw lista");
            player.sendMessage(ChatColor.GRAY + "/pw minhas");
            player.sendMessage(ChatColor.GRAY + "/pw favoritas");
            player.sendMessage(ChatColor.GRAY + "/pw visitadas");
            player.sendMessage(ChatColor.DARK_AQUA + "§m------------------------");
            return true;
        }

        switch (args[0].toLowerCase()) {

            case "cria" -> {
                player.sendMessage(ChatColor.YELLOW +
                        "Sistema de criação de PWARP em desenvolvimento!");
            }

            case "remove" -> {
                player.sendMessage(ChatColor.YELLOW +
                        "Sistema de remoção em desenvolvimento!");
            }

            case "edita" -> {
                player.sendMessage(ChatColor.YELLOW +
                        "Sistema de edição em desenvolvimento!");
            }

            case "visita" -> {
                player.sendMessage(ChatColor.YELLOW +
                        "Sistema de visitas em desenvolvimento!");
            }

            case "lista" -> {
                player.sendMessage(ChatColor.YELLOW +
                        "Lista de PWARPS em desenvolvimento!");
            }

            case "minhas" -> {
                player.sendMessage(ChatColor.YELLOW +
                        "Suas PWARPS aparecerão aqui!");
            }

            case "favoritas" -> {
                player.sendMessage(ChatColor.YELLOW +
                        "Suas PWARPS favoritas aparecerão aqui!");
            }

            case "visitadas" -> {
                player.sendMessage(ChatColor.YELLOW +
                        "Seu histórico aparecerá aqui!");
            }

            default -> player.sendMessage(
                    ChatColor.RED + "Comando desconhecido. Use /pw"
            );
        }

        return true;
    }
}
