package me.caneva20.CNVDailyRewards.Config;

import me.caneva20.CNVCore.CNVConfig;
import me.caneva20.CNVDailyRewards.CNVDailyRewards;

public class Configuration {
    private CNVDailyRewards plugin;
    private CNVConfig config;

    public Configuration (CNVDailyRewards plugin) {
        this.plugin = plugin;
        config = new CNVConfig(plugin, "Config", "Config");
    }

    public void reload () {
        config.reloadCustomConfig();
    }

    public void disable () {}

    public String getMySqlHost () {
        return config.getString("MYSQL.HOST");
    }

    public String getMySqlDb () {
        return config.getString("MYSQL.DB");
    }

    public String getMySqlUser () {
        return config.getString("MYSQL.USER");
    }

    public String getMySqlPassword () {
        return config.getString("MYSQL.PASSWORD");
    }

    public int getMySqlPort () {
        return config.getInt("MYSQL.PORT");
    }

    public long getDbLoadDelay() {
        return config.getLong("DB_LOAD_DELAY");
    }

    public long getDbSaveDelay() {
        return config.getLong("DB_SAVE_DELAY");
    }

    public boolean isConsecutiveEnabled() {
        return config.getBoolean("REWARDS.CONSECUTIVE.ENABLED");
    }

    public String getConsecutivePermission() {
        return config.getString("REWARDS.CONSECUTIVE.PERMISSION");
    }

    public boolean isCumulativeEnabled() {
        return config.getBoolean("REWARDS.CUMULATIVE.ENABLED");
    }

    public String getCumulativePermission() {
        return config.getString("REWARDS.CUMULATIVE.PERMISSION");
    }

    public int getMainMenuConsecutiveSlotId() {
        return config.getInt("REWARDS.MAIN_MENU.CONSECUTIVE.SLOT_ID");
    }

    public String getMainMenuConsecutiveMenuName() {
        return config.getString("REWARDS.CONSECUTIVE.MENU_NAME");
    }

    public int getMainMenuCumulativeSlotId() {
        return config.getInt("REWARDS.MAIN_MENU.CUMULATIVE.SLOT_ID");
    }

    public String getMainMenuCumulativeMenuName() {
        return config.getString("REWARDS.CUMULATIVE.MENU_NAME");
    }

}


























