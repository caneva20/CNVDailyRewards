package me.caneva20.CNVDailyRewards.Listeners;

import me.caneva20.CNVDailyRewards.CNVDailyRewards;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    @EventHandler
    private void onPlayerJoin (PlayerJoinEvent e) {
        CNVDailyRewards.getRewardManager().addPlayer(e.getPlayer());
    }

//    @EventHandler
//    private void onPlayerQuit (PlayerQuitEvent e) {
//        CNVDailyRewards.getRewardManager().removePlayer(e.getPlayer());
//    }
//
//    @EventHandler
//    private void onPlayerKick(PlayerKickEvent e) {
//
//    }
}
