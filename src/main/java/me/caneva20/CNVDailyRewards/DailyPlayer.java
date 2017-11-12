package me.caneva20.CNVDailyRewards;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DailyPlayer {
    private Player player;
    private String lastJoinDate;
    private int cumulativePoints;
    private int consecutivePoints;

    private List<Integer> cumulativesClaimed = new ArrayList<>();
    private List<Integer> consecutivesClaimed = new ArrayList<>();

    public DailyPlayer(Player player, String lastJoinDate, int cumulativePoints, int consecutivePoints, String cumulativesClaimed, String consecutivesClaimed) {
        this.player = player;
        this.lastJoinDate = lastJoinDate;
        this.cumulativePoints = cumulativePoints;
        this.consecutivePoints = consecutivePoints;

        String[] split = cumulativesClaimed.split(",");

        for (String s : split) {
            try {
                this.cumulativesClaimed.add(Integer.parseInt(s));
            } catch (NumberFormatException ignored) {}
        }

        split = consecutivesClaimed.split(",");

        for (String s : split) {
            try {
                this.consecutivesClaimed.add(Integer.parseInt(s));
            } catch (NumberFormatException ignored) {}
        }
    }

    public Player getPlayer() {
        return player;
    }

    public String getLastJoinDate() {
        return lastJoinDate;
    }

    public int getCumulativePoints() {
        return cumulativePoints;
    }

    public int getConsecutivePoints() {
        return consecutivePoints;
    }

    public String getCumulativeClaimed() {
        StringBuilder builder = new StringBuilder();

        for (Integer val : cumulativesClaimed) {
            builder.append(val).append(",");
        }

        return builder.toString();
    }

    public String getConsecutiveClaimed() {
        StringBuilder builder = new StringBuilder();

        for (Integer val : consecutivesClaimed) {
            builder.append(val).append(",");
        }

        return builder.toString();
    }

    public void setLastJoinDate(String lastJoinDate) {
        this.lastJoinDate = lastJoinDate;
    }

    public void addConsecutivePoint () {
//        CNVDailyRewards.getMainLogger().info(player, "You have been awarded with one consecutive point");
        consecutivePoints++;
    }

    public void resetConsecutivePoints () {
//        CNVDailyRewards.getMainLogger().info(player, "Your consecutive points have been reset");
        consecutivePoints = 1;
        consecutivesClaimed.clear();
    }

    public void addCumulativePoint () {
//        CNVDailyRewards.getMainLogger().info(player, "You have been awarded with one cumulative point");
        cumulativePoints++;
    }

    public void claimConsecutive(int id) {
        consecutivesClaimed.add(id);
    }

    public void claimCumulative(int id) {
        cumulativesClaimed.add(id);
    }

    public boolean isConsecutiveClaimed(int id) {
        return consecutivesClaimed.contains(id);
    }

    public boolean isCumulativeClaimed(int id) {
        return cumulativesClaimed.contains(id);
    }
}





















