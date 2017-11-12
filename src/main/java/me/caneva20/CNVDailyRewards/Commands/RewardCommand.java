package me.caneva20.CNVDailyRewards.Commands;

import me.caneva20.CNVCore.Command.CommandBase;
import me.caneva20.CNVCore.Command.ICommand;
import me.caneva20.CNVDailyRewards.CNVDailyRewards;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class RewardCommand extends CommandBase implements ICommand {
    private String command = "";
    private String permission = "cnv.dailyrewards.commands.reward";
    private String usage = "/reward";
    private boolean onlyPlayers = true;
    private String description = "Opens the reward page";

    public RewardCommand() {
    }

    public String getCommand() {
        return this.command;
    }

    public String getPermission() {
        return this.permission;
    }

    public boolean getOnlyPlayers() {
        return this.onlyPlayers;
    }

    public String getUsage() {
        return this.usage;
    }

    public List<String> getAlias() {
        return new ArrayList<>();
    }

    public String getDescription() {
        return this.description;
    }

    public void onCommand(CommandSender sender, String[] args, JavaPlugin plugin) {
        CNVDailyRewards.getRewardManager().rewardPlayer(((Player) sender));
    }
}
