package me.caneva20.CNVDailyRewards.Config;

import me.caneva20.CNVCore.MySQL;
import me.caneva20.CNVDailyRewards.CNVDailyRewards;
import me.caneva20.CNVDailyRewards.DailyPlayer;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Storage {
    private CNVDailyRewards plugin;
    private MySQL mySQL;

    public Storage (CNVDailyRewards plugin) {
        this.plugin = plugin;

        Configuration configuration = CNVDailyRewards.getConfiguration();

        String host = configuration.getMySqlHost();
        String db = configuration.getMySqlDb();
        String user = configuration.getMySqlUser();
        String password = configuration.getMySqlPassword();
        int port = configuration.getMySqlPort();

        mySQL = new MySQL(host, db, user, password, port);

        if (!mySQL.isConnected()) {
            CNVDailyRewards.getMainLogger().errorConsole("SQL Connection failed, see the log for more information");
        } else {
            try {
                mySQL.update(SQLS.createTableCnvRanks);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void reload () {}

    public static void disable () {}

    public DailyPlayer loadPlayer(Player player) {
        try {
            String sql = "SELECT * FROM cnvdailyrewards WHERE player='{{PLAYER_NAME}}'"
                    .replace("{{PLAYER_NAME}}", player.getName().toLowerCase());

//            CNVDailyRewards.getMainLogger().debug("SQL(Load): <par>" + sql + "</par>");

            ResultSet query = mySQL.query(sql);

            if (query.next()) {
                String lastJoinDate = query.getString("last_join_date");
                int cumulativePoints = query.getInt("consecutive_points");
                int consecutivePoints = query.getInt("cumulative_points");
                String consecutiveClaimed = query.getString("consecutive_claimed");
                String cumulativeClaimed = query.getString("cumulative_claimed");

                return new DailyPlayer(player, lastJoinDate, cumulativePoints, consecutivePoints, cumulativeClaimed, consecutiveClaimed);
            } else {
                DailyPlayer newDailyPlayer = new DailyPlayer(player, "", 0, 0, "", "");

                if (createPlayer(newDailyPlayer)) {
                    return newDailyPlayer;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean savePlayer (DailyPlayer dailyPlayer) {
        try {
            String sql = "SELECT * FROM cnvdailyrewards WHERE player='{{PLAYER_NAME}}'"
                    .replace("{{PLAYER_NAME}}", dailyPlayer.getPlayer().getName().toLowerCase());

//            CNVDailyRewards.getMainLogger().debug("SQL(Select): <par>" + sql + "</par>");

            boolean next = mySQL.query(sql).next();

            if (!next) {
                createPlayer(dailyPlayer);
            } else {
                String sql2 = ("UPDATE cnvdailyrewards SET " +
                        "last_join_date='{{LAST_JOIN_DATE}}'," +
                        "consecutive_points='{{CONSECUTIVE_POINTS}}'," +
                        "cumulative_points='{{CUMULATIVE_POINTS}}'," +
                        "cumulative_claimed='{{CUMULATIVE_CLAIMED}}'," +
                        "consecutive_claimed='{{CONSECUTIVE_CLAIMED}}' " +
                        "WHERE player='{{PLAYER_NAME}}'")
                                .replace("{{PLAYER_NAME}}", dailyPlayer.getPlayer().getName().toLowerCase())
                                .replace("{{LAST_JOIN_DATE}}", dailyPlayer.getLastJoinDate())
                                .replace("{{CONSECUTIVE_POINTS}}", dailyPlayer.getConsecutivePoints() + "")
                                .replace("{{CUMULATIVE_POINTS}}", dailyPlayer.getCumulativePoints() + "")
                                .replace("{{CUMULATIVE_CLAIMED}}", dailyPlayer.getCumulativeClaimed() + "")
                                .replace("{{CONSECUTIVE_CLAIMED}}", dailyPlayer.getConsecutiveClaimed() + "");

//                CNVDailyRewards.getMainLogger().debug("SQL(Update): <par>" + sql2 + "</par>");

                mySQL.update(sql2);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean createPlayer(DailyPlayer dailyPlayer) {
        try {
            String sql = ("INSERT INTO cnvdailyrewards(player, last_join_date, consecutive_points, cumulative_points, cumulative_claimed, consecutive_claimed) " +
                    "VALUES ('{{PLAYER_NAME}}','{{LAST_JOIN_DATE}}','{{CONSECUTIVE_POINTS}}','{{CUMULATIVE_POINTS}}','{{CUMULATIVE_CLAIMED}}','{{CONSECUTIVE_CLAIMED}}')")
                            .replace("{{PLAYER_NAME}}", dailyPlayer.getPlayer().getName().toLowerCase())
                            .replace("{{LAST_JOIN_DATE}}", dailyPlayer.getLastJoinDate())
                            .replace("{{CONSECUTIVE_POINTS}}", dailyPlayer.getConsecutivePoints() + "")
                            .replace("{{CUMULATIVE_POINTS}}", dailyPlayer.getCumulativePoints() + "")
                            .replace("{{CUMULATIVE_CLAIMED}}", dailyPlayer.getCumulativeClaimed() + "")
                            .replace("{{CONSECUTIVE_CLAIMED}}", dailyPlayer.getConsecutiveClaimed() + "");

//            CNVDailyRewards.getMainLogger().debug("SQL(Create): <par>" + sql + "</par>");

            mySQL.update(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
