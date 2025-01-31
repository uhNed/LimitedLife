package me.rowyourboat.limitedlife.scoreboard;

import me.rowyourboat.limitedlife.LimitedLife;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class TeamHandler {

    private final JavaPlugin plugin;
    private final Scoreboard scoreboard;

    private final Team darkGreenName;
    private final Team greenName;
    private final Team yellowName;
    private final Team redName;
    private final Team grayName;

    public TeamHandler() {
        this.plugin = LimitedLife.plugin;
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        if (scoreboardManager == null) plugin.getLogger().severe("Unable to locate the scoreboard manager! Have you initialized a world yet? Please restart the server to fix this.");
        scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        scoreboard.getTeams().forEach(Team::unregister);

        darkGreenName = scoreboard.registerNewTeam("dark_green");
        darkGreenName.setCanSeeFriendlyInvisibles(false);
        darkGreenName.setColor(ChatColor.DARK_GREEN);

        greenName = scoreboard.registerNewTeam("green");
        greenName.setCanSeeFriendlyInvisibles(false);
        greenName.setColor(ChatColor.GREEN);

        yellowName = scoreboard.registerNewTeam("yellow");
        yellowName.setCanSeeFriendlyInvisibles(false);
        yellowName.setColor(ChatColor.YELLOW);

        redName = scoreboard.registerNewTeam("red");
        redName.setCanSeeFriendlyInvisibles(false);
        redName.setColor(ChatColor.RED);

        grayName = scoreboard.registerNewTeam("gray");
        grayName.setCanSeeFriendlyInvisibles(false);
        grayName.setColor(ChatColor.GRAY);

        Bukkit.getOnlinePlayers().forEach(player -> changeTeamAndGamemodeAccordingly(player, LimitedLife.SaveHandler.getPlayerTimeLeft(player)));
    }

    public void changeTeamAndGamemodeAccordingly(Player player, long timeLeft) {
        if (timeLeft <= -1) {
            scoreboard.getTeams().forEach(team -> {
                if (team.hasEntry(player.getName()))
                    team.removeEntry(player.getName());
            });
            player.setGameMode(GameMode.ADVENTURE);
            LimitedLife.SaveHandler.removePlayerDeathMark(player);
        } else if (timeLeft == 0) {
            if (!grayName.hasEntry(player.getName())) {
                grayName.addEntry(player.getName());
                player.setGameMode(GameMode.SPECTATOR);
                if (!LimitedLife.SaveHandler.getMarkedAsDeadList().contains(player.getUniqueId().toString())) {
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        onlinePlayer.sendTitle(ChatColor.RED + player.getName(), "ran out of time!", 20, 100, 20);
                        if (plugin.getConfig().getBoolean("other.lightning-sound-on-final-death"))
                            onlinePlayer.playSound(onlinePlayer, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1, 1);
                    }
                }
                LimitedLife.SaveHandler.markPlayerAsDead(player);
            }
        } else if (timeLeft < plugin.getConfig().getInt("name-colour-thresholds.red-name")) {
            if (!redName.hasEntry(player.getName())) {
                redName.addEntry(player.getName());
                player.setGameMode(GameMode.SURVIVAL);
                LimitedLife.SaveHandler.removePlayerDeathMark(player);
            }
        } else if (timeLeft < plugin.getConfig().getInt("name-colour-thresholds.yellow-name")) {
            if (!yellowName.hasEntry(player.getName())) {
                yellowName.addEntry(player.getName());
                player.setGameMode(GameMode.SURVIVAL);
                LimitedLife.SaveHandler.removePlayerDeathMark(player);
            }
        } else if ((plugin.getConfig().getBoolean("name-colour-thresholds.dark-green-names") && timeLeft < plugin.getConfig().getInt("name-colour-thresholds.green-name")) || !plugin.getConfig().getBoolean("name-colour-thresholds.dark-green-names")) {
            if (!greenName.hasEntry(player.getName())) {
                greenName.addEntry(player.getName());
                player.setGameMode(GameMode.SURVIVAL);
                LimitedLife.SaveHandler.removePlayerDeathMark(player);
            }
        } else {
            if (!darkGreenName.hasEntry(player.getName())) {
                darkGreenName.addEntry(player.getName());
                player.setGameMode(GameMode.SURVIVAL);
                LimitedLife.SaveHandler.removePlayerDeathMark(player);
            }
        }
    }

    public void clearTeamMembers() {
        Bukkit.getOnlinePlayers().forEach(plr -> scoreboard.getTeams().forEach(team -> team.removeEntry(plr.getName())));
    }

}