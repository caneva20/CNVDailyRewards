package me.caneva20.CNVDailyRewards.Config;

public class SQLS {
    private SQLS() {}

    public static String createTableCnvRanks =
            "CREATE TABLE IF NOT EXISTS cnvdailyrewards(" +
                    "`id` int(255) NOT NULL AUTO_INCREMENT," +
                    "`player` VARCHAR(255) NOT NULL," +
                    "`last_join_date` VARCHAR(255) NOT NULL," +
                    "`consecutive_points` INT NOT NULL," +
                    "`cumulative_points` INT NOT NULL," +
                    "`consecutive_claimed` VARCHAR(255) NOT NULL," +
                    "`cumulative_claimed` VARCHAR(255) NOT NULL," +
                    "PRIMARY KEY `id` (`id`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
}
