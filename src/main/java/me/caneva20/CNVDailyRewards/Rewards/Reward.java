package me.caneva20.CNVDailyRewards.Rewards;

import me.caneva20.CNVCore.CNVActions.ActionProcessor;
import me.caneva20.CNVCore.Util.KeyValuePair;
import me.caneva20.CNVCore.Util.Random;
import me.caneva20.CNVDailyRewards.CNVDailyRewards;
import me.caneva20.CNVDailyRewards.DailyPlayer;

import java.util.ArrayList;
import java.util.List;

public class Reward {
    private int id;
    private boolean isConsecutive;
    private int pointsNeeded;
    private String name;
    private String item;
    private List<String> lore;
    private List<RewardSet> rewards = new ArrayList<>();

    public Reward(int id, boolean isConsecutive, int pointsNeeded, String name, String item, List<String> lore, List<RewardSet> rewards) {
        this.id = id;
        this.isConsecutive = isConsecutive;
        this.pointsNeeded = pointsNeeded;
        this.name = name;
        this.item = item;
        this.lore = lore;
        this.rewards = rewards;

        updateChances();
    }

    private void updateChances() {
        double sum = 0;

        for (RewardSet reward : rewards) {
            sum += reward.getChance();
        }

        for (RewardSet reward : rewards) {
            double newChance = reward.getChance() / sum * 100;

            reward.setChance(newChance);
        }

        double prob = 0;

        for (RewardSet reward : rewards) {
            double rewardProb = reward.getChance();

            reward.setProbability(rewardProb + prob);

            prob += rewardProb;
        }
    }

    public boolean rewardPlayer(DailyPlayer player) {
        if (!canClaim(player)) {
            return false;
        }

        int multiplier = CNVDailyRewards.getRewardManager().getMultiplier(player, isConsecutive);

        for (int i = 0; i < multiplier; i++) {
            RewardSet reward = getReward();

            if (reward != null) {
                for (String action : reward.getActions()) {
                    ActionProcessor.get().processAction(action, new KeyValuePair<>("PLAYER", player.getPlayer()));
                }
            } else {
                CNVDailyRewards.getMainLogger().errorConsole("No reward could be selected!");
            }
        }

        if (isConsecutive) {
            player.claimConsecutive(id);
        } else {
            player.claimCumulative(id);
        }

        return true;
    }

    public boolean canClaim(DailyPlayer player) {
        return (
                !isConsecutive || !player.isConsecutiveClaimed(id))
                && (isConsecutive || !player.isCumulativeClaimed(id)
        ) && (
                !isConsecutive || player.getConsecutivePoints() >= pointsNeeded)
                && (isConsecutive || player.getCumulativePoints() >= pointsNeeded
        );
    }

    private RewardSet getReward() {
        double chance = Random.range(0D, 100D);

        for (RewardSet reward : rewards) {
            if (reward.getProbability() > chance) {
                return reward;
            }
        }

        return null;
    }

    public int getPointsNeeded() {
        return pointsNeeded;
    }

    public String getName() {
        return name;
    }

    public String getItem() {
        return item;
    }

    public List<String> getLore() {
        return lore;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Reward{" +
                "pointsNeeded=" + pointsNeeded +
                ", name='" + name + '\'' +
                ", rewards=" + rewards +
                '}';
    }
}
