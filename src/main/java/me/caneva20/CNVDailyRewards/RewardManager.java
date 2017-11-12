package me.caneva20.CNVDailyRewards;

import me.caneva20.CNVCore.CNVConfig;
import me.caneva20.CNVCore.CNVLogger;
import me.caneva20.CNVCore.CNVMenus.Menu;
import me.caneva20.CNVCore.CNVMenus.MenuBuilder;
import me.caneva20.CNVCore.Util.TimeUtil;
import me.caneva20.CNVDailyRewards.Config.Configuration;
import me.caneva20.CNVDailyRewards.Config.Storage;
import me.caneva20.CNVDailyRewards.Rewards.Reward;
import me.caneva20.CNVDailyRewards.Rewards.RewardSet;
import me.caneva20.CNVDailyRewards.Utils.DailyRunnable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@SuppressWarnings("UnusedReturnValue")
public class RewardManager {
    private CNVDailyRewards plugin;
    private Storage storage;
    private CNVLogger logger;
    private MenuBuilder menuBuilder;

    private Set<DailyPlayer> players = new HashSet<>();
    private Set<DailyPlayer> playersToSave = new HashSet<>();
    private Map<Player, DailyRunnable> playersToLoad = new HashMap<>();

    private List<Reward> consecutiveRewards = new ArrayList<>();
    private List<Reward> cumulativeRewards = new ArrayList<>();

    private Map<String, Integer> consecutiveMultipliers = new HashMap<>();
    private Map<String, Integer> cumulativeMultipliers = new HashMap<>();

    private CNVConfig rewardsConfig;
    private CNVConfig config;

    //Tasks
    private BukkitTask loadTask;
    private BukkitTask saveTask;

    public RewardManager(CNVDailyRewards plugin) {
        this.plugin = plugin;
        storage = CNVDailyRewards.getStorage();
        logger = CNVDailyRewards.getMainLogger();
        menuBuilder = MenuBuilder.get(plugin);

        rewardsConfig = new CNVConfig(plugin, "Rewards");
        config = new CNVConfig(plugin, "Config");

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            addPlayer(player);
        }

