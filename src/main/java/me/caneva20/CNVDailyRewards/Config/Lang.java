package me.caneva20.CNVDailyRewards.Config;

import me.caneva20.CNVCore.CNVConfig;
import me.caneva20.CNVCore.CNVLogger;
import me.caneva20.CNVDailyRewards.CNVDailyRewards;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Lang {
    private CNVDailyRewards plugin;
    private CNVLogger logger;
    private CNVConfig language;

    public Lang (CNVDailyRewards plugin) {
        this.plugin = plugin;

        logger = CNVDailyRewards.getMainLogger();

        language = new CNVConfig(plugin, "Language", "Language");
    }

    public void disable () {}

    public void reload () {
        language.reloadCustomConfig();
    }

    public void sendYouCantClaimThisReward (Player player) {
        String msg = getYouCanClaimThisReward();

        logger.error(player, msg);
    }

    public void sendRewardClaimed (Player player) {
        String msg = getRewardClaimed();

        logger.success(player, msg);
    }

    public void sendPluginReload(CommandSender sender) {
        String msg = getPluginReloaded();

        logger.info(sender, msg);
    }

    public String getYouCanClaimThisReward () {
        return language.getString("YOU_CANT_CLAIM_THIS_REWARD");
    }

    public String getRewardClaimed () {
        return language.getString("REWARD_CLAIMED");
    }

    public String getPluginReloaded () {
        return language.getString("PLUGIN_RELOADED");
    }
}

























