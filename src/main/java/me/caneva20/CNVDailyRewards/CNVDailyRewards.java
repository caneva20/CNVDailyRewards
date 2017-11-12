package me.caneva20.CNVDailyRewards;

import me.caneva20.CNVCore.CNVLogger;
import me.caneva20.CNVCore.Command.CNVCommands;
import me.caneva20.CNVDailyRewards.Commands.InfoCommand;
import me.caneva20.CNVDailyRewards.Commands.ReloadCommand;
import me.caneva20.CNVDailyRewards.Commands.RewardCommand;
import me.caneva20.CNVDailyRewards.Config.Configuration;
import me.caneva20.CNVDailyRewards.Config.Lang;
import me.caneva20.CNVDailyRewards.Config.Storage;
import me.caneva20.CNVDailyRewards.Listeners.PlayerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class CNVDailyRewards extends JavaPlugin {
    private static CNVDailyRewards instance;

    private static CNVLogger logger;
    private static Configuration configuration;
    private static Storage storage;
    private static Lang lang;

    //Managers
    private static RewardManager rewardManager;

    private CNVCommands rewardCommand;
    private CNVCommands mainCommand;

    @Override
    public void onLoad() {
        logger = new CNVLogger(this, true);
    }

    @Override
    public void onEnable () {
        logger.setLoggerTag("&f[&5CNV&6DailyRewards&f] ");

        logger.infoConsole("Initializing setup.");
        //Initialize things here

        if (!initCore()) {
            return;
        }

        configuration = new Configuration(this);
        storage = new Storage(this);
        rewardManager = new RewardManager(this);
        lang = new Lang(this);

        registerListeners();
        registerCommands();

        instance = this;

        logger.infoConsole("Enabled");
    }

    @Override
    public void onDisable () {
        rewardManager.disable();
        lang.disable();
        configuration.disable();

        logger.infoConsole("Disabled");
    }

    private void registerListeners () {
        logger.infoConsole("Registering listeners");

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        logger.infoConsole("All listeners registered");
    }

    private void registerCommands () {
        logger.infoConsole("Registering commands");
        rewardCommand = new CNVCommands(this, logger, "reward");
        mainCommand = new CNVCommands(this, logger, "cnvdailyrewards");

        rewardCommand.registerCommand(new RewardCommand());
        rewardCommand.registerCommand(new ReloadCommand());

        mainCommand.registerCommand(new InfoCommand());

        logger.infoConsole("All commands registered");
    }

    private boolean initCore () {
        Plugin core = getServer().getPluginManager().getPlugin("CNVCore");

        if (core == null) {
            logger.errorConsole("<par>CNVCore</par> not found. Disabling.");

            getServer().getPluginManager().disablePlugin(this);
        }

        return core != null;
    }

    public static CNVLogger getMainLogger() {
        return logger;
    }

    public static Configuration getConfiguration() {
        return configuration;
    }

    public static Storage getStorage() {
        return storage;
    }

    public static RewardManager getRewardManager() {
        return rewardManager;
    }

    public static Lang getLang() {
        return lang;
    }

    public static CNVDailyRewards get() {
        return instance;
    }
}
