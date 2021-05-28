package com.gmail.excel8392.projectilemanipulation.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import com.gmail.excel8392.projectilemanipulation.entity.projectile.ManipulatedArrow;
import com.gmail.excel8392.projectilemanipulation.entity.projectile.ManipulatedEgg;
import com.gmail.excel8392.projectilemanipulation.entity.projectile.ManipulatedEnderpearl;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Huge parameter dump for testing and tweaking and demos
 */
@CommandAlias("projectilemanipulation|pm|projm")
public class ProjectileManipulationCommand extends BaseCommand {

    @Subcommand("enderpearlSpeed")
    public void onEnderpearlSpeed(CommandSender sender, String[] args) {
        if (!checkArgsHasDouble(args)) {
            sender.sendMessage(ChatColor.GREEN + "Enderpearl speed multiplier: " + ManipulatedEnderpearl.speedMultiplier);
        } else {
            ManipulatedEnderpearl.speedMultiplier = Double.parseDouble(args[0]);
            sender.sendMessage(ChatColor.GREEN + "Set enderpearl speed multiplier");
        }
    }

    @Subcommand("enderpearlRange")
    public void onEnderpearlRange(CommandSender sender, String[] args) {
        if (!checkArgsHasDouble(args)) {
            sender.sendMessage(ChatColor.GREEN + "Enderpearl range: " + ManipulatedEnderpearl.range);
        } else {
            ManipulatedEnderpearl.range = Double.parseDouble(args[0]);
            sender.sendMessage(ChatColor.GREEN + "Set enderpearl range");
        }
    }

    @Subcommand("enderpearlCorrection")
    public void onEnderpearlCorrection(CommandSender sender, String[] args) {
        if (!checkArgsHasDouble(args)) {
            sender.sendMessage(ChatColor.GREEN + "Enderpearl correction multiplier: " + ManipulatedEnderpearl.correction);
        } else {
            ManipulatedEnderpearl.correction = Double.parseDouble(args[0]);
            sender.sendMessage(ChatColor.GREEN + "Set enderpearl correction multiplier");
        }
    }

    @Subcommand("eggRange")
    public void onEggRange(CommandSender sender, String[] args) {
        if (!checkArgsHasDouble(args)) {
            sender.sendMessage(ChatColor.GREEN + "Egg range: " + ManipulatedEgg.range);
        } else {
            ManipulatedEgg.range = Double.parseDouble(args[0]);
            sender.sendMessage(ChatColor.GREEN + "Set egg range");
        }
    }

    @Subcommand("eggCorrection")
    public void onEggCorrection(CommandSender sender, String[] args) {
        if (!checkArgsHasDouble(args)) {
            sender.sendMessage(ChatColor.GREEN + "Egg correction multiplier: " + ManipulatedEgg.correction);
        } else {
            ManipulatedEgg.correction = Double.parseDouble(args[0]);
            sender.sendMessage(ChatColor.GREEN + "Set egg correction multiplier");
        }
    }

    @Subcommand("eggGravity")
    public void onEggGravity(CommandSender sender, String[] args) {
        if (!checkArgsHasDouble(args)) {
            sender.sendMessage(ChatColor.GREEN + "Egg gravity per tick: " + ManipulatedEgg.gravity);
        } else {
            ManipulatedEgg.gravity = Double.parseDouble(args[0]);
            sender.sendMessage(ChatColor.GREEN + "Set egg gravity per tick");
        }
    }

    @Subcommand("arrowMaxAngle")
    public void onArrowMaxAngle(CommandSender sender, String[] args) {
        if (!checkArgsHasDouble(args)) {
            sender.sendMessage(ChatColor.GREEN + "Arrow max angle: " + Math.toDegrees(ManipulatedArrow.maxAngle));
        } else {
            ManipulatedArrow.maxAngle = Math.toRadians(Double.parseDouble(args[0]));
            sender.sendMessage(ChatColor.GREEN + "Set arrow max angle");
        }
    }

    @Subcommand("arrowMaxAngleTime")
    public void onArrowMaxAngleTime(CommandSender sender, String[] args) {
        if (!checkArgsHasInt(args)) {
            sender.sendMessage(ChatColor.GREEN + "Arrow max angle time (ticks): " + ManipulatedArrow.maxAngleTime);
        } else {
            ManipulatedArrow.maxAngleTime = Integer.parseInt(args[0]);
            sender.sendMessage(ChatColor.GREEN + "Set arrow max angle time (ticks)");
        }
    }

    @Subcommand("arrowLife")
    public void onArrowLife(CommandSender sender, String[] args) {
        if (!checkArgsHasInt(args)) {
            sender.sendMessage(ChatColor.GREEN + "Arrow life time (ticks): " + ManipulatedArrow.maxTimeAlive);
        } else {
            ManipulatedArrow.maxTimeAlive = Integer.parseInt(args[0]);
            sender.sendMessage(ChatColor.GREEN + "Set arrow life time (ticks)");
        }
    }

    @Subcommand("arrowFollowTime")
    public void onArrowFollowTime(CommandSender sender, String[] args) {
        if (!checkArgsHasInt(args)) {
            sender.sendMessage(ChatColor.GREEN + "Arrow follow time (millis): " + ManipulatedArrow.maxFollowTime);
        } else {
            ManipulatedArrow.maxFollowTime = Integer.parseInt(args[0]);
            sender.sendMessage(ChatColor.GREEN + "Set arrow follow time (millis)");
        }
    }

    @Subcommand("arrowSpeed")
    public void onArrowSpeed(CommandSender sender, String[] args) {
        if (!checkArgsHasDouble(args)) {
            sender.sendMessage(ChatColor.GREEN + "Arrow speed multiplier: " + ManipulatedArrow.speedMultiplier);
        } else {
            ManipulatedArrow.speedMultiplier = Double.parseDouble(args[0]);
            sender.sendMessage(ChatColor.GREEN + "Set arrow speed multiplier");
        }
    }

    @Subcommand("arrowRange")
    public void onArrowRange(CommandSender sender, String[] args) {
        if (!checkArgsHasDouble(args)) {
            sender.sendMessage(ChatColor.GREEN + "Arrow range: " + ManipulatedArrow.range);
        } else {
            ManipulatedArrow.range = Double.parseDouble(args[0]);
            sender.sendMessage(ChatColor.GREEN + "Set arrow range");
        }
    }

    @Subcommand("arrowCorrection")
    public void onArrowCorrection(CommandSender sender, String[] args) {
        if (!checkArgsHasDouble(args)) {
            sender.sendMessage(ChatColor.GREEN + "Arrow correction multiplier: " + ManipulatedArrow.correction);
        } else {
            ManipulatedArrow.correction = Double.parseDouble(args[0]);
            sender.sendMessage(ChatColor.GREEN + "Set arrow correction multiplier");
        }
    }

    private static boolean checkArgsHasDouble(String[] args) {
        if (args.length < 1) return false;
        try {
            Double.parseDouble(args[0]);
        } catch (NumberFormatException exception) {
            return false;
        }
        return true;
    }

    private static boolean checkArgsHasInt(String[] args) {
        if (args.length < 1) return false;
        try {
            Integer.parseInt(args[0]);
        } catch (NumberFormatException exception) {
            return false;
        }
        return true;
    }

}