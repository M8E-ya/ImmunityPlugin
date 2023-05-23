package com.gmail.perpltxed.immunity;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Immunity extends JavaPlugin {
    private List<Player> immunePlayers = new ArrayList<>();

    @Override
    public void onEnable() {
        // Plugin initialization
        getLogger().info("ImmunityPlugin has been enabled.");
    }

    @Override
    public void onDisable() {
        // Plugin cleanup
        getLogger().info("ImmunityPlugin has been disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("immunity")) {
            if (!sender.hasPermission("immunity.admin")) {
                sender.sendMessage("You do not have permission to use this command.");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null || !target.isOnline()) {
                sender.sendMessage("Player not found or is offline.");
                return true;
            }

            int duration;
            try {
                duration = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage("Invalid duration specified.");
                return true;
            }

            grantImmunity(target, duration);
            sender.sendMessage(target.getName() + " has been granted immunity for " + duration + " seconds.");
            return true;
        }
        return false;
    }

    private void grantImmunity(Player player, int duration) {
        immunePlayers.add(player);
        player.setInvulnerable(true); // Make the player invulnerable

        // Disable various types of damage and debuffs
        player.setHealth(player.getMaxHealth()); // Restore player's health
        player.setFoodLevel(20); // Restore player's food level
        player.setSaturation(20); // Restore player's saturation level
        player.setFireTicks(0); // Remove any fire ticks
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType())); // Remove all active potion effects

        new BukkitRunnable() {
            @Override
            public void run() {
                immunePlayers.remove(player);
                player.setInvulnerable(false); // Remove invulnerability
                player.sendMessage("Your immunity has expired.");
            }
        }.runTaskLater(this, duration * 20); // Convert seconds to ticks (20 ticks per second)
    }
}
