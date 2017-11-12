package me.caneva20.CNVDailyRewards.Rewards;

import java.util.ArrayList;
import java.util.List;

public class RewardSet {
    private double chance;
    private double probability;
    private List<String> actions = new ArrayList<>();

    public RewardSet(double chance, List<String> actions) {
        this.chance = chance;
        this.actions = actions;
    }

    public double getChance() {
        return chance;
    }

    public List<String> getActions() {
        return actions;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    @Override
    public String toString() {
        return "RewardSet{" +
                "chance=" + chance +
                ", probability=" + probability +
                ", actions=" + actions +
                '}';
    }
}