        loadRewards();
        loadMultipliers();
        routines();
    }

    public void disable() {
        loadTask.cancel();
        saveTask.cancel();

        savePlayers();
    }

    public void reload () {
        rewardsConfig.reloadCustomConfig();
        menuBuilder.reload();
    }

    private void routines() {
        loadTask = new BukkitRunnable() {
            @Override
            public void run() {
                loadPlayers();
            }
        }.runTaskTimerAsynchronously(plugin, 0L, 20L);

        saveTask = new BukkitRunnable() {
            @Override
            public void run() {
                savePlayers();
            }
        }.runTaskTimerAsynchronously(plugin, 0L, 20L);
    }

    private void loadRewards() {
        CNVDailyRewards.getMainLogger().infoConsole("Loading rewards...");

        consecutiveRewards = loadRewardSection("CONSECUTIVE", true);
        cumulativeRewards = loadRewardSection("CUMULATIVE", false);

        CNVDailyRewards.getMainLogger().infoConsole("All rewards loaded");
    }

    private void loadMultipliers () {
        consecutiveMultipliers = loadMultiplierSection("CONSECUTIVE");
        cumulativeMultipliers = loadMultiplierSection("CUMULATIVE");
    }

    private List<Reward> loadRewardSection(String section, boolean isConsecutive) {
        CNVDailyRewards.getMainLogger().infoConsole("Loading " + section + " rewards...");
        List<Reward> rewards = new ArrayList<>();

        ArrayList<String> loadSection = null;
        try {
            loadSection = rewardsConfig.getSection(section);
        } catch (Exception ignored) {}

        if (loadSection != null) {
            int lastId = 0;
            for (String rewardSection : loadSection) {
                int pointsNeeded;
                try {
                    pointsNeeded = Integer.parseInt(rewardSection);
                } catch (NumberFormatException e) {
                    CNVDailyRewards.getMainLogger().errorConsole("<par>" + rewardSection + "</par> is not a valid number. Reward could not be loaded. Skipping...");
                    continue;
                }

                List<RewardSet> sets = new ArrayList<>();

                for (String setSection : rewardsConfig.getSection(section + "." + rewardSection + ".REWARD_SETS")) {
                    double chance = rewardsConfig.getDouble(section + "." + rewardSection + ".REWARD_SETS." + setSection + ".CHANCE");
                    List<String> actions = rewardsConfig.getStringList(section + "." + rewardSection + ".REWARD_SETS." + setSection + ".REWARDS");

                    sets.add(new RewardSet(chance, actions));
                }

                String name = rewardsConfig.getString(section + "." + rewardSection + ".MENU.NAME");
                String item = rewardsConfig.getString(section + "." + rewardSection + ".MENU.ITEM_ID");
                List<String> lore = rewardsConfig.getStringList(section + "." + rewardSection + ".MENU.ITEM_LORE");

                rewards.add(new Reward(lastId++, isConsecutive, pointsNeeded, name, item, lore, sets));
            }

            CNVDailyRewards.getMainLogger().infoConsole(section + " rewards loaded!");
        } else {
            CNVDailyRewards.getMainLogger().errorConsole(section + " rewards could not be found. Skipping...");
        }

        return rewards;
    }

    private Map<String, Integer> loadMultiplierSection(String section) {
        Map<String, Integer> multipliers = new HashMap<>();

        for (String multiplier : config.getSection("REWARDS." + section + ".MULTIPLIERS")) {
            String path = "REWARDS." + section + ".MULTIPLIERS." + multiplier + ".";

            int multiply = config.getInt(path + "MULTIPLY");
            String perm = config.getString(path + "PERMISSION");

            multipliers.put(perm, multiply);
        }

        return multipliers;
    }

    private void loadPlayers() {
        for (Player player : new ArrayList<>(playersToLoad.keySet())) {
            DailyPlayer dailyPlayer = storage.loadPlayer(player);

            playersToLoad.get(player).run(dailyPlayer);

            playersToLoad.remove(player);
        }
    }

    private void savePlayers() {
        for (DailyPlayer dailyPlayer : new ArrayList<>(playersToSave)) {
            storage.savePlayer(dailyPlayer);
            playersToSave.remove(dailyPlayer);

//            logger.debug("Player <par>" + dailyPlayer.getPlayer().getName() + "</par> successfully saved and removed");
        }
    }

    public void addPlayer(Player player) {
        playersToLoad.put(player, this::playerLoaded);
    }

    public void removePlayer(DailyPlayer player) {
        playersToSave.add(player);
        players.remove(player);
    }

    private void playerLoaded(DailyPlayer player) {
        if (player == null) {
            logger.warnConsole("A player could not be loaded");
            return;
        }

        players.add(player);
        updatePoints(player);
    }

    public void removePlayer(Player player) {
        for (DailyPlayer dailyPlayer : players) {
            if (dailyPlayer.getPlayer() == player) {
                removePlayer(dailyPlayer);
                break;
            }
        }
    }

    public void updatePoints(DailyPlayer player) {
        String lastJoinDate = player.getLastJoinDate();

        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT-3"));

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        // 02-11-2017
        String date = day + "-" + month + "-" + year;

        boolean rewarded = false;

        if (!lastJoinDate.equals(date) && "".equals(lastJoinDate)) {
            player.addConsecutivePoint();
            player.addCumulativePoint();

            rewarded = true;
        } else {
            String[] split = lastJoinDate.split("-");

            if (split.length == 3) {
                int lastDay = Integer.parseInt(split[0]);
                int lastMonth = Integer.parseInt(split[1]);
                int lastYear = Integer.parseInt(split[2]);

                long diff = TimeUtil.getDateDiff(LocalDate.of(lastYear, lastMonth, lastDay), LocalDate.of(year, month, day), ChronoUnit.DAYS);

                if (diff != 0) {
                    player.addCumulativePoint();
                    rewarded = true;

                    if (diff > 1) {
                        player.resetConsecutivePoints();
                    } else {
                        Reward lastReward = consecutiveRewards.get(consecutiveRewards.size() - 1);

                        if (player.isConsecutiveClaimed(lastReward.getId())) {
                            player.resetConsecutivePoints();
                        } else {
                            player.addConsecutivePoint();
                        }
                    }
                }
            } else {
                logger.errorConsole("Date was stored in wrong format. No reward given");
            }
        }

        if (rewarded) {
            player.setLastJoinDate(date);
            playersToSave.add(player);
        }
    }

    public DailyPlayer getDailyPlayer(Player player) {
        for (DailyPlayer dailyPlayer : players) {
            if (dailyPlayer.getPlayer() == player) {
                return dailyPlayer;
            }
        }

        return null;
    }

    public boolean rewardPlayer(Player player) {
        DailyPlayer dailyPlayer = getDailyPlayer(player);

        Configuration config = CNVDailyRewards.getConfiguration();

        if (config.isConsecutiveEnabled() && config.isCumulativeEnabled()
                && player.hasPermission(config.getConsecutivePermission()) && player.hasPermission(config.getCumulativePermission())) {
            //Create a chest with two options, one for each type of reward
            showRewardsMenu(dailyPlayer);
        } else if (config.isConsecutiveEnabled() && player.hasPermission(config.getConsecutivePermission())) {
            //Create a chest with all the consecutive rewards
            showConsecutiveMenu(dailyPlayer);
        } else if (config.isCumulativeEnabled() && player.hasPermission(config.getCumulativePermission())) {
            //Create a chest with all the cumulative rewards
            showCumulativeMenu(dailyPlayer);
        } else {
            return false;
        }

        return true;
    }

    public int getMultiplier (DailyPlayer player, boolean isConsecutive) {
        Map<String, Integer> multipliers = isConsecutive ? consecutiveMultipliers : cumulativeMultipliers;

        Player p = player.getPlayer();

        int multiplier = 1;

        for (String perm : multipliers.keySet()) {
            if (p.hasPermission(perm)) {
                if (multipliers.get(perm) > multiplier) {
                    multiplier = multipliers.get(perm);
                }
            }
        }

        return multiplier;
    }

    private void showRewardsMenu(DailyPlayer player) {
        Configuration c = CNVDailyRewards.getConfiguration();
        Menu menu = menuBuilder.createMenu("DailyRewards", 1);

        int mainMenuConsecutiveSlotId = c.getMainMenuConsecutiveSlotId();
        int mainMenuCumulativeSlotId = c.getMainMenuCumulativeSlotId();

        menu.addButton(mainMenuConsecutiveSlotId, () -> {
            menu.close(player.getPlayer());
            showConsecutiveMenu(player);
        });

        menu.addButton(mainMenuCumulativeSlotId, () -> {
            menu.close(player.getPlayer());
            showCumulativeMenu(player);
        });

        menu.open(player.getPlayer());
    }

    private void showConsecutiveMenu(DailyPlayer player) {
        int rows = consecutiveRewards.size() / 9 + 1;

        Menu menu = menuBuilder.createEmptyMenu(CNVDailyRewards.getConfiguration().getMainMenuConsecutiveMenuName(), rows);

        int size = consecutiveRewards.size() < 56 ? consecutiveRewards.size() : 56;

        for (int i = 0; i < size; i++) {
            Reward reward = consecutiveRewards.get(i);

            ItemStack item = menuBuilder.createItem(reward.getItem(), reward.getName(), reward.getLore());

            menu.addButton(item, i, () -> {
                if (reward.rewardPlayer(player)) {
                    playersToSave.add(player);

                    menu.close(player.getPlayer());
                    CNVDailyRewards.getLang().sendRewardClaimed(player.getPlayer());
                } else {
                    CNVDailyRewards.getLang().sendYouCantClaimThisReward(player.getPlayer());
                }
            });

            if (reward.canClaim(player)) {
                menu.highlightButton(i);
            }
        }

        menu.open(player.getPlayer());
    }

    private void showCumulativeMenu(DailyPlayer player) {
        int rows = cumulativeRewards.size() / 9 + 1;

        Menu menu = menuBuilder.createEmptyMenu(CNVDailyRewards.getConfiguration().getMainMenuCumulativeMenuName(), rows);

        int size = cumulativeRewards.size() < 56 ? cumulativeRewards.size() : 56;

        for (int i = 0; i < size; i++) {
            Reward reward = cumulativeRewards.get(i);

            ItemStack item = menuBuilder.createItem(reward.getItem(), reward.getName(), reward.getLore());

            menu.addButton(item, i, () -> {
                if (reward.rewardPlayer(player)) {
                    playersToSave.add(player);

                    menu.close(player.getPlayer());
                    CNVDailyRewards.getLang().sendRewardClaimed(player.getPlayer());
                } else {
                    CNVDailyRewards.getLang().sendYouCantClaimThisReward(player.getPlayer());
                }
            });

            if (reward.canClaim(player)) {
                menu.highlightButton(i);
            }
        }

        menu.open(player.getPlayer());
    }
}



















